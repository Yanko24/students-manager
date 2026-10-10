package com.example.studentsmanager.model.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AcademicTermDTO {
    private String termCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer isActive;
}
