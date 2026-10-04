package com.example.studentsmanager.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studentsmanager.constant.ResultMessage;
import com.example.studentsmanager.exception.BusinessException;
import com.example.studentsmanager.mapper.StudentMapper;
import com.example.studentsmanager.model.vo.student.StudentVO;
import com.example.studentsmanager.model.dto.student.StudentQueryDTO;
import com.example.studentsmanager.model.dto.student.StudentUpdateDTO;
import com.example.studentsmanager.model.entity.Student;
import com.example.studentsmanager.model.entity.User;
import com.example.studentsmanager.service.MajorService;
import com.example.studentsmanager.service.StudentService;
import com.example.studentsmanager.service.UserService;
import com.example.studentsmanager.utils.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {
    
    private final UserService userService;
    private final MajorService majorService;

    @Autowired
    private StudentMapper studentMapper;

    public StudentServiceImpl(UserService userService, MajorService majorService) {
        this.userService = userService;
        this.majorService = majorService;
    }

    @Override
    public Page<StudentVO> getStudentPage(StudentQueryDTO queryDTO) {
        log.debug("开始分页查询学生信息，查询条件：{}", queryDTO);
        try {
            Page<Student> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
            Page<StudentVO> result = studentMapper.selectStudentPage(page, queryDTO);
            log.debug("分页查询学生信息完成，总记录数：{}", result.getTotal());
            return result;
        } catch (Exception e) {
            log.error("分页查询学生信息系统异常，查询条件：{}", queryDTO, e);
            throw new BusinessException(ResultMessage.STUDENT_QUERY_FAILED);
        }
    }

    @Override
    public List<StudentVO> getAllStudentsWithInfo() {
        log.debug("开始查询所有学生信息");
        try {
            List<StudentVO> result = studentMapper.selectAllStudentsWithInfo();
            log.debug("查询所有学生信息完成，记录数：{}", result.size());
            return result;
        } catch (Exception e) {
            log.error("查询所有学生信息系统异常", e);
            throw new BusinessException(ResultMessage.STUDENT_QUERY_FAILED);
        }
    }

    @Override
    public StudentVO getStudentById(Long id) {
        log.debug("开始查询学生信息，学生ID：{}", id);
        try {
            StudentVO result = studentMapper.selectByIdWithInfo(id);
            if (result == null) {
                log.warn("查询学生信息失败，学生ID：{}，原因：学生不存在", id);
                throw new BusinessException(ResultMessage.STUDENT_NOT_FOUND);
            }
            log.debug("查询学生信息完成，学生ID：{}", id);
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询学生信息系统异常，学生ID：{}", id, e);
            throw new BusinessException(ResultMessage.STUDENT_QUERY_FAILED);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentVO updateStudent(Long id, StudentUpdateDTO updateDTO) {
        log.info("开始更新学生信息，学生ID：{}，更新内容：{}", id, updateDTO);
        try {
            // 获取现有学生信息
            Student existingStudent = this.getById(id);
            if (existingStudent == null) {
                log.warn("更新学生信息失败，学生ID：{}，原因：学生不存在", id);
                throw new BusinessException(ResultMessage.STUDENT_NOT_FOUND);
            }

            // 更新用户信息
            User user = userService.getById(existingStudent.getUserId());
            if (user != null) {
                user.setRealName(updateDTO.getRealName());
                user.setGender(updateDTO.getGender());
                user.setEmail(updateDTO.getEmail());
                user.setPhone(updateDTO.getPhone());
                user.setUpdateTime(LocalDateTime.now());
                user.setUpdateBy(SecurityUtils.getCurrentUsername());
                userService.updateById(user);
                log.debug("更新用户信息完成，用户ID：{}", user.getId());
            }

            // 更新学生信息
            Student student = new Student();
            student.setId(id);
            student.setStudentNo(updateDTO.getStudentNo());
            student.setMajorCode(updateDTO.getMajorCode());
            student.setGrade(updateDTO.getGrade());
            student.setClassNo(updateDTO.getClassNo());
            student.setAdmissionDate(updateDTO.getAdmissionDate());
            student.setBirthDate(updateDTO.getBirthDate());
            student.setAddress(updateDTO.getAddress());
            student.setStatus(updateDTO.getStatus());
            student.setUpdateTime(LocalDateTime.now());
            student.setUpdateBy(SecurityUtils.getCurrentUsername());
            this.updateById(student);
            log.debug("更新学生信息完成，学生ID：{}", id);

            StudentVO result = convertToVO(student);
            log.info("更新学生信息成功，学生ID：{}", id);
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新学生信息系统异常，学生ID：{}", id, e);
            throw new BusinessException(ResultMessage.STUDENT_UPDATE_FAILED);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentVO addStudent(StudentUpdateDTO updateDTO) {
        log.info("开始添加学生信息，学号：{}", updateDTO.getStudentNo());
        try {
            // 1. 创建用户
            User user = new User();
            user.setUsername(updateDTO.getStudentNo());
            user.setRole("student");
            user.setRealName(updateDTO.getRealName());
            user.setGender(updateDTO.getGender());
            user.setPhone(updateDTO.getPhone());
            user.setEmail(updateDTO.getEmail());
            user.setStatus(0);
            user.setCreateBy(SecurityUtils.getCurrentUsername());
            user.setUpdateBy(SecurityUtils.getCurrentUsername());
            userService.createUser(user);
            log.debug("创建用户完成，用户ID：{}", user.getId());

            // 2. 创建学生信息
            Student student = new Student();
            student.setUserId(user.getId());
            student.setStudentNo(updateDTO.getStudentNo());
            student.setMajorCode(updateDTO.getMajorCode());
            student.setGrade(updateDTO.getGrade());
            student.setClassNo(updateDTO.getClassNo());
            student.setAdmissionDate(updateDTO.getAdmissionDate());
            student.setBirthDate(updateDTO.getBirthDate());
            student.setAddress(updateDTO.getAddress());
            student.setStatus(updateDTO.getStatus());
            student.setCreateBy(SecurityUtils.getCurrentUsername());
            student.setUpdateBy(SecurityUtils.getCurrentUsername());
            this.save(student);
            log.debug("创建学生信息完成，学生ID：{}", student.getId());

            StudentVO result = convertToVO(student);
            log.info("添加学生信息成功，学生ID：{}", student.getId());
            return result;
        } catch (Exception e) {
            log.error("添加学生信息系统异常", e);
            throw new BusinessException(ResultMessage.STUDENT_ADD_FAILED);
        }
    }

    @Override
    @Transactional
    public boolean deleteStudent(Long id) {
        log.info("开始删除学生信息，学生ID：{}", id);
        try {
            // 1. 获取学生信息
            Student student = this.getById(id);
            if (student == null) {
                log.warn("删除学生信息失败，学生ID：{}，原因：学生不存在", id);
                return false;
            }

            // 2. 软删除学生信息
            boolean result = baseMapper.softDeleteStudent(id, SecurityUtils.getCurrentUsername()) > 0;
            if (result) {
                // 3. 软删除并禁用对应的用户账号
                userService.softDeleteUser(student.getUserId(), SecurityUtils.getCurrentUsername());
                log.debug("已软删除并禁用学生对应的用户账号，用户ID：{}", student.getUserId());
                log.info("删除学生信息成功，学生ID：{}", id);
            } else {
                log.warn("删除学生信息失败，学生ID：{}，原因：学生不存在", id);
            }
            return result;
        } catch (Exception e) {
            log.error("删除学生信息系统异常，学生ID：{}", id, e);
            throw new BusinessException(ResultMessage.STUDENT_DELETE_FAILED);
        }
    }

    @Override
    public Integer countClassTotalStudents(String majorCode, String grade, String classNo) {
        log.debug("开始统计班级总人数，专业代码：{}，年级：{}，班级号：{}", majorCode, grade, classNo);
        try {
            Integer result = studentMapper.countClassTotalStudents(majorCode, grade, classNo);
            log.debug("统计班级总人数完成，专业代码：{}，年级：{}，班级号：{}，人数：{}", majorCode, grade, classNo, result);
            return result;
        } catch (Exception e) {
            log.error("统计班级总人数系统异常，专业代码：{}，年级：{}，班级号：{}", majorCode, grade, classNo, e);
            throw new BusinessException(ResultMessage.STUDENT_COUNT_FAILED);
        }
    }

    @Override
    public Integer countClassEnrolledStudents(String majorCode, String grade, String classNo) {
        log.debug("开始统计班级在读学生人数，专业代码：{}，年级：{}，班级号：{}", majorCode, grade, classNo);
        try {
            Integer result = studentMapper.countClassEnrolledStudents(majorCode, grade, classNo);
            log.debug("统计班级在读学生人数完成，专业代码：{}，年级：{}，班级号：{}，人数：{}", majorCode, grade, classNo, result);
            return result;
        } catch (Exception e) {
            log.error("统计班级在读学生人数系统异常，专业代码：{}，年级：{}，班级号：{}", majorCode, grade, classNo, e);
            throw new BusinessException(ResultMessage.STUDENT_COUNT_FAILED);
        }
    }

    private StudentVO convertToVO(Student student) {
        log.debug("开始转换学生信息为VO，学生ID：{}", student.getId());
        try {
            StudentVO vo = new StudentVO();
            BeanUtils.copyProperties(student, vo);
            
            // 从用户表获取真实姓名和性别
            User user = userService.getById(student.getUserId());
            if (user != null) {
                vo.setRealName(user.getRealName());
                vo.setGender(user.getGender());
            }
            
            // 设置状态文字描述
            if (student.getStatus() != null) {
                switch (student.getStatus()) {
                    case 0:
                        vo.setStatusText("在读");
                        break;
                    case 1:
                        vo.setStatusText("休学");
                        break;
                    case 2:
                        vo.setStatusText("退学");
                        break;
                    case 3:
                        vo.setStatusText("毕业");
                        break;
                    default:
                        vo.setStatusText("未知");
                }
            }
            
            log.debug("转换学生信息为VO完成，学生ID：{}", student.getId());
            return vo;
        } catch (Exception e) {
            log.error("转换学生信息为VO系统异常，学生ID：{}", student.getId(), e);
            throw new BusinessException(ResultMessage.STUDENT_CONVERT_FAILED);
        }
    }
} 
