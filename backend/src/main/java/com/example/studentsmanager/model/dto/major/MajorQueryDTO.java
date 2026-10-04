package com.example.studentsmanager.model.dto.major;

import com.example.studentsmanager.model.dto.BaseQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MajorQueryDTO extends BaseQueryDTO {
    private String code;           // 专业代码
    private String name;           // 专业名称
    private String grade;          // 年级
    private String classNo;        // 班级号
    private Long collegeId;        // 学院ID
    private Integer status;        // 状态：0-正常，1-停招，2-撤销
} 