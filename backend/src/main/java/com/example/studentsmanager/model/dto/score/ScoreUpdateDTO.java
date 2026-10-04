package com.example.studentsmanager.model.dto.score;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ScoreUpdateDTO {
    private Long studentId;
    private Long courseId;
    private BigDecimal score;
    private String grade;
    private String semester;
    private LocalDateTime examTime;
    private String comment;
}
