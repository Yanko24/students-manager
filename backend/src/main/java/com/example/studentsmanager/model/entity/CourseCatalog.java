package com.example.studentsmanager.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("course_catalog")
public class CourseCatalog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String courseCode;
    private String courseName;
    private BigDecimal credits;
    private String courseType;
    private Integer hours;
    private String description;
    private String objectives;
}
