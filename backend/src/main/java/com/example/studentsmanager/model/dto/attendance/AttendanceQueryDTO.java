package com.example.studentsmanager.model.dto.attendance;

import com.example.studentsmanager.model.dto.BaseQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class AttendanceQueryDTO extends BaseQueryDTO {
    private String studentNo;
    private String studentName;
    private String courseName;
    private Long courseId;
    private String className;
    private String semester;
    private LocalDate date;
    private String status;
}
