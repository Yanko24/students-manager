package com.example.studentsmanager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;
import com.example.studentsmanager.model.vo.student.StudentVO;
import com.example.studentsmanager.model.dto.student.StudentQueryDTO;
import com.example.studentsmanager.model.entity.Student;
import com.example.studentsmanager.model.dto.student.StudentUpdateDTO;

import java.util.List;

public interface StudentService extends IService<Student> {
    /**
     * 分页查询学生列表
     */
    Page<StudentVO> getStudentPage(StudentQueryDTO queryDTO);

    /**
     * 获取所有学生信息
     */
    List<StudentVO> getAllStudentsWithInfo();

    /**
     * 根据ID获取学生信息
     */
    StudentVO getStudentById(Long id);

    /**
     * 更新学生信息
     */
    StudentVO updateStudent(Long id, StudentUpdateDTO updateDTO);

    /**
     * 添加学生
     */
    StudentVO addStudent(StudentUpdateDTO updateDTO);

    /**
     * 删除学生
     */
    boolean deleteStudent(Long id);

    /**
     * 统计班级总人数（包括所有状态）
     * @param majorCode 专业代码
     * @param grade 年级
     * @param classNo 班级号
     * @return 班级总人数
     */
    Integer countClassTotalStudents(String majorCode, String grade, String classNo);

    /**
     * 统计班级在读学生人数
     * @param majorCode 专业代码
     * @param grade 年级
     * @param classNo 班级号
     * @return 在读学生人数
     */
    Integer countClassEnrolledStudents(String majorCode, String grade, String classNo);
}
