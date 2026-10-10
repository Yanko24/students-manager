package com.example.studentsmanager.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class StudentStatusChangeScheduler {
    private static final String TASK_NAME = "学籍异动生效处理";
    private final StudentStatusChangeService studentStatusChangeService;
    private final ScheduledTaskRunService taskRunService;

    @Scheduled(cron = "0 * * * * *")
    public void applyDueApprovedChanges() {
        LocalDateTime startedAt = LocalDateTime.now();
        try {
            int processed = studentStatusChangeService.applyDueApprovedChanges();
            if (processed > 0) {
                taskRunService.recordSuccess(TASK_NAME, processed, startedAt);
            }
        } catch (Exception error) {
            taskRunService.recordFailure(TASK_NAME, error.getMessage(), startedAt);
            log.error("定时任务 {} 执行失败", TASK_NAME, error);
        }
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void cleanupExpiredRuns() {
        taskRunService.deleteExpiredRuns();
    }
}
