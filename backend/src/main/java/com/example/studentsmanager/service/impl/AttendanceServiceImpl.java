package com.example.studentsmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studentsmanager.exception.BusinessException;
import com.example.studentsmanager.mapper.AttendanceMapper;
import com.example.studentsmanager.model.dto.attendance.AttendanceQueryDTO;
import com.example.studentsmanager.model.dto.attendance.AttendanceUpdateDTO;
import com.example.studentsmanager.model.entity.Attendance;
import com.example.studentsmanager.model.entity.Course;
import com.example.studentsmanager.model.entity.Student;
import com.example.studentsmanager.model.entity.Teacher;
import com.example.studentsmanager.model.entity.User;
import com.example.studentsmanager.model.vo.attendance.AttendanceStatsVO;
import com.example.studentsmanager.model.vo.attendance.AttendanceTrendVO;
import com.example.studentsmanager.model.vo.attendance.AttendanceVO;
import com.example.studentsmanager.service.AttendanceService;
import com.example.studentsmanager.service.CourseService;
import com.example.studentsmanager.service.StudentService;
import com.example.studentsmanager.service.TeacherService;
import com.example.studentsmanager.service.UserService;
import com.example.studentsmanager.utils.security.SecurityUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AttendanceServiceImpl extends ServiceImpl<AttendanceMapper, Attendance> implements AttendanceService {
    private static final List<String> STATUSES = Arrays.asList("正常", "迟到", "早退", "缺勤", "请假");
    private final StudentService studentService;
    private final CourseService courseService;
    private final TeacherService teacherService;
    private final UserService userService;

    public AttendanceServiceImpl(StudentService studentService, CourseService courseService,
                                 TeacherService teacherService, UserService userService) {
        this.studentService = studentService;
        this.courseService = courseService;
        this.teacherService = teacherService;
        this.userService = userService;
    }

    @Override
    public Page<AttendanceVO> getAdminAttendancePage(AttendanceQueryDTO query) {
        return queryPage(query, null, null);
    }

    @Override
    public Page<AttendanceVO> getTeacherAttendancePage(String username, AttendanceQueryDTO query) {
        User user = requireUser(username);
        Teacher teacher = teacherService.getOne(new LambdaQueryWrapper<Teacher>().eq(Teacher::getUserId, user.getId()));
        if (teacher == null) throw new BusinessException("教师信息不存在");
        return queryPage(query, null, teacher.getId());
    }

    @Override
    public Page<AttendanceVO> getStudentAttendancePage(String username, AttendanceQueryDTO query) {
        return queryPage(query, getStudent(username).getId(), null);
    }

    @Override
    public AttendanceStatsVO getStudentAttendanceStats(String username, AttendanceQueryDTO query) {
        normalizeQuery(query);
        return baseMapper.selectStudentAttendanceStats(getStudent(username).getId(), query);
    }

    private Student getStudent(String username) {
        User user = requireUser(username);
        Student student = studentService.getOne(new LambdaQueryWrapper<Student>().eq(Student::getUserId, user.getId()));
        if (student == null) throw new BusinessException("学生信息不存在");
        return student;
    }

    private Page<AttendanceVO> queryPage(AttendanceQueryDTO query, Long studentId, Long teacherId) {
        normalizeQuery(query);
        int page = query.getPage() == null || query.getPage() < 1 ? 1 : query.getPage();
        int size = query.getSize() == null || query.getSize() < 1 ? 10 : Math.min(query.getSize(), 100);
        query.setPage(page);
        query.setSize(size);
        return baseMapper.selectAttendancePage(new Page<>(page, size), query, studentId, teacherId);
    }

    private void normalizeQuery(AttendanceQueryDTO query) {
        query.setStudentNo(trimToNull(query.getStudentNo()));
        query.setStudentName(trimToNull(query.getStudentName()));
        query.setCourseName(trimToNull(query.getCourseName()));
        query.setClassName(trimToNull(query.getClassName()));
        query.setSemester(trimToNull(query.getSemester()));
        query.setStatus(trimToNull(query.getStatus()));
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        return value.trim();
    }

    @Override
    public AttendanceVO getAttendance(Long id) {
        AttendanceVO attendance = baseMapper.selectAttendanceById(id);
        if (attendance == null) throw new BusinessException("考勤记录不存在");
        return attendance;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AttendanceVO createAttendance(AttendanceUpdateDTO dto) {
        Attendance record = new Attendance();
        apply(record, dto, true);
        ensureNoDuplicate(record, null);
        record.setIsDeleted(0);
        record.setCreateBy(SecurityUtils.getCurrentUsername());
        record.setUpdateBy(SecurityUtils.getCurrentUsername());
        try {
            save(record);
        } catch (DuplicateKeyException ex) {
            throw new BusinessException("该学生在此课程、日期和课时已有考勤记录");
        }
        return getAttendance(record.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AttendanceVO updateAttendance(Long id, AttendanceUpdateDTO dto) {
        Attendance record = getById(id);
        if (record == null) throw new BusinessException("考勤记录不存在");
        apply(record, dto, false);
        ensureNoDuplicate(record, id);
        record.setUpdateBy(SecurityUtils.getCurrentUsername());
        try {
            updateById(record);
        } catch (DuplicateKeyException ex) {
            throw new BusinessException("该学生在此课程、日期和课时已有考勤记录");
        }
        return getAttendance(id);
    }

    private void apply(Attendance record, AttendanceUpdateDTO dto, boolean creating) {
        if (creating) {
            Student student = dto.getStudentId() != null ? studentService.getById(dto.getStudentId()) :
                    studentService.getOne(new LambdaQueryWrapper<Student>().eq(Student::getStudentNo, dto.getStudentNo()));
            if (student == null) throw new BusinessException("学生不存在，请检查学号");
            record.setStudentId(student.getId());
            record.setCourseId(dto.getCourseId());
        }
        Course course = courseService.getById(record.getCourseId());
        if (course == null) throw new BusinessException("课程不存在");
        if (dto.getDate() == null) throw new BusinessException("考勤日期不能为空");
        if (dto.getStatus() == null || !STATUSES.contains(dto.getStatus())) throw new BusinessException("考勤状态无效");
        record.setDate(dto.getDate());
        record.setClassPeriod(dto.getClassPeriod() == null ? "" : dto.getClassPeriod().trim());
        record.setStatus(dto.getStatus());
        record.setRemark(dto.getRemark());
    }

    private void ensureNoDuplicate(Attendance record, Long excludedId) {
        LambdaQueryWrapper<Attendance> query = new LambdaQueryWrapper<Attendance>()
                .eq(Attendance::getStudentId, record.getStudentId())
                .eq(Attendance::getCourseId, record.getCourseId())
                .eq(Attendance::getDate, record.getDate())
                .eq(Attendance::getClassPeriod, record.getClassPeriod());
        if (excludedId != null) query.ne(Attendance::getId, excludedId);
        if (count(query) > 0) throw new BusinessException("该学生在此课程、日期和课时已有考勤记录");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAttendance(Long id) {
        if (getById(id) == null) throw new BusinessException("考勤记录不存在");
        removeById(id);
    }

    @Override
    public AttendanceStatsVO getAttendanceStats(String period) {
        LocalDate end = LocalDate.now();
        LocalDate start;
        if ("week".equals(period)) start = end.minusDays(6);
        else if ("month".equals(period)) start = end.minusDays(29);
        else throw new BusinessException("统计范围无效");
        return baseMapper.selectAttendanceStats(start, end);
    }

    @Override
    public List<AttendanceTrendVO> getAttendanceTrend(String period) {
        LocalDate end = LocalDate.now();
        int days;
        if ("week".equals(period)) days = 7;
        else if ("month".equals(period)) days = 30;
        else throw new BusinessException("统计范围无效");
        LocalDate start = end.minusDays(days - 1L);
        Map<LocalDate, AttendanceTrendVO> byDate = new HashMap<>();
        for (AttendanceTrendVO row : baseMapper.selectAttendanceTrend(start, end)) byDate.put(row.getDate(), row);
        List<AttendanceTrendVO> result = new ArrayList<>(days);
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            AttendanceTrendVO row = byDate.get(date);
            if (row == null) {
                row = new AttendanceTrendVO();
                row.setDate(date);
                row.setTotalCount(0L);
                row.setAttendedCount(0L);
                row.setAttendanceRate(BigDecimal.ZERO);
            }
            result.add(row);
        }
        return result;
    }

    private User requireUser(String username) {
        User user = userService.findByUsername(username);
        if (user == null) throw new BusinessException("用户信息不存在");
        return user;
    }
}
