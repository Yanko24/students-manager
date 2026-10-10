package com.example.studentsmanager.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentsmanager.core.response.Result;
import com.example.studentsmanager.model.dto.curriculum.CurriculumPlanDTO;
import com.example.studentsmanager.model.vo.curriculum.CurriculumMajorOptionVO;
import com.example.studentsmanager.model.vo.curriculum.CurriculumPlanVO;
import com.example.studentsmanager.service.impl.CurriculumPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/admin/curriculum-plans")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class CurriculumPlanController {
    private final CurriculumPlanService curriculumPlanService;

    @GetMapping("/major-options")
    public Result<List<CurriculumMajorOptionVO>> getMajorOptions() {
        return Result.success(curriculumPlanService.getMajorOptions());
    }

    @GetMapping
    public Result<Page<CurriculumPlanVO>> getPage(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size, @RequestParam(required = false) String majorCode,
            @RequestParam(required = false) String grade) {
        return Result.success(curriculumPlanService.getPage(page, size, majorCode, grade));
    }

    @PostMapping
    public Result<CurriculumPlanVO> create(@RequestBody CurriculumPlanDTO dto, Principal principal) {
        return Result.success(curriculumPlanService.create(dto, principal.getName()));
    }

    @PutMapping("/{id}")
    public Result<CurriculumPlanVO> update(@PathVariable Long id, @RequestBody CurriculumPlanDTO dto, Principal principal) {
        return Result.success(curriculumPlanService.update(id, dto, principal.getName()));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        curriculumPlanService.delete(id);
        return Result.success();
    }
}
