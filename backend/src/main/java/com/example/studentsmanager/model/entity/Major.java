package com.example.studentsmanager.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("majors")
public class Major {

    @TableId
    private String code;

    private String grade;

    @TableField("class_no")
    private String classNo;

    private String name;

    @TableField("college_id")
    private Long collegeId;

    @TableField("head_teacher_id")
    private Long headTeacherId;

    private Integer status;          // 状态：0-正常，1-停招，2-撤销

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("create_by")
    private String createBy;

    @TableField("update_by")
    private String updateBy;

    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted;

    @TableField(exist = false)
    private Teacher headTeacher;

    @TableField(exist = false)
    private College college;
}
 