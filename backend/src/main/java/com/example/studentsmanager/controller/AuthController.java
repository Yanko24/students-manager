package com.example.studentsmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.example.studentsmanager.core.response.Result;
import com.example.studentsmanager.model.dto.auth.LoginRequest;
import com.example.studentsmanager.model.dto.auth.LoginResponse;
import com.example.studentsmanager.model.dto.auth.ChangePasswordRequest;
import com.example.studentsmanager.model.vo.auth.AccountProfileVO;
import com.example.studentsmanager.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import jakarta.validation.Valid;

@Slf4j
@Tag(name = "认证管理")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "用户登录", description = "用户登录认证接口，验证用户身份并返回认证信息")
@PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("收到登录请求，用户名：{}", request.getUsername());
        try {
            LoginResponse response = authService.login(request);
            log.info("用户登录成功，用户名：{}", request.getUsername());
            return Result.success(response);
        } catch (Exception e) {
            log.error("用户登录失败，用户名：{}，错误信息：{}", request.getUsername(), e.getMessage(), e);
            throw e; // 让GlobalExceptionHandler处理异常
        }
    }

    /** Authenticated lightweight endpoint used by the frontend to detect backend restarts. */
    @GetMapping("/session")
    public Result<Void> validateSession() {
        return Result.success();
    }

    @GetMapping("/current")
    public Result<AccountProfileVO> getCurrentProfile(Authentication authentication) {
        return Result.success(authService.getCurrentProfile(authentication.getName()));
    }

    @PostMapping("/change-password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                       Authentication authentication) {
        authService.changePassword(authentication.getName(), request);
        return Result.success();
    }
}
