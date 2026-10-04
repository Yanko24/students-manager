package com.example.studentsmanager.model.vo.score;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreDistributionResponse {
    private String periodLabel;
    private String latestSemester;
    private List<ScoreDistributionVO> distribution;
}
