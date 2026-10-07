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
            "c.course_type AS type, c.semester, c.status, c.selection_open AS selectionOpen, c.max_students AS maxStudents,",
            "c.selection_scope AS selectionScope, c.selection_college_id AS selectionCollegeId, c.selection_major_code AS selectionMajorCode, c.selection_grade AS selectionGrade,",
            "scope_college.name AS selectionCollegeName, (SELECT sm.name FROM majors sm WHERE sm.code = c.selection_major_code AND sm.is_deleted = 0 ORDER BY sm.grade DESC, sm.class_no ASC LIMIT 1) AS selectionMajorName,",
            "(SELECT COUNT(*) FROM course_selections active WHERE active.course_id = c.id AND active.status IN ('pending', 'approved') AND active.is_deleted = 0) AS selectedCount,",
            "CASE c.status WHEN 0 THEN '未开课' WHEN 1 THEN '已开课' WHEN 2 THEN '已结课' ELSE '未知' END AS statusText,",
            "c.description, c.objectives, cs.selection_status AS selectionStatus, cs.selection_date AS selectionDate",
            "FROM courses c",
            "JOIN students current_student ON current_student.id = #{studentId} AND current_student.is_deleted = 0",
            "LEFT JOIN majors current_major ON current_major.code = current_student.major_code AND current_major.grade = current_student.grade AND current_major.class_no = current_student.class_no AND current_major.is_deleted = 0",
            "LEFT JOIN colleges scope_college ON scope_college.id = c.selection_college_id AND scope_college.is_deleted = 0",
            "LEFT JOIN teachers t ON t.id = c.teacher_id AND t.is_deleted = 0",
            "LEFT JOIN users u ON u.id = t.user_id AND u.is_deleted = 0",
            "LEFT JOIN (SELECT student_id, course_id, CASE WHEN SUM(status = 'approved') &gt; 0 THEN 'approved' WHEN SUM(status = 'pending') &gt; 0 THEN 'pending' ELSE 'rejected' END AS selection_status, MAX(selection_date) AS selection_date FROM course_selections WHERE is_deleted = 0 GROUP BY student_id, course_id) cs ON cs.course_id = c.id AND cs.student_id = #{studentId}",
            "WHERE c.is_deleted = 0 AND c.selection_open = 1 AND c.status &lt;&gt; 2",
            "AND ((c.selection_scope = 'ALL' AND (c.selection_grade IS NULL OR c.selection_grade = current_student.grade)) OR (c.selection_scope = 'COLLEGE' AND c.selection_college_id = current_major.college_id AND (c.selection_grade IS NULL OR c.selection_grade = current_student.grade)) OR (c.selection_scope = 'MAJOR' AND c.selection_major_code = current_student.major_code AND c.selection_college_id = current_major.college_id AND (c.selection_grade IS NULL OR c.selection_grade = current_student.grade)))",
            "<if test='courseName != null'>AND c.course_name LIKE CONCAT('%', #{courseName}, '%')</if>",
            "<if test='semester != null'>AND c.semester = #{semester}</if>",
            "ORDER BY c.semester DESC, c.course_name ASC",
            "</script>"
    })
    Page<CourseVO> selectAvailableCourses(Page<CourseVO> page,
                                          @Param("studentId") Long studentId,
                                          @Param("courseName") String courseName,
                                          @Param("semester") String semester);

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
