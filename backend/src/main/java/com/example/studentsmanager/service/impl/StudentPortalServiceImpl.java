package com.example.studentsmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentsmanager.exception.BusinessException;
import com.example.studentsmanager.mapper.StudentPortalMapper;
import com.example.studentsmanager.mapper.CourseMapper;
import com.example.studentsmanager.mapper.CourseSelectionMapper;
import com.example.studentsmanager.mapper.CurriculumPlanMapper;
import com.example.studentsmanager.model.entity.Course;
import com.example.studentsmanager.model.entity.Student;
import com.example.studentsmanager.model.entity.User;
import com.example.studentsmanager.model.vo.course.CourseVO;
import com.example.studentsmanager.model.vo.score.ScoreVO;
import com.example.studentsmanager.model.vo.score.StudentScoreStatsVO;
import com.example.studentsmanager.model.vo.curriculum.CurriculumEarnedCreditsVO;
import com.example.studentsmanager.model.vo.curriculum.CurriculumProgressVO;
import com.example.studentsmanager.model.entity.CurriculumPlan;
import com.example.studentsmanager.service.StudentPortalService;
import com.example.studentsmanager.service.StudentService;
import com.example.studentsmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class StudentPortalServiceImpl implements StudentPortalService {
    private final StudentPortalMapper portalMapper;
    private final CourseMapper courseMapper;
    private final CourseSelectionMapper courseSelectionMapper;
    private final UserService userService;
    private final StudentService studentService;
    private final CourseSelectionQueueService courseSelectionQueueService;
    private final CourseScheduleService courseScheduleService;
    private final CurriculumPlanMapper curriculumPlanMapper;

    private Long currentUserId(String username) {
        User user = userService.findByUsername(username);
        if (user == null) throw new BusinessException("当前账号不存在");
        return user.getId();
    }

    private Student currentStudent(String username) {
        Long userId = currentUserId(username);
        Student student = studentService.getOne(new LambdaQueryWrapper<Student>().eq(Student::getUserId, userId));
        if (student == null) throw new BusinessException("当前账号未关联学生档案");
        return student;
    }

    private Long currentStudentUserId(String username) {
        return currentStudent(username).getUserId();
    }

    private long safePage(long page) { return Math.max(1, page); }
    private long safeSize(long size) { return Math.min(100, Math.max(1, size)); }

    @Override
    public Page<CourseVO> getAvailableCourses(String username, long page, long size, String courseName, String semester) {
        Student student = currentStudent(username);
        if (student.getStatus() != null && student.getStatus() != 0) {
            throw new BusinessException("当前学籍状态不能办理选课");
        }
        Page<CourseVO> result = portalMapper.selectAvailableCourses(new Page<>(safePage(page), safeSize(size)), student.getId(),
                blankToNull(courseName), blankToNull(semester));
        courseScheduleService.attach(result.getRecords());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String selectCourse(String username, Long courseId) {
        Student student = currentStudent(username);
        if (student.getStatus() != null && student.getStatus() != 0) {
            throw new BusinessException("当前学籍状态不能办理选课");
        }

        Course course = courseMapper.selectForUpdate(courseId);
        if (course == null) throw new BusinessException("开课信息不存在");
        if (!Integer.valueOf(1).equals(course.getSelectionOpen()) || Integer.valueOf(2).equals(course.getStatus())) {
            throw new BusinessException("该课程当前未开放选课");
        }
        if (courseScheduleService.hasStudentConflict(course, student.getId())) {
            throw new BusinessException("该课程时间与您已申请或已选的课程冲突");
        }
        if (courseMapper.countStudentEligibility(courseId, student.getId()) == 0) {
            throw new BusinessException("当前课程不在你的专业或年级选课范围内");
        }
        if (!courseSelectionMapper.selectActiveForStudentAndCourseForUpdate(student.getId(), courseId).isEmpty()) {
            throw new BusinessException("你已选择该课程");
        }
        int selectedCount = courseSelectionMapper.selectActiveForCourseForUpdate(courseId).size();
        int capacity = course.getMaxStudents() == null ? 60 : course.getMaxStudents();
        String selectionStatus = selectedCount >= capacity ? "waitlisted" : "pending";
        if ("waitlisted".equals(selectionStatus)) {
            courseSelectionMapper.insertWaitlisted(student.getId(), courseId, username);
            return selectionStatus;
        }
        courseSelectionMapper.insertPending(student.getId(), courseId, username);
        return selectionStatus;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dropCourse(String username, Long courseId) {
        Student student = currentStudent(username);
        Course course = courseMapper.selectForUpdate(courseId);
        if (course == null) throw new BusinessException("开课信息不存在");
        if (Integer.valueOf(2).equals(course.getStatus())) {
            throw new BusinessException("课程已结课，不能退选");
        }
        if (courseSelectionMapper.dropSelection(student.getId(), courseId, username) == 0) {
            throw new BusinessException("没有找到可退选的课程记录");
        }
        courseSelectionQueueService.promoteAvailable(course, username);
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    @Override
    public Page<CourseVO> getMyCourses(String username, long page, long size, String courseName, String semester) {
        Page<CourseVO> result = portalMapper.selectStudentCourses(new Page<>(safePage(page), safeSize(size)),
                currentStudentUserId(username), blankToNull(courseName), blankToNull(semester));
        courseScheduleService.attach(result.getRecords());
        return result;
    }

    @Override
    public CourseVO getMyCourse(String username, Long courseId) {
        CourseVO course = portalMapper.selectStudentCourse(currentStudentUserId(username), courseId);
        if (course == null) throw new BusinessException("课程不存在或不属于当前学生");
        courseScheduleService.attach(course);
        return course;
    }

    @Override
    public Page<ScoreVO> getMyScores(String username, long page, long size, String courseName, String semester) {
        return portalMapper.selectStudentScores(new Page<>(safePage(page), safeSize(size)),
                currentStudentUserId(username), blankToNull(courseName), blankToNull(semester));
    }

    @Override
    public StudentScoreStatsVO getMyScoreStats(String username, String semester) {
        StudentScoreStatsVO stats = portalMapper.selectStudentScoreStats(currentStudentUserId(username), blankToNull(semester));
        return stats == null ? new StudentScoreStatsVO() : stats;
    }

    @Override
    public CurriculumProgressVO getCurriculumProgress(String username) {
        Student student = currentStudent(username);
        CurriculumProgressVO result = new CurriculumProgressVO();
        result.setMajorCode(student.getMajorCode());
        result.setGrade(student.getGrade());
        CurriculumEarnedCreditsVO earned = curriculumPlanMapper.selectEarnedCredits(student.getId());
        if (earned != null) {
            result.setEarnedTotalCredits(defaultValue(earned.getEarnedTotalCredits()));
            result.setEarnedRequiredCredits(defaultValue(earned.getEarnedRequiredCredits()));
            result.setEarnedElectiveCredits(defaultValue(earned.getEarnedElectiveCredits()));
        }
        CurriculumPlan plan = curriculumPlanMapper.selectForStudent(student.getId());
        if (plan == null) return result;
        result.setPlanConfigured(true);
        result.setPlanName(plan.getPlanName());
        result.setTotalCredits(plan.getTotalCredits());
        result.setRequiredCredits(plan.getRequiredCredits());
        result.setElectiveCredits(plan.getElectiveCredits());
        result.setRemainingTotalCredits(remaining(plan.getTotalCredits(), result.getEarnedTotalCredits()));
        result.setRemainingRequiredCredits(remaining(plan.getRequiredCredits(), result.getEarnedRequiredCredits()));
        result.setRemainingElectiveCredits(remaining(plan.getElectiveCredits(), result.getEarnedElectiveCredits()));
        if (plan.getTotalCredits() != null && plan.getTotalCredits().signum() > 0) {
            result.setCompletionRate(result.getEarnedTotalCredits().multiply(BigDecimal.valueOf(100))
                    .divide(plan.getTotalCredits(), 1, RoundingMode.HALF_UP).min(BigDecimal.valueOf(100)));
        }
        return result;
    }

    private BigDecimal defaultValue(BigDecimal value) { return value == null ? BigDecimal.ZERO : value; }

    private BigDecimal remaining(BigDecimal target, BigDecimal earned) {
        if (target == null) return BigDecimal.ZERO;
        return target.subtract(earned).max(BigDecimal.ZERO);
    }
}
