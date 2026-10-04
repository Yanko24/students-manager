package com.example.studentsmanager.model.vo.attendance;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AttendanceVO {
    private Long id;
    private Long studentId;
    private String studentNo;
    private String studentName;
    private String className;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private String teacherName;
    private LocalDate date;
    private String classPeriod;
    private String status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
