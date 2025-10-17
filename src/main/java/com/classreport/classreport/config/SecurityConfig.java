package com.classreport.classreport.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final com.classreport.classreport.filter.JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v1/auth/**",
                                "/auth/**",
                                "v1/auth/get/access-token",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/static/**",
                                "/*.html",
                                "/*.css",
                                "/*.js"
                        ).permitAll()
                        // ✅ DÜZƏLDİ: ROLE_ prefixi əlavə et
//                        .requestMatchers("/v1/auth/**").permitAll()
                        .requestMatchers("/v1/teachers/**").hasAnyAuthority("ROLE_TEACHER", "ROLE_ADMIN")
                        .requestMatchers("/v1/parents/**").hasAnyAuthority("ROLE_PARENT", "ROLE_ADMIN")
                        .requestMatchers("/v1/students/**").hasAnyAuthority("ROLE_STUDENT", "ROLE_TEACHER", "ROLE_ADMIN", "ROLE_PARENT")
                        // ✅ BURASI ƏSAS PROBLEM: ROLE_TEACHER əlavə et
                        .requestMatchers("/v1/groups/**").hasAnyAuthority("ROLE_TEACHER", "ROLE_ADMIN", "ROLE_PARENT")
                        .requestMatchers("/v1/attendances/**").hasAnyAuthority("ROLE_TEACHER", "ROLE_ADMIN", "ROLE_PARENT")
                        .requestMatchers("/v1/lessons/**").hasAnyAuthority("ROLE_TEACHER", "ROLE_ADMIN")
                        .requestMatchers("/v1/exams/**").hasAnyAuthority("ROLE_TEACHER", "ROLE_ADMIN", "ROLE_PARENT")
                        .requestMatchers("/v1/reports/**").hasAnyAuthority("ROLE_TEACHER", "ROLE_ADMIN", "ROLE_PARENT")
                        .requestMatchers("/v1/transfers/**").hasAnyAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://127.0.0.1:5500",
                "http://localhost:5500",
                "http://localhost",
                "http://127.0.0.1",
                "http://localhost:9999",
                "http://127.0.0.1:9999"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}