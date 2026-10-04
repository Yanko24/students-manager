package com.example.studentsmanager.model.dto.course;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CourseUpdateDTO {
    private String code;
    private String name;
    private Long teacherId;
    private BigDecimal credit;
    private String type;
    private String semester;
    private Integer hours;
    private Integer status;
    private String description;
    private String objectives;
}
