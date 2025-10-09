package com.classreport.classreport.controller;

import com.classreport.classreport.model.request.UserRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
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

}