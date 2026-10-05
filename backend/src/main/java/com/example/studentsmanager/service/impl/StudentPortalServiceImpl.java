package com.example.studentsmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentsmanager.exception.BusinessException;
import com.example.studentsmanager.mapper.StudentPortalMapper;
import com.example.studentsmanager.model.entity.Student;
import com.example.studentsmanager.model.entity.User;
import com.example.studentsmanager.model.vo.course.CourseVO;
import com.example.studentsmanager.model.vo.score.ScoreVO;
import com.example.studentsmanager.model.vo.score.StudentScoreStatsVO;
import com.example.studentsmanager.service.StudentPortalService;
import com.example.studentsmanager.service.StudentService;
import com.example.studentsmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentPortalServiceImpl implements StudentPortalService {
    private final StudentPortalMapper portalMapper;
    private final UserService userService;
    private final StudentService studentService;

    private Long currentUserId(String username) {
        User user = userService.findByUsername(username);
        if (user == null) throw new BusinessException("当前账号不存在");
        return user.getId();
    }

    private Long currentStudentUserId(String username) {
        Long userId = currentUserId(username);
        Student student = studentService.getOne(new LambdaQueryWrapper<Student>().eq(Student::getUserId, userId));
        if (student == null) throw new BusinessException("当前账号未关联学生档案");
        return userId;
    }

    private long safePage(long page) { return Math.max(1, page); }
    private long safeSize(long size) { return Math.min(100, Math.max(1, size)); }

    @Override
    public Page<CourseVO> getMyCourses(String username, long page, long size, String courseName, String semester) {
        return portalMapper.selectStudentCourses(new Page<>(safePage(page), safeSize(size)),
                currentStudentUserId(username), courseName, semester);
    }

    @Override
    public CourseVO getMyCourse(String username, Long courseId) {
        CourseVO course = portalMapper.selectStudentCourse(currentStudentUserId(username), courseId);
        if (course == null) throw new BusinessException("课程不存在或不属于当前学生");
        return course;
    }

    @Override
    public Page<ScoreVO> getMyScores(String username, long page, long size, String courseName, String semester) {
        return portalMapper.selectStudentScores(new Page<>(safePage(page), safeSize(size)),
                currentStudentUserId(username), courseName, semester);
    }

    @Override
    public StudentScoreStatsVO getMyScoreStats(String username, String semester) {
        StudentScoreStatsVO stats = portalMapper.selectStudentScoreStats(currentStudentUserId(username), semester);
        return stats == null ? new StudentScoreStatsVO() : stats;
    }
}
