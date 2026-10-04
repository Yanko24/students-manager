package com.example.studentsmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentsmanager.model.entity.Major;
import com.example.studentsmanager.model.dto.major.MajorQueryDTO;
import com.example.studentsmanager.model.vo.major.MajorVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MajorMapper extends BaseMapper<Major> {
    @Select("SELECT COUNT(*) > 0 FROM majors WHERE code = #{code} AND grade = #{grade} AND class_no = #{classNo} AND status = 0 AND is_deleted = 0")
    boolean existsActiveClass(@Param("code") String code,
                              @Param("grade") String grade,
                              @Param("classNo") String classNo);

    /**
     * 分页查询专业列表
     */
    List<MajorVO> getMajorPage(@Param("code") String code,
                               @Param("name") String name,
                               @Param("grade") String grade,
                               @Param("collegeId") Long collegeId,
                               @Param("status") Integer status,
                               @Param("offset") int offset,
                               @Param("size") int size);

    /**
     * 统计专业总数
     */
    Long countAllMajors(@Param("code") String code,
                       @Param("name") String name,
                       @Param("grade") String grade,
                       @Param("collegeId") Long collegeId,
                       @Param("status") Integer status);

    List<Major> searchMajors(@Param("query") MajorQueryDTO query);

   /**
     * 统计班级在读学生人数
     *
     * @param code 专业代码
     * @param grade 年级
     * @param classNo 班级号
     * @return 在读学生人数
     */
    Integer countClassEnrolledStudents(@Param("code") String code, 
                                     @Param("grade") String grade, 
                                     @Param("classNo") String classNo);

    /**
     * 根据专业代码、年级和班级号查询专业详细信息
     *
     * @param code 专业代码
     * @param grade 年级
     * @param classNo 班级号
     * @return 专业详细信息
     */
    MajorVO getMajorByIdWithDetails(@Param("code") String code,
                                   @Param("grade") String grade,
                                   @Param("classNo") String classNo);
} 
