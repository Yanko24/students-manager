package com.example.studentsmanager.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("score_change_logs")
public class ScoreChangeLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("score_id")
    private Long scoreId;
    private String action;
    @TableField("old_score")
    private BigDecimal oldScore;
    @TableField("new_score")
    private BigDecimal newScore;
    @TableField("old_attempt_type")
    private String oldAttemptType;
    @TableField("new_attempt_type")
    private String newAttemptType;
    @TableField("old_publish_status")
    private String oldPublishStatus;
    @TableField("new_publish_status")
    private String newPublishStatus;
    private String reason;
    @com.baomidou.mybatisplus.annotation.TableField("operator_name")
    private String operator;
    private LocalDateTime createTime;
}
