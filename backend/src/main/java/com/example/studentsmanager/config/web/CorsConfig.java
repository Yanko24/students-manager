package com.example.studentsmanager.config.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    
    @Value("${spring.cors.allowed-origins}")
    private String allowedOrigins;
    
    @Value("${spring.cors.allowed-methods}")
    private String allowedMethods;
    
    @Value("${spring.cors.allowed-headers}")
    private String allowedHeaders;
    
    @Value("${spring.cors.allow-credentials}")
    private boolean allowCredentials;
    
    @Value("${spring.cors.max-age}")
    private long maxAge;
    
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许跨域访问的源
        config.addAllowedOrigin(allowedOrigins);
        // 允许跨域访问的请求头
        config.addAllowedHeader(allowedHeaders);
        // 允许跨域访问的请求方法
        for (String method : allowedMethods.split(",")) {
            config.addAllowedMethod(method.trim());
        }
        // 允许跨域访问时携带凭证
        config.setAllowCredentials(allowCredentials);
        // 设置预检请求的有效期
        config.setMaxAge(maxAge);
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
} 