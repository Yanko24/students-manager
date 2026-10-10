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
    public Long begin(String taskName) {
        baseMapper.deleteExpiredRuns();
        ScheduledTaskRun run = new ScheduledTaskRun();
        run.setTaskName(taskName);
        run.setStatus("RUNNING");
        run.setStartedAt(LocalDateTime.now());
        run.setProcessedCount(0);
        save(run);
        return run.getId();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void finish(Long id, int processedCount) {
        ScheduledTaskRun run = getById(id);
        if (run == null) return;
        run.setStatus("SUCCESS");
        run.setProcessedCount(Math.max(0, processedCount));
        run.setFinishedAt(LocalDateTime.now());
        updateById(run);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void fail(Long id, String errorMessage) {
        ScheduledTaskRun run = getById(id);
        if (run == null) return;
        run.setStatus("FAILED");
        run.setFinishedAt(LocalDateTime.now());
        run.setErrorMessage(errorMessage == null ? "任务执行失败" : errorMessage.substring(0, Math.min(1000, errorMessage.length())));
        updateById(run);
    }

    public Page<ScheduledTaskRun> getLatest(long page, long size) {
        return baseMapper.selectLatest(new Page<>(Math.max(1, page), Math.min(100, Math.max(1, size))));
    }
}
