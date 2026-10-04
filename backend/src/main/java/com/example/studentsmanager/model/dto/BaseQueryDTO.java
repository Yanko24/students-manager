package com.example.studentsmanager.model.dto;

import lombok.Data;

@Data
public class BaseQueryDTO {
    private Integer page = 1;        // 当前页码
    private Integer size = 10;       // 每页条数
} 