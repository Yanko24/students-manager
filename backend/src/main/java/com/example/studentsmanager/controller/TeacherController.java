package com.example.studentsmanager.controller;

import com.example.studentsmanager.constant.ResultCode;
import com.example.studentsmanager.constant.ResultMessage;
import com.example.studentsmanager.core.response.Result;
import com.example.studentsmanager.model.dto.teacher.TeacherQueryDTO;
import com.example.studentsmanager.model.entity.Teacher;
import com.example.studentsmanager.model.vo.teacher.TeacherListVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentsmanager.service.TeacherService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;


@Slf4j
@RestController
@RequestMapping("/api/teachers")
@CrossOrigin(origins = "http://localhost:3000")
@Api(tags = "教师管理")
@RequiredArgsConstructor
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @GetMapping
    @ApiOperation(value = "获取所有教师", notes = "返回系统中所有教师的信息列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "成功获取教师列表")
    })
    public Result<Page<TeacherListVO>> getAllTeachers(TeacherQueryDTO queryDTO) {
        log.info("分页查询教师列表，查询条件：{}", queryDTO);
        try {
            Page<TeacherListVO> teachers = teacherService.getTeacherPage(queryDTO);
            log.info("成功获取教师列表，共{}条记录", teachers.getTotal());
            return Result.success(teachers);
        } catch (Exception e) {
            log.error("获取教师列表失败", e);
            return Result.error(ResultMessage.ERROR);
        }
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "获取教师详情", notes = "根据ID获取指定教师的详细信息")
    @ApiImplicitParam(name = "id", value = "教师ID", required = true, type = "integer", paramType = "path", example = "1", dataTypeClass = Long.class)
    @ApiResponses({
        @ApiResponse(code = 200, message = "成功获取教师信息"),
        @ApiResponse(code = 404, message = "教师不存在")
    })
    public Result<Teacher> getTeacherById(@PathVariable Long id) {
        log.info("获取教师详情，ID: {}", id);
        try {
            Teacher teacher = teacherService.getById(id);
            if (teacher == null) {
                log.warn("教师不存在，ID: {}", id);
                return Result.error(ResultCode.NOT_FOUND, ResultMessage.RECORD_NOT_FOUND);
            }
            log.info("成功获取教师详情，ID: {}", id);
            return Result.success(teacher);
        } catch (Exception e) {
            log.error("获取教师详情失败，ID: {}", id, e);
            return Result.error(ResultMessage.ERROR);
        }
    }

    @PostMapping
    @ApiOperation(value = "创建教师", notes = "创建新的教师信息")
    @ApiImplicitParam(name = "teacher", value = "教师信息", required = true, type = "object", dataTypeClass = Teacher.class)
    @ApiResponses({
        @ApiResponse(code = 200, message = "教师创建成功"),
        @ApiResponse(code = 400, message = "教师创建失败")
    })
    public Result<Teacher> createTeacher(@RequestBody Teacher teacher) {
        log.info("创建教师，工号: {}", teacher.getTeacherNumber());
        try {
            Teacher createdTeacher = teacherService.createTeacher(teacher);
            log.info("教师创建成功，ID: {}", createdTeacher.getId());
            return Result.success(createdTeacher);
        } catch (Exception e) {
            log.error("教师创建失败，工号: {}", teacher.getTeacherNumber(), e);
            return Result.error(ResultMessage.ERROR);
        }
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "更新教师", notes = "更新指定教师的信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "教师ID", required = true, type = "integer", paramType = "path", example = "1", dataTypeClass = Long.class),
        @ApiImplicitParam(name = "teacher", value = "教师信息", required = true, type = "object", dataTypeClass = Teacher.class)
    })
    @ApiResponses({
        @ApiResponse(code = 200, message = "教师更新成功"),
        @ApiResponse(code = 404, message = "教师不存在")
    })
    public Result<Boolean> updateTeacher(@PathVariable Long id, @RequestBody Teacher teacher) {
        log.info("更新教师，ID: {}", id);
        try {
            teacher.setId(id);
            boolean updated = teacherService.updateTeacher(teacher);
            if (updated) {
                log.info("教师更新成功，ID: {}", id);
                return Result.success(true);
            }
            log.warn("教师不存在，ID: {}", id);
            return Result.error(ResultCode.NOT_FOUND, ResultMessage.RECORD_NOT_FOUND);
        } catch (Exception e) {
            log.error("教师更新失败，ID: {}", id, e);
            return Result.error(ResultMessage.ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除教师", notes = "删除指定的教师信息")
    @ApiImplicitParam(name = "id", value = "教师ID", required = true, type = "integer", paramType = "path", example = "1", dataTypeClass = Long.class)
    @ApiResponses({
        @ApiResponse(code = 200, message = "教师删除成功"),
        @ApiResponse(code = 404, message = "教师不存在")
    })
    public Result<Boolean> deleteTeacher(@PathVariable Long id) {
        log.info("删除教师，ID: {}", id);
        try {
            boolean deleted = teacherService.deleteTeacher(id);
            if (deleted) {
                log.info("教师删除成功，ID: {}", id);
                return Result.success(true);
            }
            log.warn("教师不存在，ID: {}", id);
            return Result.error(ResultCode.NOT_FOUND, ResultMessage.RECORD_NOT_FOUND);
        } catch (Exception e) {
            log.error("教师删除失败，ID: {}", id, e);
            return Result.error(ResultMessage.ERROR);
        }
    }
}
