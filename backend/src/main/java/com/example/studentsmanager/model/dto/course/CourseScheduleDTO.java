package com.example.studentsmanager.model.dto.course;

import lombok.Data;

@Data
public class CourseScheduleDTO {
    private Integer dayOfWeek;
    private Integer startPeriod;
    private Integer endPeriod;
    private Integer weekStart;
    private Integer weekEnd;
    private String weekParity;
    private String classroom;
}
