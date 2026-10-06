package com.example.studentsmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentsmanager.core.response.Result;
import com.example.studentsmanager.model.dto.course.CourseQueryDTO;
import com.example.studentsmanager.model.dto.course.CourseUpdateDTO;
import com.example.studentsmanager.model.vo.course.CourseVO;
import com.example.studentsmanager.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
