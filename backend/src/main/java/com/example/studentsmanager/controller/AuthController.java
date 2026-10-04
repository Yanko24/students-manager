package com.example.studentsmanager.controller;

import com.example.studentsmanager.core.response.Result;
import com.example.studentsmanager.model.dto.auth.LoginRequest;
import com.example.studentsmanager.model.dto.auth.LoginResponse;
import com.example.studentsmanager.model.dto.auth.ChangePasswordRequest;
import com.example.studentsmanager.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import javax.validation.Valid;

@Slf4j
@Api(tags = "认证管理")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @ApiOperation(value = "用户登录", notes = "用户登录认证接口，验证用户身份并返回认证信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "登录成功", response = LoginResponse.class),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "用户名或密码错误")
    })
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

    @PostMapping("/change-password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                       Authentication authentication) {
        authService.changePassword(authentication.getName(), request);
        return Result.success();
    }
}
