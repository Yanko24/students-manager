package com.example.studentsmanager.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("colleges")
public class College {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 学院代码
     */
    private String code;
    
    /**
     * 学院名称
     */
    private String name;
    
    /**
     * 学院简介
     */
    private String description;
    
    /**
     * 院长ID
     */
    private Long deanId;
    
    /**
     * 状态（0-正常，1-停用）
     */
    private Integer status;
    
    /**
     * 是否删除（0-未删除，1-已删除）
     */
    @TableLogic
    private Integer isDeleted;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 创建人
     */
    private String createBy;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 更新人
     */
    private String updateBy;
} 