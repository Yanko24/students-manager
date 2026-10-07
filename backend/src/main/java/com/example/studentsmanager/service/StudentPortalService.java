package com.example.studentsmanager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentsmanager.model.vo.course.CourseVO;
import com.example.studentsmanager.model.vo.score.ScoreVO;
import com.example.studentsmanager.model.vo.score.StudentScoreStatsVO;

public interface StudentPortalService {
    Page<CourseVO> getAvailableCourses(String username, long page, long size, String courseName, String semester);
    void selectCourse(String username, Long courseId);
    void dropCourse(String username, Long courseId);
    Page<CourseVO> getMyCourses(String username, long page, long size, String courseName, String semester);
    CourseVO getMyCourse(String username, Long courseId);
    Page<ScoreVO> getMyScores(String username, long page, long size, String courseName, String semester);
    StudentScoreStatsVO getMyScoreStats(String username, String semester);
}
