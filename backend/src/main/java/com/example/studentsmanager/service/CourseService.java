package com.example.studentsmanager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;
import com.example.studentsmanager.model.dto.course.CourseQueryDTO;
import com.example.studentsmanager.model.dto.course.CourseUpdateDTO;
import com.example.studentsmanager.model.entity.Course;
import com.example.studentsmanager.model.entity.CourseCatalog;
import com.example.studentsmanager.model.vo.course.CourseVO;
import com.example.studentsmanager.model.vo.course.CourseSelectionStudentVO;

import java.util.List;

public interface CourseService extends IService<Course> {
    Page<CourseVO> getCoursePage(CourseQueryDTO queryDTO);
    Page<CourseVO> getTeacherCoursePage(String username, CourseQueryDTO queryDTO);
    CourseVO getTeacherCourse(String username, Long courseId);
    Page<CourseSelectionStudentVO> getTeacherSelectedStudents(String username, Long courseId, long page, long size, String keyword);
    CourseVO getCourse(Long id);
    List<CourseCatalog> getCourseCatalogOptions();
    Page<CourseSelectionStudentVO> getSelectedStudents(Long courseId, long page, long size);
    Page<CourseSelectionStudentVO> getCourseSelections(Long courseId, long page, long size, String status);
    int reviewCourseSelections(Long courseId, List<Long> selectionIds, String action, String actor);
    CourseVO createCourse(CourseUpdateDTO updateDTO);
    CourseVO updateCourse(Long id, CourseUpdateDTO updateDTO);
    void deleteCourse(Long id);
}
