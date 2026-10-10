package com.example.studentsmanager.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentsmanager.core.response.Result;
import com.example.studentsmanager.mapper.AcademicRiskMapper;
import com.example.studentsmanager.model.vo.student.AcademicRiskStudentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/academic-risks")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AcademicRiskController {
    private final AcademicRiskMapper academicRiskMapper;

    @GetMapping
    public Result<Page<AcademicRiskStudentVO>> getRiskStudents(
            @RequestParam String term,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "3") int absenceThreshold,
            @RequestParam(defaultValue = "60") int averageScoreThreshold) {
        String termCode = term.trim();
        Page<AcademicRiskStudentVO> result = academicRiskMapper.selectRiskStudents(
                new Page<>(Math.max(1, page), Math.min(100, Math.max(1, size))), termCode,
                keyword == null || keyword.isBlank() ? null : keyword.trim(),
                Math.max(1, absenceThreshold), Math.min(100, Math.max(0, averageScoreThreshold)));
        return Result.success(result);
    }
}
