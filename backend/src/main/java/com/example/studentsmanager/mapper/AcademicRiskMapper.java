package com.example.studentsmanager.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentsmanager.model.vo.student.AcademicRiskStudentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AcademicRiskMapper {
    @Select({"<script>",
            "SELECT s.id AS studentId, s.student_no AS studentNo, u.real_name AS studentName,",
            "m.name AS majorName, s.grade, s.class_no AS classNo,",
            "COALESCE(sc.failedCourseCount, 0) AS failedCourseCount, COALESCE(att.absenceCount, 0) AS absenceCount, sc.averageScore,",
            "CONCAT_WS('、',",
            "CASE WHEN COALESCE(sc.failedCourseCount, 0) &gt; 0 THEN CONCAT('不及格课程 ', sc.failedCourseCount, ' 门') END,",
            "CASE WHEN COALESCE(att.absenceCount, 0) &gt;= #{absenceThreshold} THEN CONCAT('缺勤 ', att.absenceCount, ' 次') END,",
            "CASE WHEN sc.averageScore IS NOT NULL AND sc.averageScore &lt; #{averageScoreThreshold} THEN CONCAT('学期均分 ', ROUND(sc.averageScore, 1)) END) AS riskReasons",
            "FROM students s JOIN users u ON u.id = s.user_id AND u.is_deleted = 0",
            "LEFT JOIN majors m ON m.code = s.major_code AND m.grade = s.grade AND m.class_no = s.class_no AND m.is_deleted = 0",
            "LEFT JOIN (SELECT latest.student_id, SUM(latest.score &lt; 60) AS failedCourseCount, AVG(latest.score) AS averageScore",
            "FROM (SELECT sc.student_id, COALESCE(c.catalog_id, c.id) AS catalogId, sc.score,",
            "ROW_NUMBER() OVER (PARTITION BY sc.student_id, COALESCE(c.catalog_id, c.id) ORDER BY sc.attempt_no DESC, sc.exam_time DESC, sc.id DESC) AS rowNo",
            "FROM scores sc JOIN courses c ON c.id = sc.course_id AND c.is_deleted = 0",
            "WHERE sc.semester = #{termCode} AND sc.publish_status = 'PUBLISHED' AND sc.is_deleted = 0) latest",
            "WHERE latest.rowNo = 1 GROUP BY latest.student_id) sc ON sc.student_id = s.id",
            "LEFT JOIN (SELECT ar.student_id, COUNT(*) AS absenceCount FROM attendance_records ar",
            "JOIN courses c ON c.id = ar.course_id AND c.is_deleted = 0",
            "WHERE c.semester = #{termCode} AND ar.status = '缺勤' AND ar.is_deleted = 0 GROUP BY ar.student_id) att ON att.student_id = s.id",
            "WHERE s.status = 0 AND s.is_deleted = 0",
            "AND (COALESCE(sc.failedCourseCount, 0) &gt; 0 OR COALESCE(att.absenceCount, 0) &gt;= #{absenceThreshold} OR sc.averageScore &lt; #{averageScoreThreshold})",
            "<if test='keyword != null and keyword != \"\"'>AND (s.student_no LIKE CONCAT('%', #{keyword}, '%') OR u.real_name LIKE CONCAT('%', #{keyword}, '%') OR m.name LIKE CONCAT('%', #{keyword}, '%'))</if>",
            "ORDER BY COALESCE(sc.failedCourseCount, 0) DESC, COALESCE(att.absenceCount, 0) DESC, sc.averageScore ASC, s.student_no ASC",
            "</script>"})
    Page<AcademicRiskStudentVO> selectRiskStudents(Page<AcademicRiskStudentVO> page,
            @Param("termCode") String termCode, @Param("keyword") String keyword,
            @Param("absenceThreshold") int absenceThreshold, @Param("averageScoreThreshold") int averageScoreThreshold);
}
