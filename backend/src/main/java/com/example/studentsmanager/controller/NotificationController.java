package com.example.studentsmanager.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentsmanager.core.response.Result;
import com.example.studentsmanager.exception.BusinessException;
import com.example.studentsmanager.model.entity.SystemNotification;
import com.example.studentsmanager.service.impl.SystemNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final SystemNotificationService service;
    @GetMapping public Result<Page<SystemNotification>> mine(Principal principal,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size,
            @RequestParam(defaultValue = "false") boolean unreadOnly,
            @RequestParam(required = false) String type) {
        return Result.success(service.mine(principal.getName(), page, size, unreadOnly, type));
    }
    @GetMapping("/unread-count") public Result<Map<String, Integer>> unreadCount(Principal principal) {
        return Result.success(java.util.Collections.singletonMap("count", service.unreadCount(principal.getName())));
    }
    @PutMapping("/{id}/read") public Result<Void> markRead(@PathVariable Long id, Principal principal) {
        service.markRead(id, principal.getName()); return Result.success();
    }

    @PutMapping("/read-all") public Result<Map<String, Integer>> markAllRead(Principal principal) {
        return Result.success(java.util.Collections.singletonMap("updated", service.markAllRead(principal.getName())));
    }

    @PostMapping("/admin/announcements")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> announce(@RequestBody AnnouncementRequest request) {
        if (request == null || request.title() == null || request.title().isBlank()
                || request.message() == null || request.message().isBlank()) {
            throw new BusinessException("通知标题和内容不能为空");
        }
        if (request.title().length() > 200 || request.message().length() > 1000) {
            throw new BusinessException("通知标题不能超过200字，内容不能超过1000字");
        }
        String audience = request.audience() == null ? "ALL" : request.audience().trim().toUpperCase();
        String target = request.target() == null ? "HOME" : request.target().trim().toUpperCase();
        if (!List.of("HOME", "COURSES", "SCORES", "ATTENDANCE", "STATUS").contains(target)) {
            throw new BusinessException("通知跳转目标无效");
        }
        String notificationType = "ANNOUNCEMENT_" + target;
        List<String> audiences;
        if ("ALL".equals(audience)) {
            audiences = List.of("student", "teacher");
        } else if ("STUDENT".equals(audience) || "TEACHER".equals(audience)) {
            audiences = List.of(audience.toLowerCase(java.util.Locale.ROOT));
        } else {
            throw new BusinessException("通知对象无效");
        }
        service.notifyRoles(audiences, notificationType, request.title().trim(), request.message().trim());
        return Result.success();
    }

    public record AnnouncementRequest(String audience, String target, String title, String message) {}
}
