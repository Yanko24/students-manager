package com.example.studentsmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentsmanager.model.entity.Score;
import com.example.studentsmanager.model.vo.score.ScoreDistributionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ScoreMapper extends BaseMapper<Score> {

    @Select("SELECT semester FROM scores WHERE is_deleted = 0 ORDER BY semester DESC LIMIT 1")
    String selectLatestSemester();

    @Select({
            "<script>",
            "SELECT CASE",
            "WHEN score &gt;= 90 THEN '优秀（90分及以上）'",
            "WHEN score &gt;= 80 THEN '良好（80-89分）'",
            "WHEN score &gt;= 70 THEN '中等（70-79分）'",
            "WHEN score &gt;= 60 THEN '及格（60-69分）'",
            "ELSE '不及格（60分以下）' END AS name, COUNT(*) AS value",
            "FROM scores",
            "WHERE is_deleted = 0 AND",
            "<choose>",
            "<when test='academicYear'>semester LIKE CONCAT(#{semesterPattern}, '%')</when>",
            "<otherwise>semester = #{semesterPattern}</otherwise>",
            "</choose>",
            "GROUP BY name",
            "ORDER BY FIELD(name, '优秀（90分及以上）', '良好（80-89分）', '中等（70-79分）', '及格（60-69分）', '不及格（60分以下）')",
            "</script>"
    })
    List<ScoreDistributionVO> selectScoreDistribution(
            @Param("semesterPattern") String semesterPattern,
            @Param("academicYear") boolean academicYear);
}
