package com.example.studentsmanager.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentsmanager.core.response.Result;
import com.example.studentsmanager.model.dto.student.StudentStatusChangeDTO;
import com.example.studentsmanager.model.dto.student.StudentStatusChangeReviewDTO;
import com.example.studentsmanager.model.entity.StudentStatusChangeRequest;
import com.example.studentsmanager.service.impl.StudentStatusChangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StudentStatusChangeController {
    private final StudentStatusChangeService service;

    @GetMapping("/api/student-portal/status-changes")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<List<StudentStatusChangeRequest>> mine(Principal principal) {
        return Result.success(service.mine(principal.getName()));
    }

    @PostMapping("/api/student-portal/status-changes")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<StudentStatusChangeRequest> apply(Principal principal, @RequestBody StudentStatusChangeDTO dto) {
        return Result.success(service.apply(principal.getName(), dto));
    }

    @GetMapping("/api/admin/student-status-changes")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<StudentStatusChangeRequest>> page(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size, @RequestParam(required = false) String status) {
        return Result.success(service.page(page, size, status));
    }

    @PutMapping("/api/admin/student-status-changes/{id}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<StudentStatusChangeRequest> review(@PathVariable Long id, @RequestBody StudentStatusChangeReviewDTO dto, Principal principal) {
        return Result.success(service.review(id, dto, principal.getName()));
    }
}
