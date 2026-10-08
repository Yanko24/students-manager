package com.example.studentsmanager.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentsmanager.model.vo.course.CourseSelectionStudentVO;
import com.example.studentsmanager.model.vo.course.CourseSelectionReviewRecord;

import java.util.List;

@Mapper
public interface CourseSelectionMapper {
    @Select({
            "SELECT s.id AS studentId, s.student_no AS studentNo, u.real_name AS studentName,",
            "m.name AS majorName, s.grade, s.class_no AS classNo, cs.selection_date AS selectionDate",
            "FROM course_selections cs",
            "JOIN students s ON s.id = cs.student_id AND s.is_deleted = 0",
            "JOIN users u ON u.id = s.user_id AND u.is_deleted = 0",
            "LEFT JOIN majors m ON m.code = s.major_code AND m.grade = s.grade AND m.class_no = s.class_no AND m.is_deleted = 0",
            "WHERE cs.course_id = #{courseId} AND cs.status = 'approved' AND cs.is_deleted = 0",
            "ORDER BY cs.selection_date DESC, s.student_no ASC"
    })
    Page<CourseSelectionStudentVO> selectStudentsByCourse(Page<CourseSelectionStudentVO> page, @Param("courseId") Long courseId);

    @Select({
            "<script>",
            "SELECT cs.id AS selectionId, s.id AS studentId, s.student_no AS studentNo, u.real_name AS studentName,",
            "m.name AS majorName, s.grade, s.class_no AS classNo, cs.selection_date AS selectionDate, cs.status AS selectionStatus,",
            "CASE WHEN cs.status = 'waitlisted' THEN (SELECT COUNT(*) FROM course_selections ahead WHERE ahead.course_id = cs.course_id AND ahead.status = 'waitlisted' AND ahead.is_deleted = 0 AND (ahead.selection_date &lt; cs.selection_date OR (ahead.selection_date = cs.selection_date AND ahead.id &lt;= cs.id))) ELSE NULL END AS waitlistPosition",
            "FROM course_selections cs",
            "JOIN students s ON s.id = cs.student_id AND s.is_deleted = 0",
            "JOIN users u ON u.id = s.user_id AND u.is_deleted = 0",
            "LEFT JOIN majors m ON m.code = s.major_code AND m.grade = s.grade AND m.class_no = s.class_no AND m.is_deleted = 0",
            "WHERE cs.course_id = #{courseId} AND cs.is_deleted = 0",
            "<if test='status != null and status != \"ALL\"'>AND cs.status = #{status}</if>",
            "ORDER BY CASE cs.status WHEN 'pending' THEN 0 WHEN 'waitlisted' THEN 1 WHEN 'approved' THEN 2 ELSE 3 END, cs.selection_date ASC, s.student_no ASC",
            "</script>"
    })
    Page<CourseSelectionStudentVO> selectSelectionsByCourse(Page<CourseSelectionStudentVO> page,
                                                              @Param("courseId") Long courseId,
                                                              @Param("status") String status);

    @Select({
            "<script>",
            "SELECT id AS selectionId, student_id AS studentId FROM course_selections",
            "WHERE course_id = #{courseId} AND status = 'pending' AND is_deleted = 0 AND id IN",
            "<foreach collection='selectionIds' item='selectionId' open='(' separator=',' close=')'>#{selectionId}</foreach>",
            "FOR UPDATE",
            "</script>"
    })
    List<CourseSelectionReviewRecord> selectPendingForReview(@Param("courseId") Long courseId,
                                                               @Param("selectionIds") List<Long> selectionIds);

    @Select({
            "<script>",
            "SELECT COUNT(*) FROM course_selections WHERE course_id = #{courseId} AND student_id = #{studentId}",
            "AND status IN ('pending', 'approved', 'waitlisted') AND is_deleted = 0 AND id NOT IN",
            "<foreach collection='selectionIds' item='selectionId' open='(' separator=',' close=')'>#{selectionId}</foreach>",
            "</script>"
    })
    int countOtherActiveForStudentAndCourse(@Param("courseId") Long courseId,
                                            @Param("studentId") Long studentId,
                                            @Param("selectionIds") List<Long> selectionIds);

    @Update({
            "<script>",
            "UPDATE course_selections SET status = #{status}, update_time = CURRENT_TIMESTAMP, update_by = #{actor}",
            "WHERE course_id = #{courseId} AND status = 'pending' AND is_deleted = 0 AND id IN",
            "<foreach collection='selectionIds' item='selectionId' open='(' separator=',' close=')'>#{selectionId}</foreach>",
            "</script>"
    })
    int reviewPending(@Param("courseId") Long courseId, @Param("selectionIds") List<Long> selectionIds,
                      @Param("status") String status, @Param("actor") String actor);

    @Select("SELECT id FROM course_selections WHERE course_id = #{courseId} AND status = 'waitlisted' AND is_deleted = 0 ORDER BY selection_date ASC, id ASC LIMIT #{limit} FOR UPDATE")
    List<Long> selectNextWaitlistedForUpdate(@Param("courseId") Long courseId, @Param("limit") int limit);

    @Update({
            "<script>",
            "UPDATE course_selections SET status = 'pending', update_time = CURRENT_TIMESTAMP, update_by = #{actor}",
            "WHERE course_id = #{courseId} AND status = 'waitlisted' AND is_deleted = 0 AND id IN",
            "<foreach collection='selectionIds' item='selectionId' open='(' separator=',' close=')'>#{selectionId}</foreach>",
            "</script>"
    })
    int promoteWaitlisted(@Param("courseId") Long courseId, @Param("selectionIds") List<Long> selectionIds,
                          @Param("actor") String actor);

    @Select("SELECT COUNT(*) FROM course_selections WHERE course_id = #{courseId} AND status IN ('pending', 'approved') AND is_deleted = 0")
    int countActiveForCourse(@Param("courseId") Long courseId);

    @Select("SELECT DISTINCT student_id FROM course_selections WHERE course_id = #{courseId} AND status IN ('pending', 'approved', 'waitlisted') AND is_deleted = 0")
    List<Long> selectActiveStudentIds(@Param("courseId") Long courseId);

    @Select("SELECT id FROM course_selections WHERE course_id = #{courseId} AND status IN ('pending', 'approved') AND is_deleted = 0 FOR UPDATE")
    List<Long> selectActiveForCourseForUpdate(@Param("courseId") Long courseId);

    @Select("SELECT id FROM course_selections WHERE student_id = #{studentId} AND course_id = #{courseId} AND status IN ('pending', 'approved', 'waitlisted') AND is_deleted = 0 FOR UPDATE")
    List<Long> selectActiveForStudentAndCourseForUpdate(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    @Insert("INSERT INTO course_selections (student_id, course_id, selection_date, status, create_by, update_by) VALUES (#{studentId}, #{courseId}, CURRENT_TIMESTAMP, 'pending', #{actor}, #{actor})")
    int insertPending(@Param("studentId") Long studentId, @Param("courseId") Long courseId, @Param("actor") String actor);

    @Insert("INSERT INTO course_selections (student_id, course_id, selection_date, status, create_by, update_by) VALUES (#{studentId}, #{courseId}, CURRENT_TIMESTAMP, 'waitlisted', #{actor}, #{actor})")
    int insertWaitlisted(@Param("studentId") Long studentId, @Param("courseId") Long courseId, @Param("actor") String actor);

    @Update("UPDATE course_selections SET is_deleted = 1, update_time = CURRENT_TIMESTAMP, update_by = #{actor} WHERE student_id = #{studentId} AND course_id = #{courseId} AND status IN ('pending', 'approved', 'waitlisted') AND is_deleted = 0")
    int dropSelection(@Param("studentId") Long studentId, @Param("courseId") Long courseId, @Param("actor") String actor);
}
