package com.example.studentsmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studentsmanager.exception.BusinessException;
import com.example.studentsmanager.mapper.ScoreMapper;
import com.example.studentsmanager.model.dto.score.ScoreQueryDTO;
import com.example.studentsmanager.model.dto.score.ScoreUpdateDTO;
import com.example.studentsmanager.model.entity.*;
import com.example.studentsmanager.model.vo.score.ScoreVO;
import com.example.studentsmanager.model.vo.score.ScoreDistributionResponse;
import com.example.studentsmanager.model.vo.score.ScoreDistributionVO;
import com.example.studentsmanager.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ScoreServiceImpl extends ServiceImpl<ScoreMapper, Score> implements ScoreService {
    private final StudentService studentService;
    private final CourseService courseService;
    private final UserService userService;

    public ScoreServiceImpl(StudentService studentService, CourseService courseService, UserService userService) {
        this.studentService = studentService;
        this.courseService = courseService;
        this.userService = userService;
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
    public ScoreVO createScore(ScoreUpdateDTO dto) {
        validateReferences(dto);
        if (count(new LambdaQueryWrapper<Score>().eq(Score::getStudentId, dto.getStudentId())
                .eq(Score::getCourseId, dto.getCourseId()).eq(Score::getSemester, dto.getSemester())) > 0) {
            throw new BusinessException("该学生该学期的课程成绩已存在");
        }
        Score score = new Score();
        apply(score, dto);
        score.setIsDeleted(0);
        save(score);
        return getScore(score.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ScoreVO updateScore(Long id, ScoreUpdateDTO dto) {
        Score score = getById(id);
        if (score == null) throw new BusinessException("成绩不存在");
        validateReferences(dto);
        if (count(new LambdaQueryWrapper<Score>().eq(Score::getStudentId, dto.getStudentId())
                .eq(Score::getCourseId, dto.getCourseId()).eq(Score::getSemester, dto.getSemester())
                .ne(Score::getId, id)) > 0) throw new BusinessException("该学生该学期的课程成绩已存在");
        apply(score, dto);
        updateById(score);
        return getScore(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteScore(Long id) {
        if (getById(id) == null) throw new BusinessException("成绩不存在");
        removeById(id);
    }

    private void validateReferences(ScoreUpdateDTO dto) {
        if (dto.getStudentId() == null || studentService.getById(dto.getStudentId()) == null) throw new BusinessException("请选择有效学生");
        if (dto.getCourseId() == null || courseService.getById(dto.getCourseId()) == null) throw new BusinessException("请选择有效课程");
        if (dto.getScore() == null || dto.getScore().compareTo(BigDecimal.ZERO) < 0 || dto.getScore().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new BusinessException("成绩必须在0到100之间");
        }
        if (dto.getSemester() == null || dto.getSemester().trim().isEmpty()) throw new BusinessException("学期不能为空");
    }

    private void apply(Score score, ScoreUpdateDTO dto) {
        score.setStudentId(dto.getStudentId()); score.setCourseId(dto.getCourseId()); score.setScore(dto.getScore());
        score.setGrade(letterGrade(dto.getScore()));
        score.setGradePoint(gradePoint(dto.getScore())); score.setSemester(dto.getSemester().trim());
        score.setExamTime(dto.getExamTime()); score.setComment(dto.getComment());
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
