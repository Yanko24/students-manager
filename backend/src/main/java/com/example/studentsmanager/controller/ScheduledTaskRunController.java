package com.example.studentsmanager.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentsmanager.core.response.Result;
import com.example.studentsmanager.model.entity.ScheduledTaskRun;
import com.example.studentsmanager.service.impl.ScheduledTaskRunService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/task-runs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ScheduledTaskRunController {
    private final ScheduledTaskRunService scheduledTaskRunService;

    @GetMapping
    public Result<Page<ScheduledTaskRun>> getLatest(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size) {
        return Result.success(scheduledTaskRunService.getLatest(page, size));
    }
}
