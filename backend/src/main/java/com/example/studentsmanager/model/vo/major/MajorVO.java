package com.example.studentsmanager.model.vo.major;

import com.example.studentsmanager.model.vo.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 专业信息VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "专业信息VO")
public class MajorVO extends BaseVO {
    
    @Schema(description = "专业代码")
    private String code;
    
    @Schema(description = "年级")
    private String grade;
    
    @Schema(description = "班级号")
    private String classNo;
    
    @Schema(description = "专业名称")
    private String name;
    
    @Schema(description = "所属学院ID")
    private Long collegeId;
    
    @Schema(description = "所属学院名称")
    private String collegeName;
    
    @Schema(description = "班主任ID")
    private Long headTeacherId;
    
    @Schema(description = "班主任工号")
    private String headTeacherNumber;
    
    @Schema(description = "班主任姓名")
    private String headTeacherName;
    
    @Schema(description = "专业状态")
    private Integer status;
    
    @Schema(description = "状态文字描述：0-正常，1-停招，2-撤销")
    private String statusText;
    
    @Schema(description = "学生人数")
    private Integer studentCount;

    public String getDisplayName() {
        String displayGrade = grade != null && grade.length() > 2 ? grade.substring(grade.length() - 2) : grade;
        return String.format("%s%s专业%s%s班",
            collegeName, name, displayGrade, classNo);
    }
} 