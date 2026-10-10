package com.example.studentsmanager.model.vo.student;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AcademicRiskStudentVO {
    private Long studentId;
    private String studentNo;
    private String studentName;
    private String majorName;
    private String grade;
    private String classNo;
    private Integer failedCourseCount;
    private Integer absenceCount;
    private BigDecimal averageScore;
    private String riskReasons;
}
