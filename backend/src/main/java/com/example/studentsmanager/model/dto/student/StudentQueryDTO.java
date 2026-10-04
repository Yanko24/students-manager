package com.example.studentsmanager.model.dto.student;

import com.example.studentsmanager.model.dto.BaseQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class StudentQueryDTO extends BaseQueryDTO {
    private String studentNo;       // 学号
    private String realName;        // 姓名
    private String majorCode;       // 专业代码
    private String grade;           // 年级
    private String classNo;         // 班级号
    private Integer status;         // 状态：0-在读，1-休学，2-退学，3-毕业
} 