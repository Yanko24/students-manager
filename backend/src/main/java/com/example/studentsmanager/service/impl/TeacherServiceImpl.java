package com.example.studentsmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studentsmanager.exception.BusinessException;
import com.example.studentsmanager.mapper.TeacherMapper;
import com.example.studentsmanager.model.entity.Teacher;
import com.example.studentsmanager.model.entity.User;
import com.example.studentsmanager.service.TeacherService;
import com.example.studentsmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    @Autowired
    private UserService userService;

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

        // 更新用户信息
        if (teacher.getUser() != null) {
            User user = teacher.getUser();
            user.setId(existingTeacher.getUserId());
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
