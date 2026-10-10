package com.example.studentsmanager.model.vo.course;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CourseVO {
    private Long id;
    private Long catalogId;
    private String code;
    private String sectionCode;
    private String name;
    private Long teacherId;
    private String teacher;
    private String college;
    private BigDecimal credit;
    private Integer hours;
    private String type;
    private String semester;
    private Integer status;
    private Integer selectionOpen;
    private Integer maxStudents;
    private Integer selectedCount;
    private String selectionScope;
    private Long selectionCollegeId;
    private String selectionCollegeName;
    private String selectionMajorCode;
    private String selectionMajorName;
    private String selectionGrade;
    private LocalDateTime selectionStartAt;
    private LocalDateTime selectionEndAt;
    private LocalDateTime dropDeadlineAt;
    private List<String> prerequisiteCourseCodes;
    private List<String> missingPrerequisiteCourseCodes;
    private BigDecimal semesterSelectedCredits;
    private BigDecimal semesterCreditLimit;
    private String statusText;
    private String description;
    private String objectives;
    private String selectionStatus;
    private LocalDateTime selectionDate;
    private Long waitlistPosition;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<CourseScheduleVO> schedules;
}
