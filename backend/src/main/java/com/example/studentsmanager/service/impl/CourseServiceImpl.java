package com.example.studentsmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.studentsmanager.exception.BusinessException;
import com.example.studentsmanager.mapper.CourseMapper;
import com.example.studentsmanager.mapper.CourseCatalogMapper;
import com.example.studentsmanager.mapper.CourseSelectionMapper;
import com.example.studentsmanager.model.dto.course.CourseQueryDTO;
import com.example.studentsmanager.model.dto.course.CourseUpdateDTO;
import com.example.studentsmanager.model.entity.Course;
import com.example.studentsmanager.model.entity.CourseCatalog;
import com.example.studentsmanager.model.entity.Teacher;
import com.example.studentsmanager.model.entity.Student;
import com.example.studentsmanager.model.entity.User;
import com.example.studentsmanager.model.vo.course.CourseVO;
import com.example.studentsmanager.model.vo.course.CourseSelectionStudentVO;
import com.example.studentsmanager.model.vo.course.CourseSelectionReviewRecord;
import com.example.studentsmanager.service.CourseService;
import com.example.studentsmanager.service.TeacherService;
import com.example.studentsmanager.service.StudentService;
import com.example.studentsmanager.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
    private final TeacherService teacherService;
    private final UserService userService;
    private final CourseSelectionMapper courseSelectionMapper;
    private final CourseCatalogMapper courseCatalogMapper;
    private final CourseSelectionQueueService courseSelectionQueueService;
    private final CourseScheduleService courseScheduleService;
    private final StudentService studentService;
    private final OperationAuditService operationAuditService;
    private final SystemNotificationService notificationService;

    public CourseServiceImpl(TeacherService teacherService, UserService userService, CourseSelectionMapper courseSelectionMapper,
                             CourseCatalogMapper courseCatalogMapper,
                             CourseSelectionQueueService courseSelectionQueueService, CourseScheduleService courseScheduleService,
                             StudentService studentService, OperationAuditService operationAuditService,
                             SystemNotificationService notificationService) {
        this.teacherService = teacherService;
        this.userService = userService;
        this.courseSelectionMapper = courseSelectionMapper;
        this.courseCatalogMapper = courseCatalogMapper;
        this.courseSelectionQueueService = courseSelectionQueueService;
        this.courseScheduleService = courseScheduleService;
        this.studentService = studentService;
        this.operationAuditService = operationAuditService;
        this.notificationService = notificationService;
    }

    @Override
    public Page<CourseVO> getCoursePage(CourseQueryDTO query) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<Course>()
                .like(query.getCode() != null && !query.getCode().trim().isEmpty(), Course::getCourseCode, query.getCode())
                .like(query.getName() != null && !query.getName().trim().isEmpty(), Course::getCourseName, query.getName())
                .like(query.getSemester() != null && !query.getSemester().trim().isEmpty(), Course::getSemester, query.getSemester())
                .orderByDesc(Course::getCreateTime);
        if (query.getDepartment() != null && !query.getDepartment().trim().isEmpty()) {
            List<Long> teacherIds = teacherService.list(new LambdaQueryWrapper<Teacher>()
                            .like(Teacher::getDepartment, query.getDepartment()))
                    .stream().map(Teacher::getId).collect(Collectors.toList());
            if (teacherIds.isEmpty()) {
                Page<CourseVO> emptyPage = new Page<>(query.getPage(), query.getSize());
                emptyPage.setRecords(Collections.emptyList());
                return emptyPage;
            }
            wrapper.in(Course::getTeacherId, teacherIds);
        }
        Page<Course> page = page(new Page<>(query.getPage(), query.getSize()), wrapper);
        return toVOPage(page);
    }

    @Override
    public CourseVO getCourse(Long id) {
        Course course = getById(id);
        if (course == null) throw new BusinessException("课程不存在");
        return toVOPage(new Page<Course>(1, 1, 1).setRecords(Collections.singletonList(course))).getRecords().get(0);
    }

    @Override
    public Page<CourseSelectionStudentVO> getSelectedStudents(Long courseId, long page, long size) {
        if (getById(courseId) == null) throw new BusinessException("课程不存在");
        return courseSelectionMapper.selectStudentsByCourse(new Page<>(Math.max(1, page), Math.min(100, Math.max(1, size))), courseId);
    }

    @Override
    public Page<CourseSelectionStudentVO> getCourseSelections(Long courseId, long page, long size, String status) {
        if (getById(courseId) == null) throw new BusinessException("课程不存在");
        String normalizedStatus = status == null || status.isBlank() ? "pending" : status.trim().toLowerCase();
        if (!List.of("pending", "waitlisted", "approved", "rejected", "all").contains(normalizedStatus)) {
            throw new BusinessException("选课审核状态无效");
        }
        String mapperStatus = "all".equals(normalizedStatus) ? "ALL" : normalizedStatus;
        return courseSelectionMapper.selectSelectionsByCourse(
                new Page<>(Math.max(1, page), Math.min(100, Math.max(1, size))), courseId, mapperStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int reviewCourseSelections(Long courseId, List<Long> selectionIds, String action, String actor) {
        Course course = getBaseMapper().selectForUpdate(courseId);
        if (course == null) throw new BusinessException("课程不存在");
        if (selectionIds == null || selectionIds.isEmpty() || selectionIds.size() > 100) {
            throw new BusinessException("每次请选择1到100条选课申请");
        }
        List<Long> ids = selectionIds.stream().filter(id -> id != null).distinct().toList();
        if (ids.size() != selectionIds.size()) {
            throw new BusinessException("选课申请编号重复或无效");
        }
        String normalizedAction = action == null ? "" : action.trim().toUpperCase();
        if (!List.of("APPROVE", "REJECT").contains(normalizedAction)) {
            throw new BusinessException("审核操作无效");
        }

        List<CourseSelectionReviewRecord> pending = courseSelectionMapper.selectPendingForReview(courseId, ids);
        if (pending.size() != ids.size()) {
            throw new BusinessException("部分申请已处理或不属于该课程，请刷新后重试");
        }

        if ("APPROVE".equals(normalizedAction)) {
            int activeCount = courseSelectionMapper.countActiveForCourse(courseId);
            int capacity = course.getMaxStudents() == null ? 60 : course.getMaxStudents();
            if (activeCount > capacity) {
                throw new BusinessException("当前待审核和已通过人数已超过课程容量，请先调整名额");
            }
            Set<Long> studentIds = new HashSet<>();
            for (CourseSelectionReviewRecord record : pending) {
                if (!studentIds.add(record.getStudentId())) {
                    throw new BusinessException("同一学生存在多条待审核申请，请逐条核对后处理");
                }
                if (courseSelectionMapper.countOtherActiveForStudentAndCourse(courseId, record.getStudentId(), ids) > 0) {
                    throw new BusinessException("学生已存在该课程的其他有效申请，无法通过");
                }
                if (getBaseMapper().countStudentEligibility(courseId, record.getStudentId()) == 0) {
                    throw new BusinessException("部分申请学生已不符合课程的专业或年级范围，无法批量通过");
                }
                if (courseScheduleService.hasStudentConflict(course, record.getStudentId())) {
                    throw new BusinessException("部分学生的课程时间与已选课程冲突，无法批量通过");
                }
            }
        }

        String targetStatus = "APPROVE".equals(normalizedAction) ? "approved" : "rejected";
        int updated = courseSelectionMapper.reviewPending(courseId, ids, targetStatus, actor);
        if (updated != ids.size()) {
            throw new BusinessException("审核状态已变化，请刷新后重试");
        }
        if ("REJECT".equals(normalizedAction)) {
            courseSelectionQueueService.promoteAvailable(course, actor);
        }
        String actionLabel = "APPROVE".equals(normalizedAction) ? "通过" : "拒绝";
        for (CourseSelectionReviewRecord record : pending) {
            operationAuditService.record(actor, "COURSE_SELECTION_" + normalizedAction, "COURSE_SELECTION",
                    record.getSelectionId(), actionLabel + "课程“" + course.getCourseName() + "”的选课申请");
            Student student = studentService.getById(record.getStudentId());
            if (student != null) notificationService.notifyUser(student.getUserId(), "COURSE_SELECTION_REVIEW",
                    "选课申请审核结果", "你申请的课程“" + course.getCourseName() + "”已" + actionLabel + "。");
        }
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseVO createCourse(CourseUpdateDTO dto) {
        String sectionCode = normalizeSectionCode(dto.getSectionCode());
        if (count(new LambdaQueryWrapper<Course>().eq(Course::getCourseCode, dto.getCode())
                .eq(Course::getSemester, dto.getSemester()).eq(Course::getSectionCode, sectionCode)) > 0) {
            throw new BusinessException("该课程在此学期的教学班编号已存在");
        }
        Course course = new Course();
        apply(course, dto);
        synchronizeCatalog(course);
        course.setIsDeleted(0);
        save(course);
        courseScheduleService.replace(course, dto.getSchedules(), null);
        return getCourse(course.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseVO updateCourse(Long id, CourseUpdateDTO dto) {
        Course course = getBaseMapper().selectForUpdate(id);
        if (course == null) throw new BusinessException("课程不存在");
        String sectionCode = normalizeSectionCode(dto.getSectionCode());
        if (count(new LambdaQueryWrapper<Course>().eq(Course::getCourseCode, dto.getCode())
                .eq(Course::getSemester, dto.getSemester()).eq(Course::getSectionCode, sectionCode)
                .ne(Course::getId, id)) > 0) {
            throw new BusinessException("该课程在此学期的教学班编号已存在");
        }
        apply(course, dto);
        int activeSelections = courseSelectionMapper.countActiveForCourse(id);
        if (course.getMaxStudents() != null && activeSelections > course.getMaxStudents()) {
            throw new BusinessException("课程容量不能低于当前待审核和已通过人数");
        }
        synchronizeCatalog(course);
        updateById(course);
        courseScheduleService.replace(course, dto.getSchedules(), id);
        courseSelectionQueueService.promoteAvailable(course, "system");
        return getCourse(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCourse(Long id) {
        if (getById(id) == null) throw new BusinessException("课程不存在");
        removeById(id);
    }

    private void apply(Course course, CourseUpdateDTO dto) {
        int maxStudents = dto.getMaxStudents() == null ? 60 : dto.getMaxStudents();
        int selectionOpen = dto.getSelectionOpen() == null ? 1 : dto.getSelectionOpen();
        String selectionScope = dto.getSelectionScope() == null || dto.getSelectionScope().isBlank()
                ? "ALL" : dto.getSelectionScope().trim().toUpperCase();
        if (maxStudents < 1 || maxStudents > 500) {
            throw new BusinessException("选课容量必须在 1 到 500 人之间");
        }
        if (selectionOpen != 0 && selectionOpen != 1) {
            throw new BusinessException("选课状态无效");
        }
        if (!List.of("ALL", "COLLEGE", "MAJOR").contains(selectionScope)) {
            throw new BusinessException("选课范围无效");
        }
        if ("COLLEGE".equals(selectionScope)
                && (dto.getSelectionCollegeId() == null || getBaseMapper().countActiveCollege(dto.getSelectionCollegeId()) == 0)) {
            throw new BusinessException("请选择有效的适用学院");
        }
        if ("MAJOR".equals(selectionScope)
                && (dto.getSelectionMajorCode() == null || dto.getSelectionMajorCode().isBlank()
                || dto.getSelectionCollegeId() == null
                || getBaseMapper().countActiveMajorInCollege(dto.getSelectionMajorCode().trim(), dto.getSelectionCollegeId()) == 0)) {
            throw new BusinessException("请选择有效的适用专业");
        }
        String selectionGrade = dto.getSelectionGrade() == null || dto.getSelectionGrade().isBlank()
                ? null : dto.getSelectionGrade().trim();
        if (selectionGrade != null && !selectionGrade.matches("\\d{4}")) {
            throw new BusinessException("适用年级必须为4位入学年份");
        }
        if (course.getId() != null && courseSelectionMapper.countActiveForCourse(course.getId()) > maxStudents) {
            throw new BusinessException("选课容量不能低于当前已选人数");
        }
        course.setCourseCode(dto.getCode());
        course.setSectionCode(normalizeSectionCode(dto.getSectionCode()));
        course.setCourseName(dto.getName());
        course.setTeacherId(dto.getTeacherId());
        course.setCredits(dto.getCredit());
        course.setCourseType(dto.getType());
        course.setSemester(dto.getSemester());
        course.setHours(dto.getHours());
        course.setStatus(dto.getStatus() == null ? 0 : dto.getStatus());
        course.setSelectionOpen(selectionOpen);
        course.setMaxStudents(maxStudents);
        course.setSelectionScope(selectionScope);
        course.setSelectionCollegeId("COLLEGE".equals(selectionScope) || "MAJOR".equals(selectionScope) ? dto.getSelectionCollegeId() : null);
        course.setSelectionMajorCode("MAJOR".equals(selectionScope) ? dto.getSelectionMajorCode().trim() : null);
        course.setSelectionGrade(selectionGrade);
        course.setDescription(dto.getDescription());
        course.setObjectives(dto.getObjectives());
    }

    private String normalizeSectionCode(String value) {
        String sectionCode = value == null || value.isBlank() ? "01" : value.trim();
        if (!sectionCode.matches("[A-Za-z0-9_-]{1,10}")) {
            throw new BusinessException("教学班编号仅支持1到10位字母、数字、下划线或短横线");
        }
        return sectionCode;
    }

    private void synchronizeCatalog(Course course) {
        CourseCatalog catalog = courseCatalogMapper.selectByCourseCode(course.getCourseCode());
        boolean newOffering = course.getId() == null;
        if (catalog == null) {
            catalog = new CourseCatalog();
            catalog.setCourseCode(course.getCourseCode());
            copyCourseDetailsToCatalog(course, catalog);
            courseCatalogMapper.insert(catalog);
        } else if (newOffering) {
            copyCatalogDetailsToCourse(catalog, course);
        } else {
            copyCourseDetailsToCatalog(course, catalog);
            courseCatalogMapper.updateById(catalog);
            getBaseMapper().synchronizeCatalogFields(catalog.getId(), course.getId(), catalog.getCourseName(),
                    catalog.getCredits(), catalog.getCourseType(), catalog.getHours(), catalog.getDescription(), catalog.getObjectives());
        }
        course.setCatalogId(catalog.getId());
    }

    private void copyCourseDetailsToCatalog(Course course, CourseCatalog catalog) {
        catalog.setCourseName(course.getCourseName()); catalog.setCredits(course.getCredits());
        catalog.setCourseType(course.getCourseType()); catalog.setHours(course.getHours());
        catalog.setDescription(course.getDescription()); catalog.setObjectives(course.getObjectives());
    }

    private void copyCatalogDetailsToCourse(CourseCatalog catalog, Course course) {
        course.setCourseName(catalog.getCourseName()); course.setCredits(catalog.getCredits());
        course.setCourseType(catalog.getCourseType()); course.setHours(catalog.getHours());
        course.setDescription(catalog.getDescription()); course.setObjectives(catalog.getObjectives());
    }

    private Page<CourseVO> toVOPage(Page<Course> source) {
        List<Long> teacherIds = source.getRecords().stream().map(Course::getTeacherId).filter(id -> id != null).distinct().collect(Collectors.toList());
        Map<Long, Teacher> teachers = teacherIds.isEmpty() ? Collections.emptyMap() : teacherService.listByIds(teacherIds)
                .stream().collect(Collectors.toMap(Teacher::getId, Function.identity()));
        List<Long> userIds = teachers.values().stream().map(Teacher::getUserId).distinct().collect(Collectors.toList());
        Map<Long, User> users = userIds.isEmpty() ? Collections.emptyMap() : userService.listByIds(userIds)
                .stream().collect(Collectors.toMap(User::getId, Function.identity()));
        Page<CourseVO> result = new Page<>(source.getCurrent(), source.getSize(), source.getTotal());
        result.setRecords(source.getRecords().stream().map(course -> {
            CourseVO vo = new CourseVO();
            vo.setId(course.getId()); vo.setCatalogId(course.getCatalogId()); vo.setCode(course.getCourseCode());
            vo.setSectionCode(course.getSectionCode()); vo.setName(course.getCourseName());
            vo.setTeacherId(course.getTeacherId()); vo.setCredit(course.getCredits()); vo.setHours(course.getHours());
            vo.setType(course.getCourseType()); vo.setSemester(course.getSemester()); vo.setStatus(course.getStatus());
            vo.setSelectionOpen(course.getSelectionOpen()); vo.setMaxStudents(course.getMaxStudents());
            vo.setSelectionScope(course.getSelectionScope()); vo.setSelectionCollegeId(course.getSelectionCollegeId());
            vo.setSelectionMajorCode(course.getSelectionMajorCode()); vo.setSelectionGrade(course.getSelectionGrade());
            if (course.getSelectionCollegeId() != null) vo.setSelectionCollegeName(getBaseMapper().findCollegeName(course.getSelectionCollegeId()));
            if (course.getSelectionMajorCode() != null) vo.setSelectionMajorName(getBaseMapper().findMajorName(course.getSelectionMajorCode()));
            vo.setSelectedCount(courseSelectionMapper.countActiveForCourse(course.getId()));
            vo.setStatusText(course.getStatus() == null || course.getStatus() == 0 ? "未开课" : course.getStatus() == 1 ? "已开课" : "已结课");
            vo.setDescription(course.getDescription()); vo.setObjectives(course.getObjectives());
            vo.setCreateTime(course.getCreateTime()); vo.setUpdateTime(course.getUpdateTime());
            Teacher teacher = teachers.get(course.getTeacherId());
            if (teacher != null) {
                vo.setCollege(teacher.getDepartment());
                User user = users.get(teacher.getUserId());
                if (user != null) vo.setTeacher(user.getRealName());
            }
            return vo;
        }).collect(Collectors.toList()));
        courseScheduleService.attach(result.getRecords());
        return result;
    }
}
