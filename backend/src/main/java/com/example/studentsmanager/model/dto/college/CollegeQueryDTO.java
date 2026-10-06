package com.example.studentsmanager.model.dto.college;

import io.swagger.v3.oas.annotations.media.Schema;

import com.example.studentsmanager.model.dto.BaseQueryDTO;
import lombok.Data;

@Data
@Schema(description = "学院查询条件DTO")
public class CollegeQueryDTO extends BaseQueryDTO {

    @Schema(description = "学院代码")
    private String code;

    @Schema(description = "学院名称")
    private String name;

    @Schema(description = "状态（0-正常，1-停用）")
    private Integer status;
}
