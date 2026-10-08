package com.example.studentsmanager.model.vo.teacher;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TeacherListVO {
    private Long id;
    private String teacherNo;
    private String realName;
    private Integer gender;
    private String phone;
    private String email;
    private String department;
    private String title;
    private Integer status;
    private LocalDate hireDate;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
