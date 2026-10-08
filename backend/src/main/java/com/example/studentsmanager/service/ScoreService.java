package com.example.studentsmanager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;
import com.example.studentsmanager.model.dto.score.ScoreQueryDTO;
import com.example.studentsmanager.model.dto.score.ScoreUpdateDTO;
import com.example.studentsmanager.model.entity.Score;
import com.example.studentsmanager.model.vo.score.ScoreVO;
import com.example.studentsmanager.model.vo.score.ScoreDistributionResponse;
import com.example.studentsmanager.model.vo.score.ScoreChangeLogVO;

import java.util.List;

public interface ScoreService extends IService<Score> {
    Page<ScoreVO> getScorePage(ScoreQueryDTO queryDTO);
    ScoreDistributionResponse getScoreDistribution(String period);
    ScoreVO getScore(Long id);
    ScoreVO createScore(ScoreUpdateDTO dto, String actor);
    ScoreVO updateScore(Long id, ScoreUpdateDTO dto, String actor);
    void deleteScore(Long id, String reason, String actor);
    int publishScores(List<Long> ids, String reason, String actor);
    List<ScoreChangeLogVO> getScoreChangeLogs(Long id);
    Page<ScoreVO> getTeacherCourseScorePage(String username, Long courseId, ScoreQueryDTO queryDTO);
    ScoreVO createTeacherCourseScore(String username, Long courseId, ScoreUpdateDTO dto, String actor);
    ScoreVO updateTeacherCourseScore(String username, Long courseId, Long scoreId, ScoreUpdateDTO dto, String actor);
    List<ScoreChangeLogVO> getTeacherCourseScoreHistory(String username, Long courseId, Long scoreId);
}
