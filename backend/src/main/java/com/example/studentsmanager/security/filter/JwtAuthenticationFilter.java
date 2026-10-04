package com.example.studentsmanager.security.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import com.example.studentsmanager.security.config.JwtConfig;
import com.example.studentsmanager.security.service.JwtService;
import com.example.studentsmanager.security.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtConfig jwtConfig;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // 检查是否是公开接口
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth/login") || path.startsWith("/api/health/") || 
            path.startsWith("/swagger-ui/") || path.startsWith("/swagger-resources/") || 
            path.startsWith("/v2/api-docs") || path.startsWith("/v3/api-docs") || 
            path.startsWith("/webjars/")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader(jwtConfig.getHeader());
        final String jwt;
        final String username;

        // 如果没有认证头或不是正确的 token 前缀，直接放行
        if (authHeader == null || !authHeader.startsWith(jwtConfig.getPrefix())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 提取 JWT token
        jwt = authHeader.substring(jwtConfig.getPrefix().length());
        
        try {
            // 从 token 中提取用户名
            username = jwtService.extractUsername(jwt);
            
            // 如果有用户名且当前没有认证
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 加载用户详情
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                // 验证 token
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    boolean passwordChangeRequired = userDetails.getAuthorities().contains(
                            new SimpleGrantedAuthority("ROLE_PASSWORD_CHANGE_REQUIRED"));
                    if (passwordChangeRequired && path.startsWith("/api/")
                            && !path.equals("/api/auth/change-password")) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().write("{\"code\":403,\"message\":\"首次登录必须修改默认密码\"}");
                        return;
                    }

                    // 创建认证 token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    
                    // 设置认证详情
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // 更新 SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    writeUnauthorized(response, "后端已重启或登录凭证失效，请重新登录");
                    return;
                }
            }
        } catch (ExpiredJwtException e) {
            writeUnauthorized(response, "登录已过期，请重新登录");
            return;
        } catch (JwtException | IllegalArgumentException e) {
            writeUnauthorized(response, "登录凭证无效，请重新登录");
            return;
        }

        // 继续过滤器链
        filterChain.doFilter(request, response);
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"" + message + "\"}");
    }
} 
