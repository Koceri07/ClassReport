package com.classreport.classreport.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(CorsConfigurationSource corsConfigurationSource) {
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // ✅ BÜTÜN Swagger endpoint'lərini permit et
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/api-docs/**",
                                "/api-docs",
                                "/webjars/**",
                                "/swagger-resources/**",
                                "/swagger-resources",
                                "/configuration/ui",
                                "/configuration/security",
                                "/swagger-ui/index.html",
                                "/swagger-ui/",
                                "/favicon.ico",
                                "/error",
                                "/"
                        ).permitAll()
                        .requestMatchers("/v1/auth/**").permitAll()
                        .requestMatchers("/v1/public/**").permitAll()
                        .requestMatchers("/v1/attendances/**").permitAll()
                        .requestMatchers("/v1/exams/**").permitAll()
                        .requestMatchers("/v1/groups/**").permitAll()
                        .requestMatchers("/v1/group-details/**").permitAll()
                        .requestMatchers("/v1/lesson_instances/**").permitAll()
                        .requestMatchers("/v1/mail-sender/**").permitAll()
                        .requestMatchers("/v1/parents/**").permitAll()
                        .requestMatchers("/v1/reports/**").permitAll()
                        .requestMatchers("v1/students/**").permitAll()
                        .requestMatchers("/v1/teachers").permitAll()
                        .requestMatchers("/v1/users/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}