package com.example.studentsmanager.model.dto.curriculum;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CurriculumPlanDTO {
    private String planName;
    private String majorCode;
    private String grade;
    private BigDecimal totalCredits;
    private BigDecimal requiredCredits;
    private BigDecimal electiveCredits;
}
