package com.example.studentsmanager.model.dto.curriculum;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CurriculumPlanDTO {
    private String planName;
    private String majorCode;
    private String grade;
    private BigDecimal totalCredits;
    private BigDecimal requiredCredits;
    private BigDecimal electiveCredits;
    private List<Long> requiredCourseCatalogIds;
}
