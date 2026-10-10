package com.example.studentsmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.studentsmanager.exception.BusinessException;
import com.example.studentsmanager.mapper.ScoreMapper;
import com.example.studentsmanager.mapper.ScoreChangeLogMapper;
import com.example.studentsmanager.mapper.CourseSelectionMapper;
import com.example.studentsmanager.model.dto.score.ScoreQueryDTO;
import com.example.studentsmanager.model.dto.score.ScoreUpdateDTO;
import com.example.studentsmanager.model.entity.*;
import com.example.studentsmanager.model.vo.score.ScoreVO;
import com.example.studentsmanager.model.vo.score.ScoreChangeLogVO;
import com.example.studentsmanager.model.vo.score.ScoreDistributionResponse;
import com.example.studentsmanager.model.vo.score.ScoreDistributionVO;
import com.example.studentsmanager.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ScoreServiceImpl extends ServiceImpl<ScoreMapper, Score> implements ScoreService {
    private final StudentService studentService;
    private final CourseService courseService;
    private final UserService userService;
    private final TeacherService teacherService;
    private final ScoreChangeLogMapper scoreChangeLogMapper;
    private final CourseSelectionMapper courseSelectionMapper;
    private final OperationAuditService operationAuditService;
    private final SystemNotificationService notificationService;

    public ScoreServiceImpl(StudentService studentService, CourseService courseService, UserService userService,
                            TeacherService teacherService,
                            ScoreChangeLogMapper scoreChangeLogMapper, CourseSelectionMapper courseSelectionMapper,
                            OperationAuditService operationAuditService,
                            SystemNotificationService notificationService) {
        this.studentService = studentService;
        this.courseService = courseService;
        this.userService = userService;
        this.teacherService = teacherService;
        this.scoreChangeLogMapper = scoreChangeLogMapper;
        this.courseSelectionMapper = courseSelectionMapper;
        this.operationAuditService = operationAuditService;
        this.notificationService = notificationService;
    }

    @Override
    public Page<ScoreVO> getScorePage(ScoreQueryDTO query) {
        LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<Score>()
                .like(query.getSemester() != null && !query.getSemester().trim().isEmpty(), Score::getSemester, query.getSemester())
                .orderByDesc(Score::getExamTime, Score::getId);
        if ((query.getStudentNo() != null && !query.getStudentNo().trim().isEmpty()) ||
                (query.getStudentName() != null && !query.getStudentName().trim().isEmpty())) {
            LambdaQueryWrapper<Student> studentQuery = new LambdaQueryWrapper<>();
            if (query.getStudentNo() != null && !query.getStudentNo().trim().isEmpty()) {
                studentQuery.like(Student::getStudentNo, query.getStudentNo());
            }
            if (query.getStudentName() != null && !query.getStudentName().trim().isEmpty()) {
                List<Long> userIds = userService.list(new LambdaQueryWrapper<User>().select(User::getId)
                                .eq(User::getRole, "student").like(User::getRealName, query.getStudentName()))
                        .stream().map(User::getId).collect(Collectors.toList());
                if (userIds.isEmpty()) return emptyPage(query);
                studentQuery.in(Student::getUserId, userIds);
            }
            List<Long> studentIds = studentService.list(studentQuery).stream().map(Student::getId).collect(Collectors.toList());
            if (studentIds.isEmpty()) return emptyPage(query);
            wrapper.in(Score::getStudentId, studentIds);
        }
        if (query.getCourseName() != null && !query.getCourseName().trim().isEmpty()) {
            List<Long> courseIds = courseService.list(new LambdaQueryWrapper<Course>().select(Course::getId)
                            .like(Course::getCourseName, query.getCourseName()))
                    .stream().map(Course::getId).collect(Collectors.toList());
            if (courseIds.isEmpty()) return emptyPage(query);
            wrapper.in(Score::getCourseId, courseIds);
        }
        return toVOPage(page(new Page<>(query.getPage(), query.getSize()), wrapper));
    }

    @Override
    public Page<ScoreVO> getTeacherCourseScorePage(String username, Long courseId, ScoreQueryDTO query) {
        requireTeacherCourse(username, courseId);
        int current = query.getPage() == null || query.getPage() < 1 ? 1 : query.getPage();
        int size = query.getSize() == null || query.getSize() < 1 ? 10 : Math.min(100, query.getSize());
        String semester = query.getSemester() == null || query.getSemester().isBlank() ? null : query.getSemester().trim();
        LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<Score>()
                .eq(Score::getCourseId, courseId)
                .like(semester != null, Score::getSemester, semester)
                .orderByAsc(Score::getStudentId).orderByAsc(Score::getAttemptNo);
        if (query.getStudentNo() != null && !query.getStudentNo().isBlank()) {
            List<Long> studentIds = studentService.list(new LambdaQueryWrapper<Student>()
                            .select(Student::getId).like(Student::getStudentNo, query.getStudentNo().trim()))
                    .stream().map(Student::getId).toList();
            if (studentIds.isEmpty()) return emptyPage(current, size);
            wrapper.in(Score::getStudentId, studentIds);
        }
        if (query.getStudentName() != null && !query.getStudentName().isBlank()) {
            List<Long> userIds = userService.list(new LambdaQueryWrapper<User>().select(User::getId)
                            .eq(User::getRole, "student").like(User::getRealName, query.getStudentName().trim()))
                    .stream().map(User::getId).toList();
            if (userIds.isEmpty()) return emptyPage(current, size);
            List<Long> studentIds = studentService.list(new LambdaQueryWrapper<Student>()
                            .select(Student::getId).in(Student::getUserId, userIds))
                    .stream().map(Student::getId).toList();
            if (studentIds.isEmpty()) return emptyPage(current, size);
            wrapper.in(Score::getStudentId, studentIds);
        }
        return toVOPage(page(new Page<>(current, size), wrapper));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ScoreVO createTeacherCourseScore(String username, Long courseId, ScoreUpdateDTO dto, String actor) {
        Course course = requireTeacherCourse(username, courseId);
        validateTeacherScoreInput(course, dto);
        if (courseSelectionMapper.countApprovedSelection(courseId, dto.getStudentId()) == 0) {
            throw new BusinessException("只能为已确认选修该课程的学生录入成绩");
        }
        ScoreVO created = createScore(dto, actor);
        notifyScoreSubmission(course, created.getStudentNo(), actor);
        return created;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ScoreVO> createTeacherCourseScores(String username, Long courseId, List<ScoreUpdateDTO> dtos, String actor) {
        Course course = requireTeacherCourse(username, courseId);
        if (dtos == null || dtos.isEmpty() || dtos.size() > 200) {
            throw new BusinessException("每次请选择1到200名学生批量录入");
        }
        Set<String> attempts = new HashSet<>();
        Set<Long> studentIds = new HashSet<>();
        Set<Integer> attemptNumbers = new HashSet<>();
        for (ScoreUpdateDTO dto : dtos) {
            validateTeacherScoreInput(course, dto);
            if (courseSelectionMapper.countApprovedSelection(courseId, dto.getStudentId()) == 0) {
                throw new BusinessException("学号对应学生未确认选修该课程，请检查名单后重试");
            }
            String attemptKey = dto.getStudentId() + ":" + (dto.getAttemptNo() == null ? 1 : dto.getAttemptNo());
            if (!attempts.add(attemptKey)) {
                throw new BusinessException("批量数据中存在同一学生重复的考试次数");
            }
            studentIds.add(dto.getStudentId());
            attemptNumbers.add(dto.getAttemptNo() == null ? 1 : dto.getAttemptNo());
        }
        List<Score> existingScores = list(new LambdaQueryWrapper<Score>()
                .select(Score::getStudentId, Score::getAttemptNo)
                .eq(Score::getCourseId, courseId)
                .eq(Score::getSemester, course.getSemester())
                .in(Score::getStudentId, studentIds)
                .in(Score::getAttemptNo, attemptNumbers));
        Set<String> existingAttempts = existingScores.stream()
                .map(score -> score.getStudentId() + ":" + score.getAttemptNo())
                .collect(Collectors.toSet());
        if (!Collections.disjoint(attempts, existingAttempts)) {
            Set<Long> conflicts = existingAttempts.stream().filter(attempts::contains)
                    .map(key -> Long.valueOf(key.substring(0, key.indexOf(':')))).collect(Collectors.toSet());
            Map<Long, String> studentNos = studentService.listByIds(conflicts).stream()
                    .collect(Collectors.toMap(Student::getId, Student::getStudentNo));
            String conflictNos = conflicts.stream().map(id -> studentNos.getOrDefault(id, String.valueOf(id)))
                    .collect(Collectors.joining("、"));
            throw new BusinessException("以下学生已存在相同考试次数的成绩，请调整考试次数或移除：" + conflictNos);
        }
        List<ScoreVO> createdScores = new ArrayList<>(dtos.size());
        for (ScoreUpdateDTO dto : dtos) createdScores.add(createScore(dto, actor));
        notificationService.notifyAdmins("SCORE_SUBMITTED", "收到教师成绩批量提交",
                actor + " 已提交课程“" + course.getCourseName() + "”（" + course.getSemester()
                        + "）的 " + createdScores.size() + " 条成绩，待管理员发布。");
        return createdScores;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ScoreVO updateTeacherCourseScore(String username, Long courseId, Long scoreId, ScoreUpdateDTO dto, String actor) {
        Course course = requireTeacherCourse(username, courseId);
        validateTeacherScoreInput(course, dto);
        Score existing = getById(scoreId);
        if (existing == null || !courseId.equals(existing.getCourseId())) {
            throw new BusinessException("成绩不存在或无权访问");
        }
        if (!"DRAFT".equals(existing.getPublishStatus())) {
            throw new BusinessException("已发布成绩不能由教师修改，请联系管理员办理更正");
        }
        if (!existing.getStudentId().equals(dto.getStudentId())) {
            throw new BusinessException("不能将成绩转给其他学生");
        }
        if (courseSelectionMapper.countApprovedSelection(courseId, dto.getStudentId()) == 0) {
            throw new BusinessException("该学生当前不是此课程的已确认选课学生");
        }
        ScoreVO updated = updateScore(scoreId, dto, actor);
        notifyScoreSubmission(course, updated.getStudentNo(), actor);
        return updated;
    }

    @Override
    public List<ScoreChangeLogVO> getTeacherCourseScoreHistory(String username, Long courseId, Long scoreId) {
        requireTeacherCourse(username, courseId);
        Score score = getById(scoreId);
        if (score == null || !courseId.equals(score.getCourseId())) {
            throw new BusinessException("成绩不存在或无权访问");
        }
        return getScoreChangeLogs(scoreId);
    }

    private Course requireTeacherCourse(String username, Long courseId) {
        User user = userService.findByUsername(username);
        if (user == null) throw new BusinessException("当前账号不存在");
        Teacher teacher = getTeacherForUser(user.getId());
        Course course = courseService.getById(courseId);
        if (course == null || !teacher.getId().equals(course.getTeacherId())) {
            throw new BusinessException("课程不存在或无权访问");
        }
        return course;
    }

    private Teacher getTeacherForUser(Long userId) {
        Teacher teacher = teacherService.getOne(new LambdaQueryWrapper<Teacher>().eq(Teacher::getUserId, userId));
        if (teacher == null) throw new BusinessException("教师信息不存在");
        return teacher;
    }

    private void validateTeacherScoreInput(Course course, ScoreUpdateDTO dto) {
        if (dto == null) throw new BusinessException("成绩内容不能为空");
        if (dto.getCourseId() != null && !course.getId().equals(dto.getCourseId())) {
            throw new BusinessException("提交的课程与当前授课课程不一致");
        }
        dto.setCourseId(course.getId());
        String courseSemester = course.getSemester();
        if (courseSemester == null || courseSemester.isBlank()) {
            throw new BusinessException("课程未设置学期，暂不能录入成绩");
        }
        if (dto.getSemester() == null || dto.getSemester().isBlank()) dto.setSemester(courseSemester);
        if (!courseSemester.equals(dto.getSemester().trim())) {
            throw new BusinessException("成绩学期必须与课程开课学期一致");
        }
        if (dto.getExamTime() == null) throw new BusinessException("请选择考试时间");
        validateReferences(dto);
        if (dto.getChangeReason() == null || dto.getChangeReason().isBlank()) {
            dto.setChangeReason("教师提交成绩，待管理员发布");
        }
    }

    private void notifyScoreSubmission(Course course, String studentNo, String actor) {
        notificationService.notifyAdmins("SCORE_SUBMITTED", "收到教师成绩提交",
                actor + " 已提交课程“" + course.getCourseName() + "”（" + course.getSemester()
                        + "）的成绩，学生学号：" + studentNo + "。成绩待管理员发布。 ");
    }

    private Page<ScoreVO> emptyPage(long current, long size) {
        Page<ScoreVO> result = new Page<>(current, size, 0);
        result.setRecords(Collections.emptyList());
        return result;
    }

    @Override
    public ScoreDistributionResponse getScoreDistribution(String period) {
        if (!"semester".equals(period) && !"year".equals(period)) {
            throw new BusinessException("统计范围无效");
        }

        String latestSemester = baseMapper.selectLatestSemester();
        List<ScoreDistributionVO> distribution = new java.util.ArrayList<>(List.of(
                new ScoreDistributionVO("优秀（90分及以上）", 0L),
                new ScoreDistributionVO("良好（80-89分）", 0L),
                new ScoreDistributionVO("中等（70-79分）", 0L),
                new ScoreDistributionVO("及格（60-69分）", 0L),
                new ScoreDistributionVO("不及格（60分以下）", 0L)
        ));
        if (latestSemester == null || latestSemester.trim().isEmpty()) {
            return new ScoreDistributionResponse("暂无成绩", null, distribution);
        }

        String academicYear = latestSemester;
        int lastSeparator = latestSemester.lastIndexOf('-');
        if (lastSeparator > 0) academicYear = latestSemester.substring(0, lastSeparator);
        boolean byAcademicYear = "year".equals(period);
        String filter = byAcademicYear ? academicYear : latestSemester;
        Map<String, Long> counts = baseMapper.selectScoreDistribution(filter, byAcademicYear).stream()
                .collect(Collectors.toMap(ScoreDistributionVO::getName, ScoreDistributionVO::getValue));
        distribution.forEach(item -> item.setValue(counts.getOrDefault(item.getName(), 0L)));

        String periodLabel = byAcademicYear ? academicYear + "学年" : latestSemester;
        return new ScoreDistributionResponse(periodLabel, latestSemester, distribution);
    }

    @Override
    public ScoreVO getScore(Long id) {
        Score score = getById(id);
        if (score == null) throw new BusinessException("成绩不存在");
        return toVOPage(new Page<Score>(1, 1, 1).setRecords(Collections.singletonList(score))).getRecords().get(0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ScoreVO createScore(ScoreUpdateDTO dto, String actor) {
        validateReferences(dto);
        if (count(new LambdaQueryWrapper<Score>().eq(Score::getStudentId, dto.getStudentId())
                .eq(Score::getCourseId, dto.getCourseId()).eq(Score::getSemester, dto.getSemester())
                .eq(Score::getAttemptNo, normalizeAttemptNo(dto.getAttemptNo()))) > 0) {
            throw new BusinessException("该学生该课程的考试次数已存在");
        }
        Score score = new Score();
        apply(score, dto);
        score.setPublishStatus("DRAFT");
        score.setIsDeleted(0);
        score.setCreateBy(actor); score.setUpdateBy(actor);
        save(score);
        writeChangeLog("CREATE", null, score, blankToDefault(dto.getChangeReason(), "新增成绩，待发布"), actor);
        operationAuditService.record(actor, "SCORE_CREATE", "SCORE", score.getId(), "录入成绩，待发布");
        return getScore(score.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ScoreVO updateScore(Long id, ScoreUpdateDTO dto, String actor) {
        Score score = getById(id);
        if (score == null) throw new BusinessException("成绩不存在");
        if (dto.getChangeReason() == null || dto.getChangeReason().isBlank()) throw new BusinessException("成绩更正必须填写原因");
        validateReferences(dto);
        if (count(new LambdaQueryWrapper<Score>().eq(Score::getStudentId, dto.getStudentId())
                .eq(Score::getCourseId, dto.getCourseId()).eq(Score::getSemester, dto.getSemester())
                .eq(Score::getAttemptNo, normalizeAttemptNo(dto.getAttemptNo()))
                .ne(Score::getId, id)) > 0) throw new BusinessException("该学生该学期的课程成绩已存在");
        Score before = copy(score);
        apply(score, dto);
        score.setPublishStatus("DRAFT");
        score.setUpdateBy(actor);
        updateById(score);
        writeChangeLog("UPDATE", before, score, dto.getChangeReason().trim(), actor);
        operationAuditService.record(actor, "SCORE_UPDATE", "SCORE", score.getId(), "更正成绩：" + dto.getChangeReason().trim());
        return getScore(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteScore(Long id, String reason, String actor) {
        Score score = getById(id);
        if (score == null) throw new BusinessException("成绩不存在");
        if (reason == null || reason.isBlank()) throw new BusinessException("删除成绩必须填写原因");
        Score before = copy(score);
        score.setUpdateBy(actor);
        score.setIsDeleted(1);
        updateById(score);
        writeChangeLog("DELETE", before, null, reason.trim(), actor);
        operationAuditService.record(actor, "SCORE_DELETE", "SCORE", id, "删除成绩：" + reason.trim());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int publishScores(List<Long> ids, String reason, String actor) {
        if (ids == null || ids.isEmpty() || ids.size() > 500) throw new BusinessException("每次请选择1到500条成绩");
        if (reason == null || reason.isBlank()) throw new BusinessException("发布成绩必须填写发布说明");
        List<Long> uniqueIds = ids.stream().filter(id -> id != null).distinct().toList();
        if (uniqueIds.size() != ids.size()) throw new BusinessException("成绩编号重复或无效");
        List<Score> selected = listByIds(uniqueIds);
        if (selected.size() != uniqueIds.size()) throw new BusinessException("部分成绩不存在或已删除，请刷新后重试");
        if (selected.stream().anyMatch(score -> !"DRAFT".equals(score.getPublishStatus()))) {
            throw new BusinessException("选中的成绩包含已发布记录，请刷新后重新选择待发布成绩");
        }
        for (Score score : selected) {
            Score before = copy(score);
            score.setPublishStatus("PUBLISHED"); score.setUpdateBy(actor);
            updateById(score);
            writeChangeLog("PUBLISH", before, score, reason.trim(), actor);
            operationAuditService.record(actor, "SCORE_PUBLISH", "SCORE", score.getId(), "发布成绩：" + reason.trim());
            Student student = studentService.getById(score.getStudentId());
            if (student != null) notificationService.notifyUser(student.getUserId(), "SCORE_PUBLISHED", "成绩已发布",
                    "你的" + score.getSemester() + "成绩已发布，请前往成绩查询查看。");
        }
        return selected.size();
    }

    @Override
    public List<ScoreChangeLogVO> getScoreChangeLogs(Long id) {
        if (getById(id) == null) throw new BusinessException("成绩不存在");
        return scoreChangeLogMapper.selectByScoreId(id);
    }

    private void validateReferences(ScoreUpdateDTO dto) {
        if (dto.getStudentId() == null || studentService.getById(dto.getStudentId()) == null) throw new BusinessException("请选择有效学生");
        if (dto.getCourseId() == null || courseService.getById(dto.getCourseId()) == null) throw new BusinessException("请选择有效课程");
        if (dto.getScore() == null || dto.getScore().compareTo(BigDecimal.ZERO) < 0 || dto.getScore().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new BusinessException("成绩必须在0到100之间");
        }
        if (dto.getSemester() == null || dto.getSemester().trim().isEmpty()) throw new BusinessException("学期不能为空");
        String attemptType = dto.getAttemptType() == null || dto.getAttemptType().isBlank() ? "REGULAR" : dto.getAttemptType().trim().toUpperCase();
        if (!List.of("REGULAR", "MAKEUP", "RETAKE").contains(attemptType)) throw new BusinessException("考试类型无效");
        if (normalizeAttemptNo(dto.getAttemptNo()) < 1 || normalizeAttemptNo(dto.getAttemptNo()) > 10) throw new BusinessException("考试次数必须在1到10之间");
    }

    private void apply(Score score, ScoreUpdateDTO dto) {
        score.setStudentId(dto.getStudentId()); score.setCourseId(dto.getCourseId()); score.setScore(dto.getScore());
        score.setGrade(letterGrade(dto.getScore()));
        score.setGradePoint(gradePoint(dto.getScore())); score.setSemester(dto.getSemester().trim());
        score.setAttemptType(dto.getAttemptType() == null || dto.getAttemptType().isBlank() ? "REGULAR" : dto.getAttemptType().trim().toUpperCase());
        score.setAttemptNo(normalizeAttemptNo(dto.getAttemptNo()));
        score.setExamTime(dto.getExamTime()); score.setComment(dto.getComment());
    }

    private int normalizeAttemptNo(Integer attemptNo) { return attemptNo == null ? 1 : attemptNo; }

    private String blankToDefault(String value, String fallback) { return value == null || value.isBlank() ? fallback : value.trim(); }

    private Score copy(Score source) {
        Score copy = new Score(); copy.setId(source.getId()); copy.setScore(source.getScore());
        copy.setAttemptType(source.getAttemptType()); copy.setAttemptNo(source.getAttemptNo());
        copy.setPublishStatus(source.getPublishStatus()); return copy;
    }

    private void writeChangeLog(String action, Score before, Score after, String reason, String actor) {
        ScoreChangeLog log = new ScoreChangeLog();
        log.setScoreId(after != null ? after.getId() : before.getId()); log.setAction(action);
        if (before != null) {
            log.setOldScore(before.getScore()); log.setOldAttemptType(before.getAttemptType());
            log.setOldPublishStatus(before.getPublishStatus());
        }
        if (after != null) {
            log.setNewScore(after.getScore()); log.setNewAttemptType(after.getAttemptType());
            log.setNewPublishStatus(after.getPublishStatus());
        } else log.setNewPublishStatus("DELETED");
        log.setReason(reason); log.setOperator(actor); scoreChangeLogMapper.insert(log);
    }

    private BigDecimal gradePoint(BigDecimal score) {
        double n = score.doubleValue();
        double point = n >= 90 ? 4.0 : n >= 85 ? 3.7 : n >= 82 ? 3.3 : n >= 78 ? 3.0 : n >= 75 ? 2.7 : n >= 72 ? 2.3 : n >= 68 ? 2.0 : n >= 64 ? 1.5 : n >= 60 ? 1.0 : 0.0;
        return BigDecimal.valueOf(point).setScale(2, RoundingMode.HALF_UP);
    }

    private String letterGrade(BigDecimal score) {
        int n = score.intValue();
        return n >= 90 ? "A+" : n >= 85 ? "A" : n >= 80 ? "B+" : n >= 75 ? "B" : n >= 70 ? "C+" : n >= 60 ? "C" : n >= 50 ? "D" : "F";
    }

    private Page<ScoreVO> emptyPage(ScoreQueryDTO query) {
        Page<ScoreVO> page = new Page<>(query.getPage(), query.getSize());
        page.setRecords(Collections.emptyList());
        return page;
    }

    private Page<ScoreVO> toVOPage(Page<Score> source) {
        List<Long> studentIds = source.getRecords().stream().map(Score::getStudentId).distinct().collect(Collectors.toList());
        List<Long> courseIds = source.getRecords().stream().map(Score::getCourseId).distinct().collect(Collectors.toList());
        Map<Long, Student> students = studentIds.isEmpty() ? Collections.emptyMap() : studentService.listByIds(studentIds)
                .stream().collect(Collectors.toMap(Student::getId, Function.identity()));
        Map<Long, Course> courses = courseIds.isEmpty() ? Collections.emptyMap() : courseService.listByIds(courseIds)
                .stream().collect(Collectors.toMap(Course::getId, Function.identity()));
        List<Long> userIds = students.values().stream().map(Student::getUserId).distinct().collect(Collectors.toList());
        Map<Long, User> users = userIds.isEmpty() ? Collections.emptyMap() : userService.listByIds(userIds)
                .stream().collect(Collectors.toMap(User::getId, Function.identity()));
        Page<ScoreVO> result = new Page<>(source.getCurrent(), source.getSize(), source.getTotal());
        result.setRecords(source.getRecords().stream().map(row -> {
            ScoreVO vo = new ScoreVO();
            vo.setId(row.getId()); vo.setStudentId(row.getStudentId()); vo.setCourseId(row.getCourseId());
            vo.setScore(row.getScore()); vo.setGrade(row.getGrade()); vo.setGradePoint(row.getGradePoint());
            vo.setSemester(row.getSemester()); vo.setExamTime(row.getExamTime()); vo.setComment(row.getComment());
            vo.setAttemptType(row.getAttemptType()); vo.setAttemptNo(row.getAttemptNo()); vo.setPublishStatus(row.getPublishStatus());
            vo.setRemark(row.getComment()); vo.setStatus(row.getScore().compareTo(BigDecimal.valueOf(60)) >= 0 ? "合格" : "不合格");
            vo.setCreateTime(row.getCreateTime()); vo.setUpdateTime(row.getUpdateTime());
            Student student = students.get(row.getStudentId());
            if (student != null) {
                vo.setStudentNo(student.getStudentNo());
                User user = users.get(student.getUserId());
                if (user != null) vo.setStudentName(user.getRealName());
            }
            Course course = courses.get(row.getCourseId());
            if (course != null) {
                vo.setCourseCode(course.getCourseCode()); vo.setCourseNo(course.getCourseCode());
                vo.setCourseName(course.getCourseName()); vo.setCredit(course.getCredits());
            }
            return vo;
        }).collect(Collectors.toList()));
        return result;
    }
}
