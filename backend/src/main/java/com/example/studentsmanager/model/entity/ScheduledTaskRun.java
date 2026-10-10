package com.example.studentsmanager.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("scheduled_task_runs")
public class ScheduledTaskRun {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String taskName;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private Integer processedCount;
    private String errorMessage;
}
