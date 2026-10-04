package com.example.studentsmanager.controller;

import com.example.studentsmanager.config.web.FileUploadConfig;
import com.example.studentsmanager.core.response.Result;
import com.example.studentsmanager.exception.BusinessException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Api(tags = "文件管理")
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileUploadConfig fileUploadConfig;

    @ApiOperation(value = "文件上传", notes = "上传文件到服务器，支持文件类型验证和大小限制")
    @ApiParam(name = "file", value = "要上传的文件", required = true)
    @ApiResponses({
        @ApiResponse(code = 200, message = "文件上传成功", response = String.class),
        @ApiResponse(code = 400, message = "文件为空或类型不支持"),
        @ApiResponse(code = 500, message = "文件上传失败")
    })
    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !fileUploadConfig.getAllowedTypes().contains(contentType)) {
            throw new BusinessException("不支持的文件类型");
        }

        // 检查文件大小
        if (file.getSize() > parseSize(fileUploadConfig.getMaxSize())) {
            throw new BusinessException("文件大小超过限制");
        }

        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + extension;

        // 创建上传目录
        File uploadDir = new File(fileUploadConfig.getPath());
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 保存文件
        try {
            File dest = new File(uploadDir, fileName);
            file.transferTo(dest);
            return Result.success(fileName);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败");
        }
    }

    private long parseSize(String size) {
        size = size.toUpperCase();
        if (size.endsWith("KB")) {
            return Long.parseLong(size.substring(0, size.length() - 2)) * 1024;
        } else if (size.endsWith("MB")) {
            return Long.parseLong(size.substring(0, size.length() - 2)) * 1024 * 1024;
        } else if (size.endsWith("GB")) {
            return Long.parseLong(size.substring(0, size.length() - 2)) * 1024 * 1024 * 1024;
        } else {
            return Long.parseLong(size);
        }
    }
} 