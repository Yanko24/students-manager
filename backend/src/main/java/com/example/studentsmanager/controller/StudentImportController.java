package com.example.studentsmanager.controller;

import com.example.studentsmanager.core.response.Result;
import com.example.studentsmanager.service.StudentImportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/students")
@RequiredArgsConstructor
@Api(tags = "学生管理")
public class StudentImportController {
    private final StudentImportService studentImportService;

    @PostMapping("/import")
    @ApiOperation(value = "批量导入学生", notes = "通过CSV文件批量导入学生信息")
    public Result<Map<String, Integer>> importStudents(@RequestParam("file") MultipartFile file) {
        int imported = studentImportService.importCsv(file);
        return Result.success(Collections.singletonMap("imported", imported));
    }
}
