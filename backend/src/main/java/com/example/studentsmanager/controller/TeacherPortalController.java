package com.example.studentsmanager.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentsmanager.core.response.Result;
import com.example.studentsmanager.model.dto.course.CourseQueryDTO;
import com.example.studentsmanager.model.dto.score.ScoreQueryDTO;
import com.example.studentsmanager.model.dto.score.ScoreUpdateDTO;
import com.example.studentsmanager.model.vo.course.CourseVO;
import com.example.studentsmanager.model.vo.course.CourseSelectionStudentVO;
import com.example.studentsmanager.model.vo.score.ScoreVO;
import com.example.studentsmanager.model.vo.score.ScoreChangeLogVO;
import com.example.studentsmanager.service.CourseService;
import com.example.studentsmanager.service.ScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/teacher-portal")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TEACHER')")
public class TeacherPortalController {
    private final CourseService courseService;
    private final ScoreService scoreService;

    @GetMapping("/courses")
    public Result<Page<CourseVO>> getMyCourses(CourseQueryDTO query, Principal principal) {
        return Result.success(courseService.getTeacherCoursePage(principal.getName(), query));
    }

    @GetMapping("/courses/{courseId}")
    public Result<CourseVO> getMyCourse(@PathVariable Long courseId, Principal principal) {
        return Result.success(courseService.getTeacherCourse(principal.getName(), courseId));
    }

    @GetMapping("/courses/{courseId}/students")
    public Result<Page<CourseSelectionStudentVO>> getCourseStudents(@PathVariable Long courseId,
            @RequestParam(defaultValue = "1") long page, @RequestParam(defaultValue = "20") long size,
            @RequestParam(required = false) String keyword,
            Principal principal) {
        return Result.success(courseService.getTeacherSelectedStudents(principal.getName(), courseId, page, size, keyword));
    }

    @GetMapping("/courses/{courseId}/scores")
    public Result<Page<ScoreVO>> getCourseScores(@PathVariable Long courseId, ScoreQueryDTO query, Principal principal) {
        return Result.success(scoreService.getTeacherCourseScorePage(principal.getName(), courseId, query));
    }

    @PostMapping("/courses/{courseId}/scores")
    public Result<ScoreVO> submitCourseScore(@PathVariable Long courseId, @RequestBody ScoreUpdateDTO request,
                                              Principal principal) {
        return Result.success(scoreService.createTeacherCourseScore(principal.getName(), courseId, request, principal.getName()));
    }

    @PostMapping("/courses/{courseId}/scores/batch")
    public Result<List<ScoreVO>> submitCourseScores(@PathVariable Long courseId,
            @RequestBody List<ScoreUpdateDTO> requests, Principal principal) {
        return Result.success(scoreService.createTeacherCourseScores(principal.getName(), courseId, requests, principal.getName()));
    }

    @PutMapping("/courses/{courseId}/scores/{scoreId}")
    public Result<ScoreVO> updateCourseScore(@PathVariable Long courseId, @PathVariable Long scoreId,
            @RequestBody ScoreUpdateDTO request, Principal principal) {
        return Result.success(scoreService.updateTeacherCourseScore(principal.getName(), courseId, scoreId, request, principal.getName()));
    }

    @GetMapping("/courses/{courseId}/scores/{scoreId}/history")
    public Result<List<ScoreChangeLogVO>> getCourseScoreHistory(@PathVariable Long courseId,
            @PathVariable Long scoreId, Principal principal) {
        return Result.success(scoreService.getTeacherCourseScoreHistory(principal.getName(), courseId, scoreId));
    }
}
