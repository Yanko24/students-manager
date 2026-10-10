package com.example.studentsmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentsmanager.exception.BusinessException;
import com.example.studentsmanager.mapper.StudentPortalMapper;
import com.example.studentsmanager.mapper.CourseMapper;
import com.example.studentsmanager.mapper.CourseSelectionMapper;
import com.example.studentsmanager.mapper.CoursePrerequisiteMapper;
import com.example.studentsmanager.mapper.StudentMapper;
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
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentPortalServiceImpl implements StudentPortalService {
    private final StudentPortalMapper portalMapper;
    private final CourseMapper courseMapper;
    private final StudentMapper studentMapper;
    private final CourseSelectionMapper courseSelectionMapper;
    private final CoursePrerequisiteMapper coursePrerequisiteMapper;
    private final UserService userService;
    private final StudentService studentService;
    private final CourseSelectionQueueService courseSelectionQueueService;
    private final CourseScheduleService courseScheduleService;
    private final CurriculumPlanMapper curriculumPlanMapper;
    @Value("${students-manager.course-selection.max-semester-credits:30}")
    private BigDecimal maxSemesterCredits;

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
        for (CourseVO course : result.getRecords()) {
            List<String> required = course.getCatalogId() == null ? List.of()
                    : coursePrerequisiteMapper.selectCodes(course.getCatalogId());
            course.setPrerequisiteCourseCodes(required);
            course.setMissingPrerequisiteCourseCodes(course.getCatalogId() == null ? List.of()
                    : coursePrerequisiteMapper.selectMissingCodes(course.getCatalogId(), student.getId()));
            course.setSemesterSelectedCredits(courseSelectionMapper.sumActiveCreditsBySemester(student.getId(), course.getSemester()));
            course.setSemesterCreditLimit(maxSemesterCredits);
        }
        courseScheduleService.attach(result.getRecords());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String selectCourse(String username, Long courseId) {
        Student student = currentStudent(username);
        student = studentMapper.selectForUpdate(student.getId());
        if (student == null) throw new BusinessException("当前学生档案不存在");
        if (student.getStatus() != null && student.getStatus() != 0) {
            throw new BusinessException("当前学籍状态不能办理选课");
        }

        Course course = courseMapper.selectForUpdate(courseId);
        if (course == null) throw new BusinessException("开课信息不存在");
        if (!Integer.valueOf(1).equals(course.getSelectionOpen()) || Integer.valueOf(2).equals(course.getStatus())) {
            throw new BusinessException("该课程当前未开放选课");
        }
        LocalDateTime now = LocalDateTime.now();
        if (course.getSelectionStartAt() != null && now.isBefore(course.getSelectionStartAt())) {
            throw new BusinessException("该课程尚未到选课开始时间");
        }
        if (course.getSelectionEndAt() != null && now.isAfter(course.getSelectionEndAt())) {
            throw new BusinessException("该课程选课时间已截止");
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
        List<String> missingPrerequisites = course.getCatalogId() == null ? List.of()
                : coursePrerequisiteMapper.selectMissingCodes(course.getCatalogId(), student.getId());
        if (!missingPrerequisites.isEmpty()) {
            throw new BusinessException("尚未通过先修课程：" + String.join("、", missingPrerequisites));
        }
        BigDecimal currentCredits = courseSelectionMapper.sumActiveCreditsBySemester(student.getId(), course.getSemester());
        if (currentCredits.add(course.getCredits()).compareTo(maxSemesterCredits) > 0) {
            throw new BusinessException("本学期申请学分将超过上限 " + maxSemesterCredits.stripTrailingZeros().toPlainString() + " 学分");
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
        student = studentMapper.selectForUpdate(student.getId());
        if (student == null) throw new BusinessException("当前学生档案不存在");
        Course course = courseMapper.selectForUpdate(courseId);
        if (course == null) throw new BusinessException("开课信息不存在");
        if (Integer.valueOf(2).equals(course.getStatus())) {
            throw new BusinessException("课程已结课，不能退选");
        }
        List<String> activeStatuses = courseSelectionMapper.selectActiveStatusesForStudentAndCourseForUpdate(student.getId(), courseId);
        if (activeStatuses.isEmpty()) throw new BusinessException("没有找到可退选的课程记录");
        boolean hasEnrolledRequest = activeStatuses.stream().anyMatch(status -> "pending".equals(status) || "approved".equals(status));
        if (hasEnrolledRequest && course.getDropDeadlineAt() != null && LocalDateTime.now().isAfter(course.getDropDeadlineAt())) {
            throw new BusinessException("该课程已超过退选截止时间");
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
        if (Integer.valueOf(3).equals(student.getStatus())) {
            result.setGraduationAuditStatus("ALREADY_GRADUATED");
        } else if (student.getStatus() != null && student.getStatus() != 0) {
            result.setGraduationAuditStatus("INELIGIBLE");
            result.getGraduationAuditBlockers().add("当前学籍状态不能办理毕业审核");
        }
        CurriculumEarnedCreditsVO earned = curriculumPlanMapper.selectEarnedCredits(student.getId());
        if (earned != null) {
            result.setEarnedTotalCredits(defaultValue(earned.getEarnedTotalCredits()));
            result.setEarnedRequiredCredits(defaultValue(earned.getEarnedRequiredCredits()));
            result.setEarnedElectiveCredits(defaultValue(earned.getEarnedElectiveCredits()));
        }
        CurriculumPlan plan = curriculumPlanMapper.selectForStudent(student.getId());
        if (plan == null) {
            if (!"ALREADY_GRADUATED".equals(result.getGraduationAuditStatus())) {
                result.setGraduationAuditStatus("NOT_CONFIGURED");
            }
            return result;
        }
        result.setPlanConfigured(true);
        result.setPlanName(plan.getPlanName());
        result.setTotalCredits(plan.getTotalCredits());
        result.setRequiredCredits(plan.getRequiredCredits());
        result.setElectiveCredits(plan.getElectiveCredits());
        List<com.example.studentsmanager.model.vo.curriculum.CurriculumRequiredCourseVO> requiredCourses =
                curriculumPlanMapper.selectRequiredCourseProgress(plan.getId(), student.getId());
        result.setRequiredCourses(requiredCourses);
        result.setCourseRequirementsConfigured(!requiredCourses.isEmpty());
        requiredCourses.stream().filter(course -> !course.isPassed())
                .forEach(course -> result.getGraduationAuditBlockers().add(
                        "必修课程未通过：" + course.getCourseName() + "（" + course.getCourseCode() + "）"));
        result.setRemainingTotalCredits(remaining(plan.getTotalCredits(), result.getEarnedTotalCredits()));
        result.setRemainingRequiredCredits(remaining(plan.getRequiredCredits(), result.getEarnedRequiredCredits()));
        result.setRemainingElectiveCredits(remaining(plan.getElectiveCredits(), result.getEarnedElectiveCredits()));
        if (result.getRemainingTotalCredits().signum() > 0) result.getGraduationAuditBlockers().add("毕业总学分尚差 " + result.getRemainingTotalCredits() + " 学分");
        if (result.getRemainingRequiredCredits().signum() > 0) result.getGraduationAuditBlockers().add("必修学分尚差 " + result.getRemainingRequiredCredits() + " 学分");
        if (result.getRemainingElectiveCredits().signum() > 0) result.getGraduationAuditBlockers().add("选修学分尚差 " + result.getRemainingElectiveCredits() + " 学分");
        if (plan.getTotalCredits() != null && plan.getTotalCredits().signum() > 0) {
            result.setCompletionRate(result.getEarnedTotalCredits().multiply(BigDecimal.valueOf(100))
                    .divide(plan.getTotalCredits(), 1, RoundingMode.HALF_UP).min(BigDecimal.valueOf(100)));
        }
        if (!"ALREADY_GRADUATED".equals(result.getGraduationAuditStatus())
                && !"INELIGIBLE".equals(result.getGraduationAuditStatus())) {
            result.setGraduationAuditStatus(result.getGraduationAuditBlockers().isEmpty() ? "ELIGIBLE" : "IN_PROGRESS");
        }
        return result;
    }

    private BigDecimal defaultValue(BigDecimal value) { return value == null ? BigDecimal.ZERO : value; }

    private BigDecimal remaining(BigDecimal target, BigDecimal earned) {
        if (target == null) return BigDecimal.ZERO;
        return target.subtract(earned).max(BigDecimal.ZERO);
    }
}
