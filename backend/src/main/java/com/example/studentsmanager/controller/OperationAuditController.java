package com.example.studentsmanager.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentsmanager.core.response.Result;
import com.example.studentsmanager.model.entity.OperationAudit;
import com.example.studentsmanager.service.impl.OperationAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/operation-audits")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class OperationAuditController {
    private final OperationAuditService service;

    @GetMapping
    public Result<Page<OperationAudit>> page(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size, @RequestParam(required = false) String actor,
            @RequestParam(required = false) String action) {
        return Result.success(service.page(page, size, actor, action));
    }
}
