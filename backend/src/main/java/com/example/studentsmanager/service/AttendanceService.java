package com.example.studentsmanager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentsmanager.model.dto.attendance.AttendanceQueryDTO;
import com.example.studentsmanager.model.dto.attendance.AttendanceUpdateDTO;
import com.example.studentsmanager.model.entity.Attendance;
import com.example.studentsmanager.model.vo.attendance.AttendanceStatsVO;
import com.example.studentsmanager.model.vo.attendance.AttendanceTrendVO;
import com.example.studentsmanager.model.vo.attendance.AttendanceVO;
import com.baomidou.mybatisplus.spring.service.IService;

import java.util.List;

public interface AttendanceService extends IService<Attendance> {
    Page<AttendanceVO> getAdminAttendancePage(AttendanceQueryDTO query);
    Page<AttendanceVO> getTeacherAttendancePage(String username, AttendanceQueryDTO query);
    Page<AttendanceVO> getStudentAttendancePage(String username, AttendanceQueryDTO query);
    AttendanceStatsVO getStudentAttendanceStats(String username, AttendanceQueryDTO query);
    AttendanceVO getAttendance(Long id);
    AttendanceVO createAttendance(AttendanceUpdateDTO dto);
    AttendanceVO updateAttendance(Long id, AttendanceUpdateDTO dto);
    void deleteAttendance(Long id);
    AttendanceStatsVO getAttendanceStats(String period);
    List<AttendanceTrendVO> getAttendanceTrend(String period);
}
