package com.classreport.classreport.service;

import com.classreport.classreport.entity.UserEntity;
import com.classreport.classreport.model.request.UserRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.model.response.UserResponse;
import com.classreport.classreport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public ApiResponse register(UserRequest request) {
        try {
            // Emailin artıq mövcud olub olmadığını yoxla
            if (userRepository.existsByEmail(request.getEmail())) {
                ApiResponse response = new ApiResponse();
                response.setCode("400");
                response.setMessage("Bu email artıq istifadə olunub");
                return response;
            }

            UserEntity user = new UserEntity();
            user.setName(request.getSurname());
            user.setSurname(request.getSurname());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(request.getRole());

            UserEntity savedUser = userRepository.save(user);

            // UserResponse yarat (token ilə birlikdə)
            var jwtToken = jwtService.generateToken(savedUser);
            var userResponse = UserResponse.builder()
                    .id(savedUser.getId())
                    .name(savedUser.getName())
                    .surname(savedUser.getSurname())
                    .email(savedUser.getEmail())
                    .role(savedUser.getRole())
                    .token(jwtToken)
                    .build();

            return new ApiResponse(userResponse);

        } catch (Exception e) {
            ApiResponse response = new ApiResponse();
            response.setCode("500");
            response.setMessage("Qeydiyyat zamanı xəta baş verdi: " + e.getMessage());
            return response;
        }
    }

    public ApiResponse login(UserRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            Optional<UserEntity> userOptional = userRepository.findByEmail(request.getEmail());
            if (userOptional.isEmpty()) {
                ApiResponse response = new ApiResponse();
                response.setCode("404");
                response.setMessage("İstifadəçi tapılmadı");
                return response;
            }

            UserEntity user = userOptional.get();

            // UserResponse yarat (token ilə birlikdə)
            var jwtToken = jwtService.generateToken(user);
            var userResponse = UserResponse.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .surname(user.getSurname())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .token(jwtToken)
                    .build();

            return new ApiResponse(userResponse);

        } catch (BadCredentialsException e) {
            ApiResponse response = new ApiResponse();
            response.setCode("401");
            response.setMessage("Email və ya şifrə yanlışdır");
            return response;

        } catch (Exception e) {
            ApiResponse response = new ApiResponse();
            response.setCode("500");
            response.setMessage("Giriş zamanı xəta baş verdi: " + e.getMessage());
            return response;
        }
    }

    public ApiResponse refreshToken(String refreshToken) {
        try {
            if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
                ApiResponse response = new ApiResponse();
                response.setCode("400");
                response.setMessage("Yeniləmə tokeni təmin edilməyib");
                return response;
            }

            final String token = refreshToken.substring(7);
            final String userEmail = jwtService.extractUsername(token);

            if (userEmail != null) {
                Optional<UserEntity> userOptional = userRepository.findByEmail(userEmail);
                if (userOptional.isEmpty()) {
                    ApiResponse response = new ApiResponse();
                    response.setCode("404");
                    response.setMessage("İstifadəçi tapılmadı");
                    return response;
                }

                UserEntity user = userOptional.get();

                if (jwtService.isTokenValid(token, user)) {
                    var jwtToken = jwtService.generateToken(user);
                    var userResponse = UserResponse.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .surname(user.getSurname())
                            .email(user.getEmail())
                            .role(user.getRole())
                            .token(jwtToken)
                            .build();

                    return new ApiResponse(userResponse);
                }
            }

            ApiResponse response = new ApiResponse();
            response.setCode("401");
            response.setMessage("Yeniləmə tokeni etibarsızdır");
            return response;

        } catch (Exception e) {
            ApiResponse response = new ApiResponse();
            response.setCode("500");
            response.setMessage("Token yeniləmə zamanı xəta baş verdi: " + e.getMessage());
            return response;
        }
    }
}