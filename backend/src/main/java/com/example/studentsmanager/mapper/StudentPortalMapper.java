package com.example.studentsmanager.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentsmanager.model.vo.course.CourseVO;
import com.example.studentsmanager.model.vo.score.ScoreVO;
import com.example.studentsmanager.model.vo.score.StudentScoreStatsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StudentPortalMapper {
    @Select({
            "<script>",
            "SELECT c.id, c.course_code AS code, c.course_name AS name, c.teacher_id AS teacherId,",
            "u.real_name AS teacher, t.department AS college, c.credits AS credit, c.hours,",
            "c.course_type AS type, c.semester, c.status,",
            "CASE c.status WHEN 0 THEN '未开课' WHEN 1 THEN '已开课' WHEN 2 THEN '已结课' ELSE '未知' END AS statusText,",
            "c.description, c.objectives, cs.status AS selectionStatus, cs.selection_date AS selectionDate",
            "FROM course_selections cs",
            "JOIN students s ON s.id = cs.student_id AND s.is_deleted = 0",
            "JOIN courses c ON c.id = cs.course_id AND c.is_deleted = 0",
            "LEFT JOIN teachers t ON t.id = c.teacher_id AND t.is_deleted = 0",
            "LEFT JOIN users u ON u.id = t.user_id AND u.is_deleted = 0",
            "WHERE s.user_id = #{userId} AND cs.status = 'approved' AND cs.is_deleted = 0",
            "<if test='courseName != null'>AND c.course_name LIKE CONCAT('%', #{courseName}, '%')</if>",
            "<if test='semester != null'>AND c.semester = #{semester}</if>",
            "ORDER BY c.semester DESC, c.course_name ASC",
            "</script>"
    })
    Page<CourseVO> selectStudentCourses(Page<CourseVO> page,
                                        @Param("userId") Long userId,
                                        @Param("courseName") String courseName,
                                        @Param("semester") String semester);

    @Select({
            "SELECT c.id, c.course_code AS code, c.course_name AS name, c.teacher_id AS teacherId,",
            "u.real_name AS teacher, t.department AS college, c.credits AS credit, c.hours,",
            "c.course_type AS type, c.semester, c.status,",
            "CASE c.status WHEN 0 THEN '未开课' WHEN 1 THEN '已开课' WHEN 2 THEN '已结课' ELSE '未知' END AS statusText,",
            "c.description, c.objectives, cs.status AS selectionStatus, cs.selection_date AS selectionDate",
            "FROM course_selections cs",
            "JOIN students s ON s.id = cs.student_id AND s.is_deleted = 0",
            "JOIN courses c ON c.id = cs.course_id AND c.is_deleted = 0",
            "LEFT JOIN teachers t ON t.id = c.teacher_id AND t.is_deleted = 0",
            "LEFT JOIN users u ON u.id = t.user_id AND u.is_deleted = 0",
            "WHERE s.user_id = #{userId} AND cs.status = 'approved' AND cs.is_deleted = 0 AND c.id = #{courseId}"
    })
    CourseVO selectStudentCourse(@Param("userId") Long userId, @Param("courseId") Long courseId);

    @Select({
            "<script>",
            "SELECT sc.id, sc.student_id AS studentId, sc.course_id AS courseId,",
            "c.course_code AS courseCode, c.course_name AS courseName, c.credits AS credit,",
            "sc.score, sc.grade, sc.grade_point AS gradePoint, sc.semester, sc.exam_time AS examTime,",
            "sc.remarks AS comment, CASE WHEN sc.score &gt;= 60 THEN '合格' ELSE '不合格' END AS status,",
            "tuser.real_name AS teacher",
            "FROM scores sc",
            "JOIN students s ON s.id = sc.student_id AND s.is_deleted = 0",
            "JOIN courses c ON c.id = sc.course_id AND c.is_deleted = 0",
            "LEFT JOIN teachers t ON t.id = c.teacher_id AND t.is_deleted = 0",
            "LEFT JOIN users tuser ON tuser.id = t.user_id AND tuser.is_deleted = 0",
            "WHERE s.user_id = #{userId} AND sc.is_deleted = 0",
            "<if test='courseName != null'>AND c.course_name LIKE CONCAT('%', #{courseName}, '%')</if>",
            "<if test='semester != null'>AND sc.semester = #{semester}</if>",
            "ORDER BY sc.exam_time DESC, sc.id DESC",
            "</script>"
    })
    Page<ScoreVO> selectStudentScores(Page<ScoreVO> page,
                                      @Param("userId") Long userId,
                                      @Param("courseName") String courseName,
                                      @Param("semester") String semester);

    @Select({
            "<script>",
            "SELECT COUNT(*) AS scoreCount, COALESCE(ROUND(AVG(sc.score), 2), 0) AS averageScore,",
            "COALESCE(ROUND(SUM(sc.score &gt;= 60) * 100.0 / NULLIF(COUNT(*), 0), 1), 0) AS passRate,",
            "COALESCE(ROUND(SUM(sc.score &gt;= 90) * 100.0 / NULLIF(COUNT(*), 0), 1), 0) AS excellentRate,",
            "COALESCE(SUM(CASE WHEN sc.score &gt;= 60 THEN c.credits ELSE 0 END), 0) AS earnedCredits",
            "FROM scores sc JOIN students s ON s.id = sc.student_id AND s.is_deleted = 0",
            "JOIN courses c ON c.id = sc.course_id AND c.is_deleted = 0",
            "WHERE s.user_id = #{userId} AND sc.is_deleted = 0",
            "<if test='semester != null'>AND sc.semester = #{semester}</if>",
            "</script>"
    })
    StudentScoreStatsVO selectStudentScoreStats(@Param("userId") Long userId, @Param("semester") String semester);
}
