package com.example.studentsmanager.model.vo.course;

import lombok.Data;

@Data
public class CourseScheduleVO {
    private Long id;
    private Integer dayOfWeek;
    private Integer startPeriod;
    private Integer endPeriod;
    private Integer weekStart;
    private Integer weekEnd;
    private String weekParity;
    private String classroom;
}
