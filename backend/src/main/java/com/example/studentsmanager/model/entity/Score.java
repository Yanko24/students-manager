package com.example.studentsmanager.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("scores")
public class Score {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("student_id")
    private Long studentId;
    @TableField("course_id")
    private Long courseId;
    private BigDecimal score;
    private String grade;
    @TableField("grade_point")
    private BigDecimal gradePoint;
    private String semester;
    @TableField("attempt_type")
    private String attemptType;
    @TableField("attempt_no")
    private Integer attemptNo;
    @TableField("publish_status")
    private String publishStatus;
    @TableField("exam_time")
    private LocalDateTime examTime;
    @TableField("remarks")
    private String comment;
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
