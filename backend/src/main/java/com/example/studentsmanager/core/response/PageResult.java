package com.example.studentsmanager.core.response;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    private List<T> records;    // 数据列表
    private Long total;         // 总记录数
    private Integer size;       // 每页大小
    private Integer current;    // 当前页码
    private Integer pages;      // 总页数

    public PageResult(List<T> records, Long total) {
        this(records, total, 10, 1);  // 默认每页10条，第1页
    }

    public PageResult(List<T> records, Long total, Integer size, Integer current) {
        this.records = records;
        this.total = total;
        this.size = size;
        this.current = current;
        this.pages = (int) Math.ceil((double) total / size);
    }
} 