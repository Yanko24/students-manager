package com.example.studentsmanager.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("student_status_change_requests")
public class StudentStatusChangeRequest {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private String changeType;
    private Integer currentStatus;
    private Integer targetStatus;
    private String targetMajorCode;
    private String targetClassNo;
    private LocalDate effectiveDate;
    private String reason;
    private String status;
    private String reviewComment;
    private String reviewedBy;
    private LocalDateTime reviewedAt;
    private LocalDateTime appliedAt;
    @TableField(exist = false)
    private String studentNo;
    @TableField(exist = false)
    private String studentName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
