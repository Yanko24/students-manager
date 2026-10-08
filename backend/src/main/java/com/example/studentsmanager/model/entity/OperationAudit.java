package com.example.studentsmanager.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("operation_audits")
public class OperationAudit {
    @TableId(type = IdType.AUTO) private Long id;
    private String actor;
    private String action;
    private String entityType;
    private String entityId;
    private String summary;
    private LocalDateTime createTime;
}
