package com.example.studentsmanager.model.dto.score;

import lombok.Data;

import java.util.List;

@Data
public class ScorePublishDTO {
    private List<Long> scoreIds;
    private String reason;
}
