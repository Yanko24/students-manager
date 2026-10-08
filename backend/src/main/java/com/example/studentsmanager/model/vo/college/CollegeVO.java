package com.example.studentsmanager.model.vo.college;

import com.example.studentsmanager.model.vo.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学院信息VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "学院信息VO")
public class CollegeVO extends BaseVO {
    
    @Schema(description = "学院代码")
    private String code;
    
    @Schema(description = "学院名称")
    private String name;
    
    @Schema(description = "学院描述")
    private String description;
    
    @Schema(description = "学院状态：0-正常，1-停用")
    private Integer status;
    
    @Schema(description = "状态文字描述")
    private String statusText;
}
