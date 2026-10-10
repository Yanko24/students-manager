package com.example.studentsmanager.controller;

import com.example.studentsmanager.core.response.Result;
import com.example.studentsmanager.model.dto.AcademicTermDTO;
import com.example.studentsmanager.model.entity.AcademicTerm;
import com.example.studentsmanager.service.impl.AcademicTermService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/admin/academic-terms")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AcademicTermController {
    private final AcademicTermService academicTermService;

    @GetMapping
    public Result<List<AcademicTerm>> list() { return Result.success(academicTermService.getAllTerms()); }

    @GetMapping("/options")
    public Result<List<AcademicTerm>> options() { return Result.success(academicTermService.getActiveTerms()); }

    @GetMapping("/current")
    public Result<AcademicTerm> current() { return Result.success(academicTermService.getCurrentTerm()); }

    @PostMapping
    public Result<AcademicTerm> create(@RequestBody AcademicTermDTO dto, Principal principal) {
        return Result.success(academicTermService.create(dto, principal.getName()));
    }

    @PutMapping("/{id}")
    public Result<AcademicTerm> update(@PathVariable Long id, @RequestBody AcademicTermDTO dto, Principal principal) {
        return Result.success(academicTermService.update(id, dto, principal.getName()));
    }

    @PutMapping("/{id}/current")
    public Result<AcademicTerm> setCurrent(@PathVariable Long id, Principal principal) {
        return Result.success(academicTermService.setCurrent(id, principal.getName()));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        academicTermService.delete(id);
        return Result.success();
    }
}
