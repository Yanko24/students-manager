package com.example.studentsmanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studentsmanager.model.entity.Teacher;

public interface TeacherService extends IService<Teacher> {
    // 根据教师编号查询教师
    Teacher getByTeacherNumber(String teacherNumber);

    // 创建教师（包括用户信息）
    Teacher createTeacher(Teacher teacher);

    // 更新教师信息
    boolean updateTeacher(Teacher teacher);

    // 删除教师
    boolean deleteTeacher(Long id);
} 