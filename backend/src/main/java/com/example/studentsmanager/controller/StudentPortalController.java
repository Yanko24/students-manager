package com.example.studentsmanager.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentsmanager.core.response.Result;
import com.example.studentsmanager.model.vo.course.CourseVO;
import com.example.studentsmanager.model.vo.score.ScoreVO;
import com.example.studentsmanager.model.vo.score.StudentScoreStatsVO;
import com.example.studentsmanager.service.StudentPortalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/student-portal")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentPortalController {
    private final StudentPortalService studentPortalService;

    @GetMapping("/courses")
    public Result<Page<CourseVO>> getCourses(Principal principal,
            @RequestParam(defaultValue = "1") long page, @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String courseName, @RequestParam(required = false) String semester) {
        return Result.success(studentPortalService.getMyCourses(principal.getName(), page, size, courseName, semester));
    }

    @GetMapping("/courses/{courseId}")
    public Result<CourseVO> getCourse(Principal principal, @PathVariable Long courseId) {
        return Result.success(studentPortalService.getMyCourse(principal.getName(), courseId));
    }

    @GetMapping("/scores")
    public Result<Page<ScoreVO>> getScores(Principal principal,
            @RequestParam(defaultValue = "1") long page, @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String courseName, @RequestParam(required = false) String semester) {
        return Result.success(studentPortalService.getMyScores(principal.getName(), page, size, courseName, semester));
    }

    @GetMapping("/scores/statistics")
    public Result<StudentScoreStatsVO> getScoreStats(Principal principal,
            @RequestParam(required = false) String semester) {
        return Result.success(studentPortalService.getMyScoreStats(principal.getName(), semester));
    }
}
