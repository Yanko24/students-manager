package com.example.studentsmanager.model.vo.score;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ScoreChangeLogVO {
    private Long id;
    private String action;
    private BigDecimal oldScore;
    private BigDecimal newScore;
    private String oldAttemptType;
    private String newAttemptType;
    private String oldPublishStatus;
    private String newPublishStatus;
    private String reason;
    private String operator;
    private LocalDateTime createTime;
}
