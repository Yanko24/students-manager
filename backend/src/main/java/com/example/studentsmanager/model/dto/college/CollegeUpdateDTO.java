package com.example.studentsmanager.model.dto.college;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@ApiModel(description = "学院更新DTO")
public class CollegeUpdateDTO {
    
    @ApiModelProperty(value = "学院代码", required = true)
    @NotBlank(message = "学院代码不能为空")
    @Size(max = 20, message = "学院代码长度不能超过20个字符")
    private String code;
    
    @ApiModelProperty(value = "学院名称", required = true)
    @NotBlank(message = "学院名称不能为空")
    @Size(max = 50, message = "学院名称长度不能超过50个字符")
    private String name;
    
    @ApiModelProperty(value = "学院描述")
    @Size(max = 500, message = "学院描述长度不能超过500个字符")
    private String description;
    
    @ApiModelProperty(value = "状态（0-正常，1-停用）")
    private Integer status;
}
