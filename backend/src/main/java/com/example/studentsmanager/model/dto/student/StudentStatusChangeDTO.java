package com.example.studentsmanager.model.dto.student;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentStatusChangeDTO {
    private String changeType;
    private String targetMajorCode;
    private String targetClassNo;
    private LocalDate effectiveDate;
    private String reason;
}
