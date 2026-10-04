package com.example.studentsmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studentsmanager.exception.BusinessException;
import com.example.studentsmanager.mapper.CourseMapper;
import com.example.studentsmanager.model.dto.course.CourseQueryDTO;
import com.example.studentsmanager.model.dto.course.CourseUpdateDTO;
import com.example.studentsmanager.model.entity.Course;
import com.example.studentsmanager.model.entity.Teacher;
import com.example.studentsmanager.model.entity.User;
import com.example.studentsmanager.model.vo.course.CourseVO;
import com.example.studentsmanager.service.CourseService;
import com.example.studentsmanager.service.TeacherService;
import com.example.studentsmanager.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
    private final TeacherService teacherService;
    private final UserService userService;

    public CourseServiceImpl(TeacherService teacherService, UserService userService) {
        this.teacherService = teacherService;
        this.userService = userService;
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
    @Transactional(rollbackFor = Exception.class)
    public CourseVO createCourse(CourseUpdateDTO dto) {
        if (count(new LambdaQueryWrapper<Course>().eq(Course::getCourseCode, dto.getCode())) > 0) {
            throw new BusinessException("课程代码已存在");
        }
        Course course = new Course();
        apply(course, dto);
        course.setIsDeleted(0);
        save(course);
        return getCourse(course.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseVO updateCourse(Long id, CourseUpdateDTO dto) {
        Course course = getById(id);
        if (course == null) throw new BusinessException("课程不存在");
        if (count(new LambdaQueryWrapper<Course>().eq(Course::getCourseCode, dto.getCode()).ne(Course::getId, id)) > 0) {
            throw new BusinessException("课程代码已存在");
        }
        apply(course, dto);
        updateById(course);
        return getCourse(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCourse(Long id) {
        if (getById(id) == null) throw new BusinessException("课程不存在");
        removeById(id);
    }

    private void apply(Course course, CourseUpdateDTO dto) {
        course.setCourseCode(dto.getCode());
        course.setCourseName(dto.getName());
        course.setTeacherId(dto.getTeacherId());
        course.setCredits(dto.getCredit());
        course.setCourseType(dto.getType());
        course.setSemester(dto.getSemester());
        course.setHours(dto.getHours());
        course.setStatus(dto.getStatus() == null ? 0 : dto.getStatus());
        course.setDescription(dto.getDescription());
        course.setObjectives(dto.getObjectives());
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
            vo.setId(course.getId()); vo.setCode(course.getCourseCode()); vo.setName(course.getCourseName());
            vo.setTeacherId(course.getTeacherId()); vo.setCredit(course.getCredits()); vo.setHours(course.getHours());
            vo.setType(course.getCourseType()); vo.setSemester(course.getSemester()); vo.setStatus(course.getStatus());
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
        return result;
    }
}
