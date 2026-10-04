package com.example.studentsmanager.model.dto.teacher;

import com.example.studentsmanager.model.dto.BaseQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TeacherQueryDTO extends BaseQueryDTO {
    private String teacherNo;
    private String realName;
    private String department;
}
