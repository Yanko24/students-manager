package com.example.studentsmanager.model.vo.auth;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AccountProfileVO {
    private Long userId;
    private String username;
    private String realName;
    private String role;
    private Integer gender;
    private String phone;
    private String email;
    private Integer accountStatus;
    private LocalDateTime accountCreatedAt;

    private String studentNo;
    private LocalDate birthDate;
    private LocalDate admissionDate;
    private String address;
    private String majorCode;
    private String majorName;
    private String collegeName;
    private String grade;
    private String classNo;
    private Integer studentStatus;
    private String studentStatusText;

    private String teacherNo;
    private String department;
    private String title;
    private LocalDate hireDate;
    private Integer teacherStatus;
}
