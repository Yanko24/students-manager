package com.example.studentsmanager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;
import com.example.studentsmanager.model.dto.score.ScoreQueryDTO;
import com.example.studentsmanager.model.dto.score.ScoreUpdateDTO;
import com.example.studentsmanager.model.entity.Score;
import com.example.studentsmanager.model.vo.score.ScoreVO;
import com.example.studentsmanager.model.vo.score.ScoreDistributionResponse;

public interface ScoreService extends IService<Score> {
    Page<ScoreVO> getScorePage(ScoreQueryDTO queryDTO);
    ScoreDistributionResponse getScoreDistribution(String period);
    ScoreVO getScore(Long id);
    ScoreVO createScore(ScoreUpdateDTO dto);
    ScoreVO updateScore(Long id, ScoreUpdateDTO dto);
    void deleteScore(Long id);
}
