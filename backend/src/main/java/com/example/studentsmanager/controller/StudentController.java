package com.example.studentsmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentsmanager.constant.ResultCode;
import com.example.studentsmanager.core.response.Result;
import com.example.studentsmanager.exception.BusinessException;
import com.example.studentsmanager.model.vo.student.StudentVO;
import com.example.studentsmanager.model.dto.student.StudentQueryDTO;
import com.example.studentsmanager.model.dto.student.StudentUpdateDTO;
import com.example.studentsmanager.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@Tag(name = "学生管理")
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Operation(summary = "分页查询学生信息", description = "根据查询条件分页获取学生信息列表")
    @GetMapping("")
    public Result<Page<StudentVO>> getStudentPage(StudentQueryDTO queryDTO) {
        log.info("收到分页查询学生信息请求");
        try {
            Page<StudentVO> result = studentService.getStudentPage(queryDTO);
            log.info("分页查询学生信息成功，总记录数：{}", result.getTotal());
            return Result.success(result);
        } catch (BusinessException e) {
            log.warn("分页查询学生信息失败，错误信息：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("分页查询学生信息系统异常", e);
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "系统异常");
        }
    }

    @Operation(summary = "获取所有学生信息", description = "获取所有学生的详细信息列表")
    @GetMapping("/list")
    public Result<List<StudentVO>> getAllStudentsWithInfo() {
        log.info("收到获取所有学生信息请求");
        try {
            List<StudentVO> result = studentService.getAllStudentsWithInfo();
            log.info("获取所有学生信息成功，记录数：{}", result.size());
            return Result.success(result);
        } catch (BusinessException e) {
            log.warn("获取所有学生信息失败，错误信息：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("获取所有学生信息系统异常", e);
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "系统异常");
        }
    }

    @Operation(summary = "获取学生详情", description = "根据学生ID获取学生的详细信息")
    @GetMapping("/{id}")
    public Result<StudentVO> getStudentById(@PathVariable Long id) {
        log.info("收到获取学生信息请求，ID：{}", id);
        try {
            StudentVO result = studentService.getStudentById(id);
            if (result == null) {
                log.warn("学生不存在，ID：{}", id);
                throw new BusinessException(ResultCode.NOT_FOUND, "学生不存在");
            }
            log.info("获取学生信息成功，ID：{}", id);
            return Result.success(result);
        } catch (BusinessException e) {
            log.warn("获取学生信息失败，ID：{}，错误信息：{}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("获取学生信息系统异常，ID：{}", id, e);
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "系统异常");
        }
    }

    @Operation(summary = "更新学生信息", description = "根据学生ID更新学生的详细信息")
    @PutMapping("/{id}")
    public Result<StudentVO> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentUpdateDTO updateDTO) {
        log.info("收到更新学生信息请求，ID：{}", id);
        try {
            StudentVO result = studentService.updateStudent(id, updateDTO);
            if (result == null) {
                log.warn("更新学生信息失败，ID：{}", id);
                throw new BusinessException(ResultCode.ERROR, "更新学生信息失败");
            }
            log.info("更新学生信息成功，ID：{}", id);
            return Result.success(result);
        } catch (BusinessException e) {
            log.warn("更新学生信息失败，ID：{}，错误信息：{}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("更新学生信息系统异常，ID：{}", id, e);
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "系统异常");
        }
    }

    @Operation(summary = "添加学生", description = "添加新的学生信息")
    @PostMapping
    public Result<StudentVO> addStudent(@Valid @RequestBody StudentUpdateDTO updateDTO) {
        log.info("收到添加学生请求");
        try {
            StudentVO result = studentService.addStudent(updateDTO);
            if (result == null) {
                log.warn("添加学生失败");
                throw new BusinessException(ResultCode.ERROR, "添加学生失败");
            }
            log.info("添加学生成功");
            return Result.success(result);
        } catch (BusinessException e) {
            log.warn("添加学生失败，错误信息：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("添加学生系统异常", e);
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "系统异常");
        }
    }

    @Operation(summary = "删除学生", description = "根据学生ID删除学生信息")
    @DeleteMapping("/{id}")
    public Result<Void> deleteStudent(@PathVariable Long id) {
        log.info("收到删除学生请求，ID：{}", id);
        try {
            boolean result = studentService.deleteStudent(id);
            if (!result) {
                log.warn("删除学生失败，ID：{}", id);
                throw new BusinessException(ResultCode.ERROR, "删除学生失败");
            }
            log.info("删除学生成功，ID：{}", id);
            return Result.success();
        } catch (BusinessException e) {
            log.warn("删除学生失败，ID：{}，错误信息：{}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("删除学生系统异常，ID：{}", id, e);
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "系统异常");
        }
    }

    @Operation(summary = "统计班级总人数", description = "统计指定班级的总人数（包括所有状态）")
    @GetMapping("/count/total")
    public Result<Integer> countClassTotalStudents(
            @RequestParam String majorCode,
            @RequestParam String grade,
            @RequestParam String classNo) {
        log.info("收到统计班级总人数请求，专业代码：{}，年级：{}，班级号：{}", majorCode, grade, classNo);
        try {
            Integer result = studentService.countClassTotalStudents(majorCode, grade, classNo);
            log.info("统计班级总人数请求处理成功，专业代码：{}，年级：{}，班级号：{}，人数：{}", majorCode, grade, classNo, result);
            return Result.success(result);
        } catch (BusinessException e) {
            log.warn("统计班级总人数失败，专业代码：{}，年级：{}，班级号：{}，错误信息：{}", majorCode, grade, classNo, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("统计班级总人数系统异常，专业代码：{}，年级：{}，班级号：{}", majorCode, grade, classNo, e);
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "系统异常");
        }
    }

    @Operation(summary = "统计班级在读学生人数", description = "统计指定班级的在读学生人数")
    @GetMapping("/count/enrolled")
    public Result<Integer> countClassEnrolledStudents(
            @RequestParam String majorCode,
            @RequestParam String grade,
            @RequestParam String classNo) {
        log.info("收到统计班级在读学生人数请求，专业代码：{}，年级：{}，班级号：{}", majorCode, grade, classNo);
        try {
            Integer result = studentService.countClassEnrolledStudents(majorCode, grade, classNo);
            log.info("统计班级在读学生人数请求处理成功，专业代码：{}，年级：{}，班级号：{}，人数：{}", majorCode, grade, classNo, result);
            return Result.success(result);
        } catch (BusinessException e) {
            log.warn("统计班级在读学生人数失败，专业代码：{}，年级：{}，班级号：{}，错误信息：{}", majorCode, grade, classNo, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("统计班级在读学生人数系统异常，专业代码：{}，年级：{}，班级号：{}", majorCode, grade, classNo, e);
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "系统异常");
        }
    }
}
