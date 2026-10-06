package com.example.studentsmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.studentsmanager.core.response.Result;
import com.example.studentsmanager.model.dto.college.CollegeQueryDTO;
import com.example.studentsmanager.model.dto.college.CollegeUpdateDTO;
import com.example.studentsmanager.model.vo.college.CollegeVO;
import com.example.studentsmanager.service.CollegeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "学院管理")
@RestController
@RequestMapping("/api/colleges")
@RequiredArgsConstructor
public class CollegeController {

    private final CollegeService collegeService;

    @GetMapping
    @Operation(summary = "分页查询学院信息", description = "根据查询条件分页获取学院信息列表")
    public Result<IPage<CollegeVO>> getCollegePage(CollegeQueryDTO queryDTO) {
        log.info("分页查询学院信息，查询条件：{}", queryDTO);
        try {
            IPage<CollegeVO> page = collegeService.getCollegePage(queryDTO);
            return Result.success(page);
        } catch (Exception e) {
            log.error("分页查询学院信息失败", e);
            return Result.error("分页查询学院信息失败");
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取学院详情", description = "根据ID获取指定学院的详细信息")
    public Result<CollegeVO> getCollegeById(@PathVariable Long id) {
        log.info("根据ID查询学院信息，ID：{}", id);
        try {
            CollegeVO college = collegeService.getCollegeById(id);
            return Result.success(college);
        } catch (Exception e) {
            log.error("查询学院信息失败", e);
            return Result.error("查询学院信息失败");
        }
    }

    @PostMapping
    @Operation(summary = "创建学院", description = "创建新的学院信息")
    public Result<CollegeVO> createCollege(@RequestBody CollegeUpdateDTO updateDTO) {
        log.info("创建学院，学院信息：{}", updateDTO);
        try {
            CollegeVO college = collegeService.createCollege(updateDTO);
            return Result.success(college);
        } catch (Exception e) {
            log.error("创建学院失败", e);
            return Result.error("创建学院失败");
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新学院", description = "更新指定学院的信息")
    public Result<CollegeVO> updateCollege(@PathVariable Long id, @RequestBody CollegeUpdateDTO updateDTO) {
        log.info("更新学院信息，ID：{}，更新信息：{}", id, updateDTO);
        try {
            CollegeVO college = collegeService.updateCollege(id, updateDTO);
            return Result.success(college);
        } catch (Exception e) {
            log.error("更新学院信息失败", e);
            return Result.error("更新学院信息失败");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除学院", description = "删除指定的学院信息")
    public Result<Void> deleteCollege(@PathVariable Long id) {
        log.info("删除学院，ID：{}", id);
        try {
            collegeService.deleteCollege(id);
            return Result.success();
        } catch (Exception e) {
            log.error("删除学院失败", e);
            return Result.error("删除学院失败");
        }
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有学院列表", description = "获取系统中所有学院的列表")
    public Result<List<CollegeVO>> getAllColleges() {
        log.info("获取所有学院列表");
        try {
            List<CollegeVO> colleges = collegeService.getAllColleges();
            return Result.success(colleges);
        } catch (Exception e) {
            log.error("获取学院列表失败", e);
            return Result.error("获取学院列表失败");
        }
    }
}
