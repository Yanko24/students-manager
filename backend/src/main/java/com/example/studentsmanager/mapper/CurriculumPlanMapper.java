package com.example.studentsmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentsmanager.model.entity.CurriculumPlan;
import com.example.studentsmanager.model.vo.curriculum.CurriculumEarnedCreditsVO;
import com.example.studentsmanager.model.vo.curriculum.CurriculumMajorOptionVO;
import com.example.studentsmanager.model.vo.curriculum.CurriculumRequiredCourseVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;

import java.util.List;

@Mapper
public interface CurriculumPlanMapper extends BaseMapper<CurriculumPlan> {
    @Select("SELECT code, name, grade, MIN(status) AS status FROM majors " +
            "WHERE is_deleted = 0 GROUP BY code, name, grade ORDER BY code, grade")
    List<CurriculumMajorOptionVO> selectMajorOptions();

    @Select("SELECT COUNT(*) FROM majors WHERE code = #{majorCode} AND grade = #{grade} AND status = 0 AND is_deleted = 0")
    int countActiveMajorGrade(@Param("majorCode") String majorCode, @Param("grade") String grade);

    @Select("SELECT p.* FROM curriculum_plans p JOIN students s ON s.major_code = p.major_code AND s.grade = p.grade " +
            "WHERE s.id = #{studentId} AND p.is_deleted = 0 LIMIT 1")
    CurriculumPlan selectForStudent(@Param("studentId") Long studentId);

    @Select("SELECT course_catalog_id FROM curriculum_plan_courses WHERE plan_id = #{planId} ORDER BY course_catalog_id")
    List<Long> selectRequiredCourseCatalogIds(@Param("planId") Long planId);

    @Select("SELECT cc.id AS catalogId, cc.course_code AS courseCode, cc.course_name AS courseName, " +
            "EXISTS (SELECT 1 FROM scores sc JOIN courses c ON c.id = sc.course_id " +
            "WHERE sc.student_id = #{studentId} AND c.catalog_id = cc.id AND sc.score >= 60 " +
            "AND sc.publish_status = 'PUBLISHED' AND sc.is_deleted = 0 AND c.is_deleted = 0) AS passed " +
            "FROM curriculum_plan_courses pc JOIN course_catalog cc ON cc.id = pc.course_catalog_id " +
            "WHERE pc.plan_id = #{planId} ORDER BY cc.course_code")
    List<CurriculumRequiredCourseVO> selectRequiredCourseProgress(@Param("planId") Long planId, @Param("studentId") Long studentId);

    @Select({"<script>", "SELECT COUNT(*) FROM course_catalog WHERE id IN",
            "<foreach collection='catalogIds' item='catalogId' open='(' separator=',' close=')'>#{catalogId}</foreach>",
            "</script>"})
    int countCatalogIds(@Param("catalogIds") List<Long> catalogIds);

    @Select({"<script>", "SELECT COUNT(*) FROM course_catalog WHERE course_type = '必修课' AND id IN",
            "<foreach collection='catalogIds' item='catalogId' open='(' separator=',' close=')'>#{catalogId}</foreach>",
            "</script>"})
    int countRequiredCatalogIds(@Param("catalogIds") List<Long> catalogIds);

    @Delete("DELETE FROM curriculum_plan_courses WHERE plan_id = #{planId}")
    int deletePlanCourses(@Param("planId") Long planId);

    @Insert("INSERT INTO curriculum_plan_courses (plan_id, course_catalog_id) VALUES (#{planId}, #{catalogId})")
    int insertPlanCourse(@Param("planId") Long planId, @Param("catalogId") Long catalogId);

    @Select({"SELECT COALESCE(SUM(completed.credits), 0) AS earnedTotalCredits,",
            "COALESCE(SUM(CASE WHEN completed.courseType = '必修课' THEN completed.credits ELSE 0 END), 0) AS earnedRequiredCredits,",
            "COALESCE(SUM(CASE WHEN completed.courseType <> '必修课' THEN completed.credits ELSE 0 END), 0) AS earnedElectiveCredits",
            "FROM (SELECT COALESCE(c.catalog_id, c.id) AS catalogId, MAX(c.course_type) AS courseType,",
            "MAX(c.credits) AS credits, MAX(sc.score >= 60) AS passed",
            "FROM scores sc JOIN courses c ON c.id = sc.course_id AND c.is_deleted = 0",
            "JOIN students s ON s.id = sc.student_id AND s.is_deleted = 0",
            "WHERE s.id = #{studentId} AND sc.is_deleted = 0 AND sc.publish_status = 'PUBLISHED'",
            "GROUP BY COALESCE(c.catalog_id, c.id)) completed WHERE completed.passed = 1"})
    CurriculumEarnedCreditsVO selectEarnedCredits(@Param("studentId") Long studentId);
}
