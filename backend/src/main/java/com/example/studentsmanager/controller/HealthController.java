package com.example.studentsmanager.controller;

import com.example.studentsmanager.core.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "健康检查")
@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Operation(summary = "应用健康检查", description = "检查应用的基本运行状态，无需认证即可访问")
    @GetMapping("/check")
    public Result<Map<String, Object>> healthCheck() {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", System.currentTimeMillis());
        healthInfo.put("version", "1.0.0");
        return Result.success(healthInfo);
    }

    @Operation(summary = "数据库健康检查", description = "管理员检查数据库连接状态")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/db")
    public Result<Map<String, Object>> dbHealthCheck() {
        Map<String, Object> dbInfo = new HashMap<>();
        try {
            // 执行简单的数据库查询来检查连接
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            dbInfo.put("status", "UP");
            dbInfo.put("message", "Database connection is healthy");
        } catch (Exception e) {
            dbInfo.put("status", "DOWN");
            dbInfo.put("message", "Database connection failed");
        }
        return Result.success(dbInfo);
    }
}
