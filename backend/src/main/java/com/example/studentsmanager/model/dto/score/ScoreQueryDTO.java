package com.example.studentsmanager.model.dto.score;

import com.example.studentsmanager.model.dto.BaseQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ScoreQueryDTO extends BaseQueryDTO {
    private String studentNo;
    private String studentName;
    private String courseName;
    private String semester;
}
