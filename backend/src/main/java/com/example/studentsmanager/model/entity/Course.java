package com.example.studentsmanager.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("courses")
public class Course {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("catalog_id")
    private Long catalogId;
    @TableField("section_code")
    private String sectionCode;
    @TableField("course_name")
    private String courseName;
    @TableField("course_code")
    private String courseCode;
    @TableField("teacher_id")
    private Long teacherId;
    private BigDecimal credits;
    @TableField("course_type")
    private String courseType;
    private String semester;
    private Integer hours;
    private Integer status;
    @TableField("selection_open")
    private Integer selectionOpen;
    @TableField("max_students")
    private Integer maxStudents;
    @TableField("selection_scope")
    private String selectionScope;
    @TableField("selection_college_id")
    private Long selectionCollegeId;
    @TableField("selection_major_code")
    private String selectionMajorCode;
    @TableField("selection_grade")
    private String selectionGrade;
    @TableField("selection_start_at")
    private LocalDateTime selectionStartAt;
    @TableField("selection_end_at")
    private LocalDateTime selectionEndAt;
    @TableField("drop_deadline_at")
    private LocalDateTime dropDeadlineAt;
    private String description;
    private String objectives;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableField(fill = FieldFill.INSERT)
    private String createBy;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;
    @TableLogic
    private Integer isDeleted;
}
