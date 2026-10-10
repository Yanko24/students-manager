package com.example.studentsmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentsmanager.core.response.Result;
import com.example.studentsmanager.model.dto.course.CourseQueryDTO;
import com.example.studentsmanager.model.dto.course.CourseUpdateDTO;
import com.example.studentsmanager.model.dto.course.CourseSelectionReviewDTO;
import com.example.studentsmanager.model.vo.course.CourseVO;
import com.example.studentsmanager.model.vo.course.CourseSelectionStudentVO;
import com.example.studentsmanager.model.entity.CourseCatalog;
import com.example.studentsmanager.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "课程管理")
public class CourseController {
    private final CourseService courseService;

    @GetMapping
    @Operation(summary = "分页查询课程", description = "根据查询条件分页获取课程列表")
    public Result<Page<CourseVO>> getCoursePage(CourseQueryDTO query) { return Result.success(courseService.getCoursePage(query)); }

    @GetMapping("/{id}")
    @Operation(summary = "查询课程详情", description = "根据课程编号获取课程信息")
    public Result<CourseVO> getCourse(@PathVariable Long id) { return Result.success(courseService.getCourse(id)); }

    @GetMapping("/catalog-options")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "查询先修课程选项")
    public Result<java.util.List<CourseCatalog>> getCourseCatalogOptions() {
        return Result.success(courseService.getCourseCatalogOptions());
    }

    @GetMapping("/{id}/students")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "分页查询课程选课学生", description = "查询已选该课程的学生名单")
    public Result<Page<CourseSelectionStudentVO>> getSelectedStudents(@PathVariable Long id,
            @RequestParam(defaultValue = "1") long page, @RequestParam(defaultValue = "10") long size) {
        return Result.success(courseService.getSelectedStudents(id, page, size));
    }

    @GetMapping("/{id}/selections")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "分页查询选课申请", description = "按审核状态分页查询课程申请")
    public Result<Page<CourseSelectionStudentVO>> getCourseSelections(@PathVariable Long id,
            @RequestParam(defaultValue = "1") long page, @RequestParam(defaultValue = "10") long size,
            @RequestParam(defaultValue = "pending") String status) {
        return Result.success(courseService.getCourseSelections(id, page, size, status));
    }

    @PostMapping("/{id}/selections/review")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "批量审核选课申请", description = "批量通过或拒绝待审核申请")
    public Result<Integer> reviewCourseSelections(@PathVariable Long id, @Valid @RequestBody CourseSelectionReviewDTO request,
                                                   Principal principal) {
        return Result.success(courseService.reviewCourseSelections(id, request.getSelectionIds(), request.getAction(), principal.getName()));
    }

    @PostMapping
    @Operation(summary = "新增课程", description = "创建课程信息")
    public Result<CourseVO> createCourse(@RequestBody CourseUpdateDTO dto) { return Result.success(courseService.createCourse(dto)); }

    @PutMapping("/{id}")
    @Operation(summary = "更新课程", description = "根据课程编号更新课程信息")
    public Result<CourseVO> updateCourse(@PathVariable Long id, @RequestBody CourseUpdateDTO dto) { return Result.success(courseService.updateCourse(id, dto)); }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除课程", description = "根据课程编号删除课程信息")
    public Result<Void> deleteCourse(@PathVariable Long id) { courseService.deleteCourse(id); return Result.success(); }
}
