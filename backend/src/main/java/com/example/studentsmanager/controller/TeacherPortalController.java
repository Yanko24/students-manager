package com.example.studentsmanager.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentsmanager.core.response.Result;
import com.example.studentsmanager.model.dto.course.CourseQueryDTO;
import com.example.studentsmanager.model.vo.course.CourseVO;
import com.example.studentsmanager.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/teacher-portal")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TEACHER')")
public class TeacherPortalController {
    private final CourseService courseService;

    @GetMapping("/courses")
    public Result<Page<CourseVO>> getMyCourses(CourseQueryDTO query, Principal principal) {
        return Result.success(courseService.getTeacherCoursePage(principal.getName(), query));
    }
}
