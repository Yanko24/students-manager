package com.example.studentsmanager.controller;

import com.example.studentsmanager.core.response.Result;
import com.example.studentsmanager.model.entity.SystemNotification;
import com.example.studentsmanager.service.impl.SystemNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final SystemNotificationService service;
    @GetMapping public Result<List<SystemNotification>> mine(Principal principal) { return Result.success(service.mine(principal.getName())); }
    @GetMapping("/unread-count") public Result<Map<String, Integer>> unreadCount(Principal principal) {
        return Result.success(java.util.Collections.singletonMap("count", service.unreadCount(principal.getName())));
    }
    @PutMapping("/{id}/read") public Result<Void> markRead(@PathVariable Long id, Principal principal) {
        service.markRead(id, principal.getName()); return Result.success();
    }
}
