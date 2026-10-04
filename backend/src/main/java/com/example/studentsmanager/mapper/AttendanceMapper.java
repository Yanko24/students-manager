package com.example.studentsmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentsmanager.model.dto.attendance.AttendanceQueryDTO;
import com.example.studentsmanager.model.entity.Attendance;
import com.example.studentsmanager.model.vo.attendance.AttendanceStatsVO;
import com.example.studentsmanager.model.vo.attendance.AttendanceTrendVO;
import com.example.studentsmanager.model.vo.attendance.AttendanceVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AttendanceMapper extends BaseMapper<Attendance> {
    @Select({
            "<script>",
            "SELECT a.id, a.student_id AS studentId, s.student_no AS studentNo, su.real_name AS studentName,",
            "CONCAT(m.name, ' ', s.grade, '级', s.class_no, '班') AS className,",
            "a.course_id AS courseId, c.course_code AS courseCode, c.course_name AS courseName, tu.real_name AS teacherName,",
            "a.attendance_date AS date, a.class_period AS classPeriod, a.status, a.remark, a.create_time AS createTime, a.update_time AS updateTime",
            "FROM attendance_records a",
            "JOIN students s ON s.id = a.student_id AND s.is_deleted = 0",
            "JOIN users su ON su.id = s.user_id AND su.is_deleted = 0",
            "JOIN courses c ON c.id = a.course_id AND c.is_deleted = 0",
            "LEFT JOIN teachers t ON t.id = c.teacher_id AND t.is_deleted = 0",
            "LEFT JOIN users tu ON tu.id = t.user_id AND tu.is_deleted = 0",
            "JOIN majors m ON m.code = s.major_code AND m.grade = s.grade AND m.class_no = s.class_no AND m.is_deleted = 0",
            "WHERE a.is_deleted = 0",
            "<if test='studentId != null'>AND a.student_id = #{studentId}</if>",
            "<if test='teacherId != null'>AND c.teacher_id = #{teacherId}</if>",
            "<if test='query.studentNo != null'>AND s.student_no LIKE CONCAT('%', #{query.studentNo}, '%')</if>",
            "<if test='query.studentName != null'>AND su.real_name LIKE CONCAT('%', #{query.studentName}, '%')</if>",
            "<if test='query.courseName != null'>AND c.course_name LIKE CONCAT('%', #{query.courseName}, '%')</if>",
            "<if test='query.courseId != null'>AND c.id = #{query.courseId}</if>",
            "<if test='query.className != null'>AND CONCAT(m.name, ' ', s.grade, '级', s.class_no, '班') LIKE CONCAT('%', #{query.className}, '%')</if>",
            "<if test='query.semester != null'>AND c.semester = #{query.semester}</if>",
            "<if test='query.date != null'>AND a.attendance_date = #{query.date}</if>",
            "<if test='query.status != null'>AND a.status = #{query.status}</if>",
            "ORDER BY a.attendance_date DESC, a.id DESC",
            "</script>"
    })
    Page<AttendanceVO> selectAttendancePage(Page<AttendanceVO> page,
                                            @Param("query") AttendanceQueryDTO query,
                                            @Param("studentId") Long studentId,
                                            @Param("teacherId") Long teacherId);

    @Select("SELECT a.id, a.student_id AS studentId, s.student_no AS studentNo, su.real_name AS studentName, " +
            "CONCAT(m.name, ' ', s.grade, '级', s.class_no, '班') AS className, a.course_id AS courseId, " +
            "c.course_code AS courseCode, c.course_name AS courseName, tu.real_name AS teacherName, " +
            "a.attendance_date AS date, a.class_period AS classPeriod, a.status, a.remark, a.create_time AS createTime, a.update_time AS updateTime " +
            "FROM attendance_records a JOIN students s ON s.id=a.student_id JOIN users su ON su.id=s.user_id " +
            "JOIN courses c ON c.id=a.course_id LEFT JOIN teachers t ON t.id=c.teacher_id LEFT JOIN users tu ON tu.id=t.user_id " +
            "JOIN majors m ON m.code=s.major_code AND m.grade=s.grade AND m.class_no=s.class_no " +
            "WHERE a.id=#{id} AND a.is_deleted=0 AND s.is_deleted=0 AND su.is_deleted=0 AND c.is_deleted=0 AND m.is_deleted=0")
    AttendanceVO selectAttendanceById(@Param("id") Long id);

    @Select("SELECT COUNT(*) AS totalCount, " +
            "COALESCE(SUM(status = '正常'), 0) AS presentCount, COALESCE(SUM(status = '迟到'), 0) AS lateCount, " +
            "COALESCE(SUM(status = '早退'), 0) AS earlyLeaveCount, COALESCE(SUM(status = '缺勤'), 0) AS absentCount, " +
            "COALESCE(SUM(status = '请假'), 0) AS leaveCount, " +
            "COALESCE(ROUND(SUM(status IN ('正常', '迟到', '早退')) * 100.0 / NULLIF(SUM(status <> '请假'), 0), 1), 0) AS attendanceRate " +
            "FROM attendance_records WHERE is_deleted=0 AND attendance_date BETWEEN #{startDate} AND #{endDate}")
    AttendanceStatsVO selectAttendanceStats(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Select({
            "<script>",
            "SELECT COUNT(*) AS totalCount, COALESCE(SUM(a.status = '正常'), 0) AS presentCount,",
            "COALESCE(SUM(a.status = '迟到'), 0) AS lateCount, COALESCE(SUM(a.status = '早退'), 0) AS earlyLeaveCount,",
            "COALESCE(SUM(a.status = '缺勤'), 0) AS absentCount, COALESCE(SUM(a.status = '请假'), 0) AS leaveCount,",
            "COALESCE(ROUND(SUM(a.status IN ('正常', '迟到', '早退')) * 100.0 / NULLIF(SUM(a.status != '请假'), 0), 1), 0) AS attendanceRate",
            "FROM attendance_records a JOIN courses c ON c.id = a.course_id AND c.is_deleted = 0",
            "WHERE a.is_deleted = 0 AND a.student_id = #{studentId}",
            "<if test='query.courseName != null'>AND c.course_name LIKE CONCAT('%', #{query.courseName}, '%')</if>",
            "<if test='query.semester != null'>AND c.semester = #{query.semester}</if>",
            "</script>"
    })
    AttendanceStatsVO selectStudentAttendanceStats(@Param("studentId") Long studentId,
                                                   @Param("query") AttendanceQueryDTO query);

    @Select("SELECT attendance_date AS date, COUNT(*) AS totalCount, " +
            "SUM(status IN ('正常', '迟到', '早退')) AS attendedCount, " +
            "COALESCE(ROUND(SUM(status IN ('正常', '迟到', '早退')) * 100.0 / NULLIF(SUM(status <> '请假'), 0), 1), 0) AS attendanceRate " +
            "FROM attendance_records WHERE is_deleted=0 AND attendance_date BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY attendance_date ORDER BY attendance_date")
    List<AttendanceTrendVO> selectAttendanceTrend(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
