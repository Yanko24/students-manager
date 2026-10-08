package com.example.studentsmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentsmanager.model.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {
    @Update("UPDATE courses SET course_name = #{courseName}, credits = #{credits}, course_type = #{courseType}, hours = #{hours}, description = #{description}, objectives = #{objectives} WHERE catalog_id = #{catalogId} AND id <> #{excludeCourseId}")
    int synchronizeCatalogFields(@Param("catalogId") Long catalogId, @Param("excludeCourseId") Long excludeCourseId,
                                 @Param("courseName") String courseName, @Param("credits") java.math.BigDecimal credits,
                                 @Param("courseType") String courseType, @Param("hours") Integer hours,
                                 @Param("description") String description, @Param("objectives") String objectives);

    @Select("SELECT * FROM courses WHERE id = #{id} AND is_deleted = 0 FOR UPDATE")
    Course selectForUpdate(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM colleges WHERE id = #{collegeId} AND is_deleted = 0")
    int countActiveCollege(@Param("collegeId") Long collegeId);

    @Select("SELECT COUNT(*) FROM majors WHERE code = #{majorCode} AND college_id = #{collegeId} AND is_deleted = 0")
    int countActiveMajorInCollege(@Param("majorCode") String majorCode, @Param("collegeId") Long collegeId);

    @Select("SELECT name FROM colleges WHERE id = #{collegeId} AND is_deleted = 0")
    String findCollegeName(@Param("collegeId") Long collegeId);

    @Select("SELECT name FROM majors WHERE code = #{majorCode} AND is_deleted = 0 ORDER BY grade DESC, class_no ASC LIMIT 1")
    String findMajorName(@Param("majorCode") String majorCode);

    @Select({
            "SELECT COUNT(*) FROM courses c",
            "JOIN students s ON s.id = #{studentId} AND s.is_deleted = 0 AND s.status = 0",
            "LEFT JOIN majors m ON m.code = s.major_code AND m.grade = s.grade AND m.class_no = s.class_no AND m.is_deleted = 0",
            "WHERE c.id = #{courseId} AND c.is_deleted = 0 AND (",
            "(c.selection_scope = 'ALL' AND (c.selection_grade IS NULL OR c.selection_grade = s.grade)) OR",
            "(c.selection_scope = 'COLLEGE' AND c.selection_college_id = m.college_id AND (c.selection_grade IS NULL OR c.selection_grade = s.grade)) OR",
            "(c.selection_scope = 'MAJOR' AND c.selection_major_code = s.major_code AND c.selection_college_id = m.college_id AND (c.selection_grade IS NULL OR c.selection_grade = s.grade)))"
    })
    int countStudentEligibility(@Param("courseId") Long courseId, @Param("studentId") Long studentId);
}
