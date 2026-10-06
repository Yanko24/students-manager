package com.example.studentsmanager.security.config;

import com.example.studentsmanager.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import com.example.studentsmanager.security.Sm3Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                // 前端静态资源和 Vue 页面由 Spring Boot 同源提供
                .requestMatchers("/", "/index.html", "/favicon.ico", "/assets/**",
                    "/login", "/change-password", "/admin", "/admin/**", "/teacher", "/teacher/**",
                    "/student", "/student/**").permitAll()
                .requestMatchers("/api/auth/login", "/api/health/**").permitAll()
                .requestMatchers("/api/auth/change-password").authenticated()
                // OpenAPI / Swagger UI
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/webjars/**").permitAll()
                // 基于角色的访问控制
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/students/**").hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                .requestMatchers("/api/teachers/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers("/api/student-portal/**").hasRole("STUDENT")
                .requestMatchers("/api/attendance/teacher/**").hasRole("TEACHER")
                .requestMatchers("/api/attendance/student/**").hasRole("STUDENT")
                .requestMatchers("/api/attendance", "/api/attendance/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Sm3Pbkdf2PasswordEncoder();
    }
}
