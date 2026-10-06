package com.example.studentsmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentsmanager.core.response.Result;
import com.example.studentsmanager.model.dto.attendance.AttendanceQueryDTO;
import com.example.studentsmanager.model.dto.attendance.AttendanceUpdateDTO;
import com.example.studentsmanager.model.vo.attendance.AttendanceStatsVO;
import com.example.studentsmanager.model.vo.attendance.AttendanceTrendVO;
import com.example.studentsmanager.model.vo.attendance.AttendanceVO;
import com.example.studentsmanager.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Validated
@Tag(name = "考勤管理")
public class AttendanceController {
    private final AttendanceService attendanceService;

    @GetMapping
    @Operation(summary = "分页查询考勤记录", description = "管理员按条件分页查询全校考勤记录")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<AttendanceVO>> getAdminAttendance(AttendanceQueryDTO query) {
        return Result.success(attendanceService.getAdminAttendancePage(query));
    }

    @GetMapping("/teacher")
    @Operation(summary = "查询教师考勤记录", description = "查询当前教师负责课程的考勤记录")
    @PreAuthorize("hasRole('TEACHER')")
    public Result<Page<AttendanceVO>> getTeacherAttendance(AttendanceQueryDTO query, Principal principal) {
        return Result.success(attendanceService.getTeacherAttendancePage(principal.getName(), query));
    }

    @GetMapping("/student")
    @Operation(summary = "查询个人考勤记录", description = "查询当前学生的考勤记录")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<Page<AttendanceVO>> getStudentAttendance(AttendanceQueryDTO query, Principal principal) {
        return Result.success(attendanceService.getStudentAttendancePage(principal.getName(), query));
    }

    @GetMapping("/student/statistics")
    @Operation(summary = "查询个人考勤统计", description = "统计当前学生的考勤情况")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<AttendanceStatsVO> getStudentStatistics(AttendanceQueryDTO query, Principal principal) {
        return Result.success(attendanceService.getStudentAttendanceStats(principal.getName(), query));
    }

    @GetMapping("/statistics")
    @Operation(summary = "查询考勤统计", description = "按时间范围统计全校考勤情况")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AttendanceStatsVO> getStatistics(@RequestParam(defaultValue = "week") String period) {
        return Result.success(attendanceService.getAttendanceStats(period));
    }

    @GetMapping("/trend")
    @Operation(summary = "查询考勤趋势", description = "按时间范围汇总考勤变化趋势")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<AttendanceTrendVO>> getTrend(@RequestParam(defaultValue = "week") String period) {
        return Result.success(attendanceService.getAttendanceTrend(period));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询考勤详情", description = "根据记录编号查询考勤详情")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AttendanceVO> getAttendance(@PathVariable Long id) {
        return Result.success(attendanceService.getAttendance(id));
    }

    @PostMapping
    @Operation(summary = "新增考勤记录", description = "创建一条考勤记录")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AttendanceVO> createAttendance(@Valid @RequestBody AttendanceUpdateDTO dto) {
        return Result.success(attendanceService.createAttendance(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新考勤记录", description = "根据记录编号更新考勤信息")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AttendanceVO> updateAttendance(@PathVariable Long id, @Valid @RequestBody AttendanceUpdateDTO dto) {
        return Result.success(attendanceService.updateAttendance(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除考勤记录", description = "根据记录编号删除考勤信息")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return Result.success();
    }
}
