package com.example.studentsmanager.model.vo.student;

import com.example.studentsmanager.model.vo.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 学生信息VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "学生信息VO")
public class StudentVO extends BaseVO {
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "学号")
    private String studentNo;
    
    @Schema(description = "真实姓名")
    private String realName;
    
    @Schema(description = "性别：1-男，0-女")
    private Integer gender;
    
    @Schema(description = "出生日期")
    private LocalDate birthDate;
    
    @Schema(description = "入学日期")
    private LocalDate admissionDate;
    
    @Schema(description = "联系电话")
    private String phone;
    
    @Schema(description = "电子邮箱")
    private String email;
    
    @Schema(description = "家庭住址")
    private String address;
    
    @Schema(description = "专业代码")
    private String majorCode;
    
    @Schema(description = "年级")
    private String grade;
    
    @Schema(description = "班级号")
    private String classNo;
    
    @Schema(description = "状态：0-在读，1-休学，2-退学，3-毕业")
    private Integer status;
    
    @Schema(description = "状态文字描述：0-在读，1-休学，2-退学，3-毕业")
    private String statusText;
    
    @Schema(description = "专业名称")
    private String majorName;
    
    @Schema(description = "学院名称")
    private String collegeName;

    public String getDisplayName() {
        String displayGrade = grade != null && grade.length() > 2 ? grade.substring(grade.length() - 2) : grade;
        return String.format("%s%s专业%s%s班",
            collegeName, majorName, displayGrade, classNo);
    }
} 