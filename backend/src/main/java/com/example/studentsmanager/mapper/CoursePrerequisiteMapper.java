package com.example.studentsmanager.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CoursePrerequisiteMapper {
    @Select("SELECT prerequisite.course_code FROM course_prerequisites cp " +
            "JOIN course_catalog prerequisite ON prerequisite.id = cp.prerequisite_catalog_id " +
            "WHERE cp.course_catalog_id = #{catalogId} ORDER BY prerequisite.course_code")
    List<String> selectCodes(@Param("catalogId") Long catalogId);

    @Select("SELECT prerequisite.course_code FROM course_prerequisites cp " +
            "JOIN course_catalog prerequisite ON prerequisite.id = cp.prerequisite_catalog_id " +
            "WHERE cp.course_catalog_id = #{catalogId} AND NOT EXISTS (" +
            "SELECT 1 FROM scores sc JOIN courses passed ON passed.id = sc.course_id " +
            "WHERE sc.student_id = #{studentId} AND passed.catalog_id = cp.prerequisite_catalog_id " +
            "AND sc.score >= 60 AND sc.publish_status = 'PUBLISHED' AND sc.is_deleted = 0 AND passed.is_deleted = 0) " +
            "ORDER BY prerequisite.course_code")
    List<String> selectMissingCodes(@Param("catalogId") Long catalogId, @Param("studentId") Long studentId);

    @Select("WITH RECURSIVE prerequisite_path AS (" +
            "SELECT prerequisite_catalog_id AS catalog_id FROM course_prerequisites WHERE course_catalog_id = #{startCatalogId} " +
            "UNION DISTINCT SELECT cp.prerequisite_catalog_id FROM course_prerequisites cp " +
            "JOIN prerequisite_path path ON cp.course_catalog_id = path.catalog_id) " +
            "SELECT COUNT(*) FROM prerequisite_path WHERE catalog_id = #{targetCatalogId}")
    int countPath(@Param("startCatalogId") Long startCatalogId, @Param("targetCatalogId") Long targetCatalogId);

    @Delete("DELETE FROM course_prerequisites WHERE course_catalog_id = #{catalogId}")
    int deleteForCatalog(@Param("catalogId") Long catalogId);

    @Insert("INSERT INTO course_prerequisites (course_catalog_id, prerequisite_catalog_id) VALUES (#{catalogId}, #{prerequisiteCatalogId})")
    int insert(@Param("catalogId") Long catalogId, @Param("prerequisiteCatalogId") Long prerequisiteCatalogId);
}
