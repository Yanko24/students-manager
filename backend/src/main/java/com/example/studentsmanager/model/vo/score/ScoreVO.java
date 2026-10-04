package com.example.studentsmanager.model.vo.score;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ScoreVO {
    private Long id;
    private Long studentId;
    private String studentNo;
    private String studentName;
    private Long courseId;
    private String courseCode;
    private String courseNo;
    private String courseName;
    private BigDecimal credit;
    private BigDecimal score;
    private String grade;
    private BigDecimal gradePoint;
    private String semester;
    private LocalDateTime examTime;
    private String comment;
    private String remark;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
