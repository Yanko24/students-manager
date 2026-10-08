package com.example.studentsmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.studentsmanager.exception.BusinessException;
import com.example.studentsmanager.mapper.TeacherMapper;
import com.example.studentsmanager.model.dto.teacher.TeacherQueryDTO;
import com.example.studentsmanager.model.entity.Teacher;
import com.example.studentsmanager.model.entity.User;
import com.example.studentsmanager.model.vo.teacher.TeacherListVO;
import com.example.studentsmanager.service.TeacherService;
import com.example.studentsmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    @Autowired
    private UserService userService;

    @Override
    public Page<TeacherListVO> getTeacherPage(TeacherQueryDTO queryDTO) {
        Page<Teacher> teacherPage = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        LambdaQueryWrapper<Teacher> teacherQuery = new LambdaQueryWrapper<Teacher>()
                .like(queryDTO.getTeacherNo() != null && !queryDTO.getTeacherNo().trim().isEmpty(),
                        Teacher::getTeacherNumber, queryDTO.getTeacherNo())
                .like(queryDTO.getDepartment() != null && !queryDTO.getDepartment().trim().isEmpty(),
                        Teacher::getDepartment, queryDTO.getDepartment())
                .orderByDesc(Teacher::getCreateTime);

        if (queryDTO.getRealName() != null && !queryDTO.getRealName().trim().isEmpty()) {
            List<Long> userIds = userService.list(new LambdaQueryWrapper<User>()
                            .select(User::getId)
                            .eq(User::getRole, "teacher")
                            .like(User::getRealName, queryDTO.getRealName()))
                    .stream().map(User::getId).collect(Collectors.toList());
            if (userIds.isEmpty()) {
                Page<TeacherListVO> emptyPage = new Page<>(queryDTO.getPage(), queryDTO.getSize());
                emptyPage.setRecords(Collections.emptyList());
                return emptyPage;
            }
            teacherQuery.in(Teacher::getUserId, userIds);
        }

        Page<Teacher> result = page(teacherPage, teacherQuery);
        List<Long> userIds = result.getRecords().stream().map(Teacher::getUserId).collect(Collectors.toList());
        Map<Long, User> usersById = userIds.isEmpty()
                ? Collections.emptyMap()
                : userService.listByIds(userIds).stream().collect(Collectors.toMap(User::getId, Function.identity()));

        Page<TeacherListVO> response = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        response.setRecords(result.getRecords().stream()
                .map(teacher -> toTeacherListVO(teacher, usersById.get(teacher.getUserId())))
                .collect(Collectors.toList()));
        return response;
    }

    @Override
    public TeacherListVO getTeacherDetail(Long id) {
        Teacher teacher = getById(id);
        if (teacher == null) throw new BusinessException("教师不存在");
        User user = userService.getById(teacher.getUserId());
        if (user == null) throw new BusinessException("教师账号不存在");
        return toTeacherListVO(teacher, user);
    }

    private TeacherListVO toTeacherListVO(Teacher teacher, User user) {
        TeacherListVO vo = new TeacherListVO();
        vo.setId(teacher.getId());
        vo.setTeacherNo(teacher.getTeacherNumber());
        vo.setDepartment(teacher.getDepartment());
        vo.setTitle(teacher.getTitle());
        vo.setStatus(teacher.getStatus());
        vo.setHireDate(teacher.getHireDate());
        vo.setCreateTime(teacher.getCreateTime());
        vo.setUpdateTime(teacher.getUpdateTime());
        if (user != null) {
            vo.setRealName(user.getRealName());
            vo.setGender(user.getGender());
            vo.setPhone(user.getPhone());
            vo.setEmail(user.getEmail());
        }
        return vo;
    }

    @Override
    public Teacher getByTeacherNumber(String teacherNumber) {
        return getOne(new LambdaQueryWrapper<Teacher>()
                .eq(Teacher::getTeacherNumber, teacherNumber));
    }

    @Override
    @Transactional
    public Teacher createTeacher(Teacher teacher) {
        // 检查教师编号是否已存在
        Teacher existingTeacher = getByTeacherNumber(teacher.getTeacherNumber());
        if (existingTeacher != null) {
            throw new BusinessException("教师编号已存在");
        }

        // 创建用户账号
        User user = teacher.getUser();
        if (user != null) {
            user.setRole("teacher");
            userService.createUser(user);
            teacher.setUserId(user.getId());
        }

        // 保存教师信息
        save(teacher);
        return teacher;
    }

    @Override
    @Transactional
    public boolean updateTeacher(Teacher teacher) {
        // 检查教师是否存在
        Teacher existingTeacher = getById(teacher.getId());
        if (existingTeacher == null) {
            throw new BusinessException("教师不存在");
        }

        if (teacher.getTeacherNumber() != null && !teacher.getTeacherNumber().equals(existingTeacher.getTeacherNumber())) {
            Teacher duplicate = getByTeacherNumber(teacher.getTeacherNumber());
            if (duplicate != null && !duplicate.getId().equals(existingTeacher.getId())) {
                throw new BusinessException("教师编号已存在");
            }
        }

        // 更新用户信息
        if (teacher.getUser() != null) {
            User user = teacher.getUser();
            user.setId(existingTeacher.getUserId());
            if (teacher.getTeacherNumber() != null) user.setUsername(teacher.getTeacherNumber());
            user.setRole("teacher");
            userService.updateById(user);
        }

        // 更新教师信息
        return updateById(teacher);
    }

    @Override
    @Transactional
    public boolean deleteTeacher(Long id) {
        // 检查教师是否存在
        Teacher teacher = getById(id);
        if (teacher == null) {
            throw new BusinessException("教师不存在");
        }

        // 删除用户账号
        userService.removeById(teacher.getUserId());

        // 删除教师信息
        return removeById(id);
    }
}
