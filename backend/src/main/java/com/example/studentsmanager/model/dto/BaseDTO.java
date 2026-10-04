package com.example.studentsmanager.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BaseDTO {
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 是否删除（0：未删除，1：已删除）
     */
    private Integer isDeleted;
} 