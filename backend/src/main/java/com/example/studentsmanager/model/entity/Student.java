package com.example.studentsmanager.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("students")
public class Student {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    @TableField("student_no")
    private String studentNo;

    @TableField("birth_date")
    private LocalDate birthDate;

    @TableField("admission_date")
    private LocalDate admissionDate;

    private String address;

    @TableField("major_code")
    private String majorCode;

    private String grade;

    @TableField("class_no")
    private String classNo;

    private Integer status;          // 状态：0-在读，1-休学，2-退学，3-毕业

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted;

    // 关联的用户信息（不映射到数据库）
    @TableField(exist = false)
    private User user;

    // 关联的专业信息（不映射到数据库）
    @TableField(exist = false)
    private Major major;
} 