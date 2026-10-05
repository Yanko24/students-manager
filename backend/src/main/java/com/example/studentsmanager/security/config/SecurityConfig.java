package com.example.studentsmanager.security.config;

import com.example.studentsmanager.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import com.example.studentsmanager.security.Sm3Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
                // 前端静态资源和 Vue 页面由 Spring Boot 同源提供
                .antMatchers("/", "/index.html", "/favicon.ico", "/assets/**",
                    "/login", "/change-password", "/admin", "/admin/**", "/teacher", "/teacher/**",
                    "/student", "/student/**").permitAll()
                // 登录接口
                .antMatchers("/api/auth/login").permitAll()
                .antMatchers("/api/auth/change-password").authenticated()
                // 健康检查接口
                .antMatchers("/api/health/**").permitAll()
                // Swagger UI 相关路径
                .antMatchers("/swagger-ui/**").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/v2/api-docs").permitAll()
                .antMatchers("/v3/api-docs").permitAll()
                .antMatchers("/webjars/**").permitAll()
                // 基于角色的访问控制
                .antMatchers("/api/admin/**").hasRole("ADMIN")
                .antMatchers("/api/students/**").hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                .antMatchers("/api/teachers/**").hasAnyRole("ADMIN", "TEACHER")
                .antMatchers("/api/student-portal/**").hasRole("STUDENT")
                .antMatchers("/api/attendance/teacher/**").hasRole("TEACHER")
                .antMatchers("/api/attendance/student/**").hasRole("STUDENT")
                .antMatchers("/api/attendance", "/api/attendance/**").hasRole("ADMIN")
                // 其他接口需要认证
                .anyRequest().authenticated()
            .and()
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
