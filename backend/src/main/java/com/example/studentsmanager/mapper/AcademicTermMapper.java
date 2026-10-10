package com.example.studentsmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentsmanager.model.entity.AcademicTerm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AcademicTermMapper extends BaseMapper<AcademicTerm> {
    @Select("SELECT id FROM academic_terms FOR UPDATE")
    java.util.List<Long> lockAllTerms();

    @Update("UPDATE academic_terms SET is_current = 0, update_by = #{actor} WHERE is_current = 1")
    int clearCurrent(@Param("actor") String actor);

    @Update("UPDATE academic_terms SET is_current = 1, is_active = 1, update_by = #{actor} WHERE id = #{id}")
    int setCurrent(@Param("id") Long id, @Param("actor") String actor);

    @Select("SELECT COUNT(*) FROM courses WHERE semester = #{termCode} AND is_deleted = 0")
    int countCourseReferences(@Param("termCode") String termCode);

    @Select("SELECT COUNT(*) FROM scores WHERE semester = #{termCode} AND is_deleted = 0")
    int countScoreReferences(@Param("termCode") String termCode);

    @Select("SELECT COUNT(*) FROM academic_terms WHERE term_code = #{termCode} AND is_active = 1")
    int countActiveTerm(@Param("termCode") String termCode);
}
