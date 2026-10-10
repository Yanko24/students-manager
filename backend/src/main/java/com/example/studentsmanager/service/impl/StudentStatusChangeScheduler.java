package com.example.studentsmanager.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StudentStatusChangeScheduler {
    private static final String TASK_NAME = "学籍异动生效处理";
    private final StudentStatusChangeService studentStatusChangeService;
    private final ScheduledTaskRunService taskRunService;

    @Scheduled(cron = "0 * * * * *")
    public void applyDueApprovedChanges() {
        Long runId = taskRunService.begin(TASK_NAME);
        try {
            int processed = studentStatusChangeService.applyDueApprovedChanges();
            taskRunService.finish(runId, processed);
        } catch (Exception error) {
            taskRunService.fail(runId, error.getMessage());
            log.error("定时任务 {} 执行失败", TASK_NAME, error);
        }
    }
}
