package com.example.studentsmanager.model.vo.attendance;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AttendanceStatsVO {
    private Long totalCount = 0L;
    private Long presentCount = 0L;
    private Long lateCount = 0L;
    private Long earlyLeaveCount = 0L;
    private Long absentCount = 0L;
    private Long leaveCount = 0L;
    private BigDecimal attendanceRate = BigDecimal.ZERO;
}
