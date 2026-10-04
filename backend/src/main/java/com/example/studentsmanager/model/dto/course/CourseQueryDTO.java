package com.example.studentsmanager.model.dto.course;

import com.example.studentsmanager.model.dto.BaseQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CourseQueryDTO extends BaseQueryDTO {
    private String code;
    private String name;
    private String department;
    private String semester;
}
