package com.classreport.classreport.controller;

import com.classreport.classreport.model.request.RefreshTokenRequest;
import com.classreport.classreport.model.request.UserRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.model.response.AuthResponse;
import com.classreport.classreport.service.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
//@SecurityRequirement(name = "bearerAuth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse register(@RequestBody UserRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public ApiResponse login(@RequestBody UserRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh-token")
    public ApiResponse refreshToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return authService.refreshToken(authHeader);
    }

    @GetMapping("/me")
    public ApiResponse getCurrentUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return authService.getCurrentUser(authHeader);
    }

    @PostMapping("/logout")
    public ApiResponse logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return authService.logout(authHeader);
    }


    @PostMapping("/get/access-token")
    public AuthResponse getAccessToken(@RequestBody String email) {
        return authService.createTokensForUser(email);
    }

    @PostMapping("/get/refresh-token")
    public AuthResponse getRefreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return authService.refreshToken(refreshTokenRequest);
    }

}