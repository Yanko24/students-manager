package com.example.studentsmanager.controller;

import com.example.studentsmanager.constant.ResultCode;
import com.example.studentsmanager.core.response.Result;
import com.example.studentsmanager.exception.BusinessException;
import com.example.studentsmanager.model.entity.User;
import com.example.studentsmanager.service.UserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
@Api(tags = "用户管理")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @ApiOperation(value = "获取所有用户", notes = "返回系统中所有用户的信息列表")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功获取用户列表")
    })
    public Result<List<User>> getAllUsers() {
        log.info("收到获取所有用户请求");
        try {
            List<User> users = userService.list();
            log.info("获取所有用户成功，共{}条记录", users.size());
            return Result.success(users);
        } catch (Exception e) {
            log.error("获取用户列表失败，错误信息：{}", e.getMessage(), e);
            throw e; // 让GlobalExceptionHandler处理异常
        }
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "获取用户详情", notes = "根据ID获取指定用户的详细信息")
    @ApiImplicitParam(name = "id", value = "用户ID", required = true, type = "integer", paramType = "path", example = "1", dataTypeClass = Long.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功获取用户信息"),
            @ApiResponse(code = 404, message = "用户不存在")
    })
    public Result<User> getUserById(@PathVariable Long id) {
        log.info("收到获取用户详情请求，ID: {}", id);
        try {
            User user = userService.getById(id);
            if (user == null) {
                log.warn("用户不存在，ID: {}", id);
                throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
            }
            log.info("获取用户详情成功，ID: {}", id);
            return Result.success(user);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取用户详情失败，ID: {}，错误信息：{}", id, e.getMessage(), e);
            throw e; // 让GlobalExceptionHandler处理异常
        }
    }

    @PostMapping
    @ApiOperation(value = "创建用户", notes = "创建新的用户信息")
    @ApiImplicitParam(name = "user", value = "用户信息", required = true, type = "object", dataTypeClass = User.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "用户创建成功"),
            @ApiResponse(code = 400, message = "用户创建失败")
    })
    public Result<User> createUser(@Valid @RequestBody User user) {
        log.info("收到创建用户请求，用户名: {}", user.getUsername());
        try {
            boolean success = userService.createUser(user);
            if (!success) {
                log.warn("用户创建失败，用户名: {}", user.getUsername());
                throw new BusinessException(ResultCode.ERROR, "用户创建失败");
            }
            log.info("用户创建成功，ID: {}", user.getId());
            return Result.success(user);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("用户创建失败，用户名: {}，错误信息：{}", user.getUsername(), e.getMessage(), e);
            throw e; // 让GlobalExceptionHandler处理异常
        }
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "更新用户", notes = "更新指定用户的信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", required = true, type = "integer", paramType = "path", example = "1", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "user", value = "用户信息", required = true, type = "object", dataTypeClass = User.class)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "用户更新成功"),
            @ApiResponse(code = 404, message = "用户不存在")
    })
    public Result<User> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        log.info("收到更新用户请求，ID: {}", id);
        try {
            User existingUser = userService.getById(id);
            if (existingUser == null) {
                log.warn("用户不存在，ID: {}", id);
                throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
            }
            user.setId(id);
            boolean success = userService.updateUser(user);
            if (!success) {
                log.warn("用户更新失败，ID: {}", id);
                throw new BusinessException(ResultCode.ERROR, "用户更新失败");
            }
            log.info("用户更新成功，ID: {}", id);
            return Result.success(user);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("用户更新失败，ID: {}，错误信息：{}", id, e.getMessage(), e);
            throw e; // 让GlobalExceptionHandler处理异常
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除用户", notes = "删除指定的用户信息")
    @ApiImplicitParam(name = "id", value = "用户ID", required = true, type = "integer", paramType = "path", example = "1", dataTypeClass = Long.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "用户删除成功"),
            @ApiResponse(code = 404, message = "用户不存在")
    })
    public Result<Void> deleteUser(@PathVariable Long id) {
        log.info("收到删除用户请求，ID: {}", id);
        try {
            boolean success = userService.deleteUser(id);
            if (!success) {
                log.warn("用户不存在，ID: {}", id);
                throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
            }
            log.info("用户删除成功，ID: {}", id);
            return Result.success();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("用户删除失败，ID: {}，错误信息：{}", id, e.getMessage(), e);
            throw e; // 让GlobalExceptionHandler处理异常
        }
    }
} 