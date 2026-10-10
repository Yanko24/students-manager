package com.example.studentsmanager.model.vo.curriculum;

import com.example.studentsmanager.model.entity.CurriculumPlan;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CurriculumPlanVO extends CurriculumPlan {
    private List<Long> requiredCourseCatalogIds;
}
