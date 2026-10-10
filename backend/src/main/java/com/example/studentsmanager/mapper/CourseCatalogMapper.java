package com.example.studentsmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentsmanager.model.entity.CourseCatalog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CourseCatalogMapper extends BaseMapper<CourseCatalog> {
    @Select("SELECT * FROM course_catalog WHERE course_code = #{courseCode} LIMIT 1")
    CourseCatalog selectByCourseCode(@Param("courseCode") String courseCode);

    @Select("SELECT * FROM course_catalog ORDER BY course_code")
    List<CourseCatalog> selectOptions();
}
