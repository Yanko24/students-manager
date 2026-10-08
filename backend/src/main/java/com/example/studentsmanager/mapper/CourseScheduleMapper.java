package com.example.studentsmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentsmanager.model.entity.CourseSchedule;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CourseScheduleMapper extends BaseMapper<CourseSchedule> {
    @Select("SELECT * FROM course_schedules WHERE course_id = #{courseId} ORDER BY day_of_week, start_period")
    List<CourseSchedule> selectByCourseId(@Param("courseId") Long courseId);

    @Select({"<script>", "SELECT * FROM course_schedules WHERE course_id IN",
            "<foreach collection='courseIds' item='courseId' open='(' separator=',' close=')'>#{courseId}</foreach>",
            "ORDER BY course_id, day_of_week, start_period", "</script>"})
    List<CourseSchedule> selectByCourseIds(@Param("courseIds") List<Long> courseIds);

    @Select({"<script>", "SELECT s.* FROM course_schedules s JOIN courses c ON c.id = s.course_id AND c.is_deleted = 0",
            "WHERE c.semester = #{semester} AND s.day_of_week = #{dayOfWeek}",
            "AND (c.teacher_id = #{teacherId} OR s.classroom = #{classroom})",
            "<if test='excludeCourseId != null'>AND c.id &lt;&gt; #{excludeCourseId}</if>", "</script>"})
    List<CourseSchedule> selectTeacherRoomCandidates(@Param("semester") String semester,
                                                     @Param("dayOfWeek") Integer dayOfWeek,
                                                     @Param("teacherId") Long teacherId,
                                                     @Param("classroom") String classroom,
                                                     @Param("excludeCourseId") Long excludeCourseId);

    @Select({"<script>", "SELECT s.* FROM course_schedules s JOIN courses c ON c.id = s.course_id AND c.is_deleted = 0",
            "JOIN course_selections cs ON cs.course_id = c.id AND cs.is_deleted = 0",
            "WHERE cs.student_id = #{studentId} AND cs.status IN ('pending', 'approved', 'waitlisted')",
            "AND c.semester = #{semester} AND s.day_of_week = #{dayOfWeek}",
            "<if test='excludeCourseId != null'>AND c.id &lt;&gt; #{excludeCourseId}</if>", "</script>"})
    List<CourseSchedule> selectStudentCandidates(@Param("studentId") Long studentId,
                                                 @Param("semester") String semester,
                                                 @Param("dayOfWeek") Integer dayOfWeek,
                                                 @Param("excludeCourseId") Long excludeCourseId);

    @Delete("DELETE FROM course_schedules WHERE course_id = #{courseId}")
    int deleteByCourseId(@Param("courseId") Long courseId);
}
