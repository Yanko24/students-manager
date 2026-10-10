package com.example.studentsmanager.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import com.example.studentsmanager.security.handler.Sm4StringTypeHandler;

@Data
@TableName(value = "users", autoResultMap = true)
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String role;

    @TableField("real_name")
    private String realName;

    private Integer gender;

    @TableField(typeHandler = Sm4StringTypeHandler.class)
    private String phone;

    @TableField(typeHandler = Sm4StringTypeHandler.class)
    private String email;

    private Integer status;

    @TableField("must_change_password")
    private Boolean mustChangePassword;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField("create_by")
    private String createBy;

    @TableField("update_by")
    private String updateBy;

    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted;
} 
