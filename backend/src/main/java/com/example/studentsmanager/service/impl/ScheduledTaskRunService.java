package com.example.studentsmanager.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.studentsmanager.mapper.ScheduledTaskRunMapper;
import com.example.studentsmanager.model.entity.ScheduledTaskRun;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ScheduledTaskRunService extends ServiceImpl<ScheduledTaskRunMapper, ScheduledTaskRun> {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordSuccess(String taskName, int processedCount, LocalDateTime startedAt) {
        ScheduledTaskRun run = new ScheduledTaskRun();
        run.setTaskName(taskName);
        run.setStatus("SUCCESS");
        run.setStartedAt(startedAt);
        run.setFinishedAt(LocalDateTime.now());
        run.setProcessedCount(Math.max(0, processedCount));
        save(run);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailure(String taskName, String errorMessage, LocalDateTime startedAt) {
        ScheduledTaskRun run = new ScheduledTaskRun();
        run.setTaskName(taskName);
        run.setStatus("FAILED");
        run.setStartedAt(startedAt);
        run.setFinishedAt(LocalDateTime.now());
        run.setProcessedCount(0);
        run.setErrorMessage(errorMessage == null ? "任务执行失败" : errorMessage.substring(0, Math.min(1000, errorMessage.length())));
        save(run);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int deleteExpiredRuns() {
        return baseMapper.deleteExpiredRuns();
    }

    public Page<ScheduledTaskRun> getLatest(long page, long size) {
        return baseMapper.selectLatest(new Page<>(Math.max(1, page), Math.min(100, Math.max(1, size))));
    }
}
