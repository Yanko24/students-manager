package com.example.studentsmanager.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("academic_terms")
public class AcademicTerm {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String termCode;
    private String academicYear;
    private Integer termNo;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer isCurrent;
    private Integer isActive;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String createBy;
    private String updateBy;
}
