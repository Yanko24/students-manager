package com.example.studentsmanager.model.dto.college;

import com.example.studentsmanager.model.dto.BaseQueryDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "学院查询条件DTO")
public class CollegeQueryDTO extends BaseQueryDTO {
    
    @ApiModelProperty(value = "学院代码")
    private String code;
    
    @ApiModelProperty(value = "学院名称")
    private String name;
    
    @ApiModelProperty(value = "院长ID")
    private Long deanId;
    
    @ApiModelProperty(value = "状态（0-正常，1-停用）")
    private Integer status;
} 