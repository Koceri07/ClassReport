package com.classreport.classreport.service;

import com.classreport.classreport.entity.ParentEntity;
import com.classreport.classreport.entity.StudentEntity;
import com.classreport.classreport.entity.TeacherEntity;
import com.classreport.classreport.entity.UserEntity;
import com.classreport.classreport.model.enums.Role;
import com.classreport.classreport.model.request.RefreshTokenRequest;
import com.classreport.classreport.model.request.UserRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.model.response.AuthResponse;
import com.classreport.classreport.model.response.UserResponse;
import com.classreport.classreport.repository.ParentRepository;
import com.classreport.classreport.repository.StudentRepository;
import com.classreport.classreport.repository.TeacherRepository;
import com.classreport.classreport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;
    private UserRequest request;

    @Transactional
    public ApiResponse register(UserRequest request) {
        log.info("Action.register.start for id {}", request.getId());
        try {
            // Validation
            if (request.getEmail() == null || request.getPassword() == null || request.getRole() == null) {
                log.error("Action.register.end for id {}", request.getId());
                return new ApiResponse("400");
            }

            // Şifrə validation
            if (request.getPassword().trim().isEmpty()) {
                log.error("Action.register.end for id {}", request.getId());
                return new ApiResponse("400");
            }

            // Email yoxlaması
            boolean emailExists = false;
            switch (request.getRole()) {
                case PARENT -> emailExists = parentRepository.existsByEmail(request.getEmail());
                case STUDENT -> emailExists = studentRepository.existsByEmail(request.getEmail());
                case TEACHER -> emailExists = teacherRepository.existsByEmail(request.getEmail());
            }

            if (emailExists) {
                log.error("Action.register.end for id {}", request.getId());
                return new ApiResponse("400");
            }

            // Şifrəni encode et
            String encodedPassword = passwordEncoder.encode(request.getPassword());

            // User yarat - role görə müxtəlif entity (constructor ilə)
            UserEntity user = null;

            switch (request.getRole()) {
                case PARENT:
                    ParentEntity parent = new ParentEntity();
                    parent.setName(request.getName());
                    parent.setSurname(request.getSurname());
                    parent.setEmail(request.getEmail());
                    parent.setPassword(encodedPassword);
                    parent.setRole(Role.PARENT);
                    parent.setActive(true);
                    parentRepository.save(parent);
                    user = parent;
                    break;

                case STUDENT:
                    StudentEntity student = new StudentEntity();
                    student.setName(request.getName());
                    student.setSurname(request.getSurname());
                    student.setEmail(request.getEmail());
                    student.setPassword(encodedPassword);
                    student.setRole(Role.STUDENT);
                    student.setActive(true);
                    studentRepository.save(student);
                    user = student;
                    break;

                case TEACHER:
                    TeacherEntity teacher = new TeacherEntity();
                    teacher.setName(request.getName());
                    teacher.setSurname(request.getSurname());
                    teacher.setEmail(request.getEmail());
                    teacher.setPassword(encodedPassword);
                    teacher.setRole(Role.TEACHER);
                    teacher.setActive(true);
                    teacherRepository.save(teacher);
                    user = teacher;
                    break;

                default:
                    log.error("Action.register.end for id {}", request.getId());
                    return new ApiResponse("400");
            }

            // Token yarat
            String jwtToken = jwtService.generateToken(user);

            // ✅ DÜZ DATA FORMATI
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("token", jwtToken);
            responseData.put("id", user.getId());
            responseData.put("name", user.getName());
            responseData.put("surname", user.getSurname());
            responseData.put("email", user.getEmail());
            responseData.put("role", user.getRole().name());
            responseData.put("active", user.isActive());

            log.info("Action.register.end for email {}", request.getEmail());

            // ✅ DÜZ API RESPONSE
            ApiResponse response = new ApiResponse();
            response.setCode("200");
            response.setMessage("Successfully registered");
            response.setData(responseData);

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Action.register.end for email {}", request.getEmail());
            ApiResponse response = new ApiResponse();
            response.setCode("500");
            response.setMessage("Registration error: " + e.getMessage());
            return response;
        }
    }

    public ApiResponse login(UserRequest request) {
        this.request = request;
        log.info("Action.login.start for email {}", request.getEmail());
        try {
            // Validation
            if (request.getEmail() == null || request.getPassword() == null) {
                log.error("Email or password is null");
                return new ApiResponse("400");
            }

            // Userı tap
            UserEntity user = findUserByEmail(request.getEmail());

            if (user == null) {
                log.error("User not found with email: {}", request.getEmail());
                return new ApiResponse("404");
            }

            // Şifrəni yoxla
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                log.error("Invalid password for email: {}", request.getEmail());
                return new ApiResponse("401");
            }

            // ✅ REAL JWT TOKEN YARAD
            String jwtToken = jwtService.generateToken(user);

            // ✅ DÜZ DATA FORMATI
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("token", jwtToken);
            responseData.put("id", user.getId());
            responseData.put("name", user.getName());
            responseData.put("surname", user.getSurname());
            responseData.put("email", user.getEmail());
            responseData.put("role", user.getRole().name());
            responseData.put("active", user.isActive());

            log.info("Action.login.success for email {}", request.getEmail());

            // ✅ ApiResponse yarat
            ApiResponse response = new ApiResponse();
            response.setCode("200");
            response.setMessage("Successfully logged in");
            response.setData(responseData); // ✅ Map set et

            return response;

        } catch (Exception e) {
            log.error("Action.login.error for email {}: {}", request.getEmail(), e.getMessage());
            ApiResponse response = new ApiResponse();
            response.setCode("500");
            response.setMessage("Login error: " + e.getMessage());
            return response;
        }
    }

    private UserEntity findUserByEmail(String email) {
        TeacherEntity teacher = teacherRepository.findByEmail(email);
        if (teacher != null) return teacher;

        StudentEntity student = studentRepository.findByEmail(email);
        if (student != null) return student;

        ParentEntity parent = parentRepository.findByEmail(email);
        return parent;
    }

    public ApiResponse refreshToken(String authHeader) {
        try {
            // Validation
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return new ApiResponse("400");
            }

            String token = authHeader.substring(7);
            String userEmail = jwtService.extractUsername(token);

            if (userEmail != null) {
                Optional<UserEntity> userOptional = userRepository.findByEmail(userEmail);
                if (userOptional.isEmpty()) {
                    return new ApiResponse("404");
                }

                UserEntity user = userOptional.get();

                if (jwtService.isTokenValid(token, user)) {
                    String newToken = jwtService.generateToken(user);

                    UserResponse userResponse = new UserResponse();
                    userResponse.setId(user.getId());
                    userResponse.setName(user.getName());
                    userResponse.setSurname(user.getSurname());
                    userResponse.setEmail(user.getEmail());
                    userResponse.setRole(user.getRole());
                    userResponse.setActive(user.isActive());
                    userResponse.setAccessToken(newToken);

                    return new ApiResponse(userResponse);
                }
            }

            return new ApiResponse("401");

        } catch (Exception e) {
            return new ApiResponse("500");
        }
    }


    public ApiResponse getCurrentUser(String authHeader) {
        try {
            String token = jwtService.extractTokenFromHeader(authHeader);
            if (token == null) {
                return new ApiResponse("Token not provided");
            }

            String username = jwtService.extractUsername(token);
            Optional<UserEntity> userOptional = userRepository.findByEmail(username);

            if (userOptional.isEmpty()) {
                return new  ApiResponse("User not found");
            }

            UserEntity user = userOptional.get();
            // Lazımi məlumatları qaytarın
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("email", user.getEmail());
            userData.put("name", user.getName());
            userData.put("surname", user.getSurname());
            userData.put("role", user.getRole());

            return new ApiResponse(userData);
        } catch (Exception e) {
            return new ApiResponse("Error retrieving user data: " + e.getMessage());
        }
    }

    public ApiResponse logout(String authHeader) {
        try {
            // JWT stateless olduğu üçün sadəcə client tərəfdə tokeni silmək kifayətdir
            return new ApiResponse("200");

        } catch (Exception e) {
            return new ApiResponse("500");
        }
    }


    public AuthResponse createTokensForUser(String email) {
        UserEntity userEntity = userRepository.findSimpleByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(userEntity);
        String refreshToken = jwtService.generateRefreshToken(userEntity);

        return new AuthResponse(token, refreshToken);
    }



    public AuthResponse refreshToken(RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();

            if (!jwtService.isRefreshTokenValid(refreshToken)) {
                throw new RuntimeException("Invalid or expired refresh token");
            }

            String email = jwtService.extractUsername(refreshToken);
            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String accessToken = jwtService.generateToken(user);
            String newRefreshToken = jwtService.generateRefreshToken(user);

            return new AuthResponse(accessToken, newRefreshToken);

        } catch (Exception e) {
            throw new RuntimeException("Token refresh failed: " + e.getMessage());
        }
    }
}