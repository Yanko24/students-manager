package com.example.studentsmanager.controller;

import com.example.studentsmanager.core.response.Result;
import com.example.studentsmanager.constant.ResultCode;
import com.example.studentsmanager.exception.BusinessException;
import com.example.studentsmanager.model.dto.major.MajorQueryDTO;
import com.example.studentsmanager.model.dto.major.MajorUpdateDTO;
import com.example.studentsmanager.model.vo.major.MajorVO;
import com.example.studentsmanager.core.response.PageResult;
import com.example.studentsmanager.service.MajorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@Api(tags = "专业管理")
@RestController
@RequestMapping("/api/majors")
@RequiredArgsConstructor
public class MajorController {
    @Resource
    private MajorService majorService;

    @ApiOperation(value = "分页查询专业列表", notes = "根据查询条件分页获取专业信息列表")
    @GetMapping
    public Result<PageResult<MajorVO>> getMajorPage(MajorQueryDTO queryDTO) {
        log.info("收到分页查询专业信息请求，查询条件：{}", queryDTO);
        try {
            PageResult<MajorVO> result = majorService.getMajorPage(queryDTO);
            log.info("分页查询专业信息成功，总记录数：{}", result.getTotal());
            return Result.success(result);
        } catch (Exception e) {
            log.error("分页查询专业信息失败，查询条件：{}，错误信息：{}", queryDTO, e.getMessage(), e);
            throw e; // 让GlobalExceptionHandler处理异常
        }
    }

    @ApiOperation(value = "获取专业详情", notes = "根据专业代码、年级和班级号获取专业的详细信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "code", value = "专业代码", required = true, type = "string", paramType = "path", dataTypeClass = String.class),
        @ApiImplicitParam(name = "grade", value = "年级", required = true, type = "string", paramType = "path", dataTypeClass = String.class),
        @ApiImplicitParam(name = "classNo", value = "班级号", required = true, type = "string", paramType = "path", dataTypeClass = String.class)
    })
    @GetMapping("/{code}/{grade}/{classNo}")
    public Result<MajorVO> getMajorById(@PathVariable String code,
                                       @PathVariable String grade,
                                       @PathVariable String classNo) {
        log.info("收到获取专业信息请求，专业代码：{}，年级：{}，班级号：{}", code, grade, classNo);
        try {
            MajorVO result = majorService.getMajorById(code, grade, classNo);
            if (result == null) {
                log.warn("专业不存在，专业代码：{}，年级：{}，班级号：{}", code, grade, classNo);
                throw new BusinessException(ResultCode.NOT_FOUND, "专业不存在");
            }
            log.info("获取专业信息成功，专业代码：{}，年级：{}，班级号：{}", code, grade, classNo);
            return Result.success(result);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取专业信息失败，专业代码：{}，年级：{}，班级号：{}，错误信息：{}", code, grade, classNo, e.getMessage(), e);
            throw e; // 让GlobalExceptionHandler处理异常
        }
    }

    @ApiOperation(value = "创建专业", notes = "创建新的专业信息")
    @PostMapping
    public Result<MajorVO> createMajor(@Valid @RequestBody MajorUpdateDTO updateDTO) {
        log.info("收到创建专业请求，专业信息：{}", updateDTO);
        try {
            MajorVO result = majorService.createMajor(updateDTO);
            if (result == null) {
                log.warn("创建专业失败");
                throw new BusinessException(ResultCode.ERROR, "创建专业失败");
            }
            log.info("创建专业成功，专业代码：{}，年级：{}，班级号：{}", 
                result.getCode(), result.getGrade(), result.getClassNo());
            return Result.success(result);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建专业失败，错误信息：{}", e.getMessage(), e);
            throw e; // 让GlobalExceptionHandler处理异常
        }
    }

    @ApiOperation(value = "更新专业", notes = "更新指定专业的信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "code", value = "专业代码", required = true, type = "string", paramType = "path", dataTypeClass = String.class),
        @ApiImplicitParam(name = "grade", value = "年级", required = true, type = "string", paramType = "path", dataTypeClass = String.class),
        @ApiImplicitParam(name = "classNo", value = "班级号", required = true, type = "string", paramType = "path", dataTypeClass = String.class)
    })
    @PutMapping("/{code}/{grade}/{classNo}")
    public Result<MajorVO> updateMajor(@PathVariable String code,
                                      @PathVariable String grade,
                                      @PathVariable String classNo,
                                      @Valid @RequestBody MajorUpdateDTO updateDTO) {
        log.info("收到更新专业请求，专业代码：{}，年级：{}，班级号：{}，更新内容：{}", 
            code, grade, classNo, updateDTO);
        try {
            MajorVO result = majorService.updateMajor(code, grade, classNo, updateDTO);
            if (result == null) {
                log.warn("更新专业失败，专业代码：{}，年级：{}，班级号：{}", code, grade, classNo);
                throw new BusinessException(ResultCode.ERROR, "更新专业失败");
            }
            log.info("更新专业成功，专业代码：{}，年级：{}，班级号：{}", code, grade, classNo);
            return Result.success(result);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新专业失败，专业代码：{}，年级：{}，班级号：{}，错误信息：{}", 
                code, grade, classNo, e.getMessage(), e);
            throw e; // 让GlobalExceptionHandler处理异常
        }
    }

    @ApiOperation(value = "删除专业", notes = "删除指定的专业信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "code", value = "专业代码", required = true, type = "string", paramType = "path", dataTypeClass = String.class),
        @ApiImplicitParam(name = "grade", value = "年级", required = true, type = "string", paramType = "path", dataTypeClass = String.class),
        @ApiImplicitParam(name = "classNo", value = "班级号", required = true, type = "string", paramType = "path", dataTypeClass = String.class)
    })
    @DeleteMapping("/{code}/{grade}/{classNo}")
    public Result<Void> deleteMajor(@PathVariable String code,
                                  @PathVariable String grade,
                                  @PathVariable String classNo) {
        log.info("收到删除专业请求，专业代码：{}，年级：{}，班级号：{}", code, grade, classNo);
        try {
            majorService.deleteMajor(code, grade, classNo);
            log.info("删除专业成功，专业代码：{}，年级：{}，班级号：{}", code, grade, classNo);
            return Result.success();
        } catch (Exception e) {
            log.error("删除专业失败，专业代码：{}，年级：{}，班级号：{}，错误信息：{}", 
                code, grade, classNo, e.getMessage(), e);
            throw e; // 让GlobalExceptionHandler处理异常
        }
    }

    @ApiOperation(value = "获取所有专业列表", notes = "获取系统中所有专业的列表")
    @GetMapping("/list")
    public Result<PageResult<MajorVO>> listAllMajors() {
        List<MajorVO> majors = majorService.getAllMajors();
        return Result.success(new PageResult<>(majors, (long) majors.size(), 1, majors.size()));
    }
} 