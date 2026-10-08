package com.example.studentsmanager.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("curriculum_plans")
public class CurriculumPlan {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String planName;
    private String majorCode;
    private String grade;
    private BigDecimal totalCredits;
    private BigDecimal requiredCredits;
    private BigDecimal electiveCredits;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String createBy;
    private String updateBy;
    @TableLogic
    private Integer isDeleted;
}
