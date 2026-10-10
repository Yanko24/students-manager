package com.example.studentsmanager.model.vo.curriculum;

import lombok.Data;

@Data
public class CurriculumRequiredCourseVO {
    private Long catalogId;
    private String courseCode;
    private String courseName;
    private boolean passed;
}
