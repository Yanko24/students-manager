package com.example.studentsmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentsmanager.model.entity.ScoreChangeLog;
import com.example.studentsmanager.model.vo.score.ScoreChangeLogVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ScoreChangeLogMapper extends BaseMapper<ScoreChangeLog> {
    @Select("SELECT id, action, old_score AS oldScore, new_score AS newScore, old_attempt_type AS oldAttemptType, " +
            "new_attempt_type AS newAttemptType, old_publish_status AS oldPublishStatus, new_publish_status AS newPublishStatus, " +
            "reason, operator_name AS operator, create_time AS createTime FROM score_change_logs WHERE score_id = #{scoreId} ORDER BY create_time DESC, id DESC")
    List<ScoreChangeLogVO> selectByScoreId(@Param("scoreId") Long scoreId);
}
