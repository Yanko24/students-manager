package com.example.studentsmanager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studentsmanager.model.dto.course.CourseQueryDTO;
import com.example.studentsmanager.model.dto.course.CourseUpdateDTO;
import com.example.studentsmanager.model.entity.Course;
import com.example.studentsmanager.model.vo.course.CourseVO;

public interface CourseService extends IService<Course> {
    Page<CourseVO> getCoursePage(CourseQueryDTO queryDTO);
    CourseVO getCourse(Long id);
    CourseVO createCourse(CourseUpdateDTO updateDTO);
    CourseVO updateCourse(Long id, CourseUpdateDTO updateDTO);
    void deleteCourse(Long id);
}
