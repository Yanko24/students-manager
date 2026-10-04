package com.example.studentsmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentsmanager.model.entity.College;
import com.example.studentsmanager.model.vo.college.CollegeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CollegeMapper extends BaseMapper<College> {
    
    /**
     * 根据ID查询学院详情
     *
     * @param id 学院ID
     * @return 学院详情
     */
    CollegeVO getCollegeByIdWithDetails(@Param("id") Long id);
    
    /**
     * 分页查询学院列表
     *
     * @param code   学院代码
     * @param name   学院名称
     * @param status 状态
     * @param offset 偏移量
     * @param size   每页大小
     * @return 学院列表
     */
    List<CollegeVO> getCollegePage(
            @Param("code") String code,
            @Param("name") String name,
            @Param("status") Integer status,
            @Param("offset") int offset,
            @Param("size") int size
    );
    
    /**
     * 统计学院总数
     *
     * @param code   学院代码
     * @param name   学院名称
     * @param status 状态
     * @return 总数
     */
    Long countAllColleges(
            @Param("code") String code,
            @Param("name") String name,
            @Param("status") Integer status
    );
} 