package com.example.studentsmanager.model.vo.curriculum;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class CurriculumProgressVO {
    private boolean planConfigured;
    private String planName;
    private String majorCode;
    private String grade;
    private BigDecimal totalCredits = BigDecimal.ZERO;
    private BigDecimal requiredCredits = BigDecimal.ZERO;
    private BigDecimal electiveCredits = BigDecimal.ZERO;
    private BigDecimal earnedTotalCredits = BigDecimal.ZERO;
    private BigDecimal earnedRequiredCredits = BigDecimal.ZERO;
    private BigDecimal earnedElectiveCredits = BigDecimal.ZERO;
    private BigDecimal remainingTotalCredits = BigDecimal.ZERO;
    private BigDecimal remainingRequiredCredits = BigDecimal.ZERO;
    private BigDecimal remainingElectiveCredits = BigDecimal.ZERO;
    private BigDecimal completionRate = BigDecimal.ZERO;
    private boolean courseRequirementsConfigured;
    private String graduationAuditStatus = "NOT_CONFIGURED";
    private List<String> graduationAuditBlockers = new ArrayList<>();
    private List<CurriculumRequiredCourseVO> requiredCourses = new ArrayList<>();
}
