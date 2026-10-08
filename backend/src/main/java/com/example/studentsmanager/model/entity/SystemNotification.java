package com.example.studentsmanager.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("system_notifications")
public class SystemNotification {
    @TableId(type = IdType.AUTO) private Long id;
    private Long userId;
    private String notificationType;
    private String title;
    private String message;
    private LocalDateTime readAt;
    private LocalDateTime createTime;
}
