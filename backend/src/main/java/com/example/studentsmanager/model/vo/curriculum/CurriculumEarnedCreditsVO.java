package com.example.studentsmanager.model.vo.curriculum;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CurriculumEarnedCreditsVO {
    private BigDecimal earnedTotalCredits;
    private BigDecimal earnedRequiredCredits;
    private BigDecimal earnedElectiveCredits;
}
