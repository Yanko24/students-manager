package com.example.studentsmanager.model.vo.attendance;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AttendanceTrendVO {
    private LocalDate date;
    private Long totalCount = 0L;
    private Long attendedCount = 0L;
    private BigDecimal attendanceRate = BigDecimal.ZERO;
}
