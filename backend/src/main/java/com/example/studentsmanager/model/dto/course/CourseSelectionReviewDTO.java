package com.example.studentsmanager.model.dto.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CourseSelectionReviewDTO {
    @NotEmpty(message = "请选择需要处理的选课申请")
    @Size(max = 100, message = "每次最多处理100条申请")
    private List<Long> selectionIds;

    @NotBlank(message = "请选择审核操作")
    @Pattern(regexp = "(?i)APPROVE|REJECT", message = "审核操作无效")
    private String action;
}
