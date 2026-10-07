package com.example.studentsmanager.model.vo.course;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseSelectionStudentVO {
    private Long selectionId;
    private Long studentId;
    private String studentNo;
    private String studentName;
    private String majorName;
    private String grade;
    private String classNo;
    private LocalDateTime selectionDate;
    private String selectionStatus;
}
