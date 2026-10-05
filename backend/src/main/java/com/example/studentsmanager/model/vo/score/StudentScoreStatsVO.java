package com.example.studentsmanager.model.vo.score;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StudentScoreStatsVO {
    private Long scoreCount = 0L;
    private BigDecimal averageScore = BigDecimal.ZERO;
    private BigDecimal passRate = BigDecimal.ZERO;
    private BigDecimal excellentRate = BigDecimal.ZERO;
    private BigDecimal earnedCredits = BigDecimal.ZERO;
}
