package com.example.studentsmanager.model.dto.attendance;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class AttendanceUpdateDTO {
    private Long studentId;
    private String studentNo;
    @NotNull
    private Long courseId;
    @NotNull
    private LocalDate date;
    private String classPeriod;
    @NotBlank
    private String status;
    private String remark;
}
