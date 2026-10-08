package com.example.studentsmanager.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("course_schedules")
public class CourseSchedule {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long courseId;
    private Integer dayOfWeek;
    private Integer startPeriod;
    private Integer endPeriod;
    private Integer weekStart;
    private Integer weekEnd;
    private String weekParity;
    private String classroom;
}
