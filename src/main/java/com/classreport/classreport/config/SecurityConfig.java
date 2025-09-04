package com.classreport.classreport.config;

import com.classreport.classreport.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v1/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .requestMatchers("/v1/teachers/**").hasAnyAuthority("TEACHER", "ADMIN")
                        .requestMatchers("/v1/parents/**").hasAnyAuthority("PARENT", "ADMIN")
                        .requestMatchers("/v1/students/**").hasAnyAuthority("STUDENT", "TEACHER", "ADMIN")
                        .requestMatchers("/v1/groups/**").hasAnyAuthority("TEACHER", "ADMIN")
                        .requestMatchers("/v1/attendances/**").hasAnyAuthority("TEACHER", "ADMIN")
                        .requestMatchers("/v1/lessons/**").hasAnyAuthority("TEACHER", "ADMIN")
                        .requestMatchers("/v1/exams/**").hasAnyAuthority("TEACHER", "ADMIN")
                        .requestMatchers("/v1/reports/**").hasAnyAuthority("TEACHER", "ADMIN")
                        .requestMatchers("/v1/transfers/**").hasAnyAuthority("ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}