package com.example.studentsmanager.model.dto.college;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Schema(description = "学院更新DTO")
public class CollegeUpdateDTO {

    @Schema(description = "学院代码", required = true)
    @NotBlank(message = "学院代码不能为空")
    @Size(max = 20, message = "学院代码长度不能超过20个字符")
    private String code;

    @Schema(description = "学院名称", required = true)
    @NotBlank(message = "学院名称不能为空")
    @Size(max = 50, message = "学院名称长度不能超过50个字符")
    private String name;

    @Schema(description = "学院描述")
    @Size(max = 500, message = "学院描述长度不能超过500个字符")
    private String description;

    @Schema(description = "状态（0-正常，1-停用）")
    private Integer status;
}
