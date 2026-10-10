package com.example.studentsmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentsmanager.model.vo.student.StudentVO;
import com.example.studentsmanager.model.dto.student.StudentQueryDTO;
import com.example.studentsmanager.model.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {
    @Select("SELECT * FROM students WHERE id = #{id} AND is_deleted = 0 FOR UPDATE")
    Student selectForUpdate(@Param("id") Long id);

    @Select("SELECT * FROM students WHERE student_no = #{studentNumber}")
    Student findByStudentNumber(String studentNumber);

    // 分页查询学生列表（包含用户信息）
    Page<StudentVO> selectStudentPage(Page<Student> page, @Param("query") StudentQueryDTO queryDTO);
    
    // 查询所有学生信息（包含用户和专业信息）
    List<StudentVO> selectAllStudentsWithInfo();

    // 软删除学生
    int softDeleteStudent(@Param("id") Long id, @Param("updateBy") String updateBy);

    // 根据ID获取学生详情
    StudentVO getStudentById(@Param("id") Long id);

    // 分页查询所有学生
    List<StudentVO> getAllStudents(
        @Param("studentNo") String studentNo,
        @Param("name") String name,
        @Param("majorCode") String majorCode,
        @Param("grade") String grade,
        @Param("classNo") String classNo,
        @Param("offset") int offset,
        @Param("pageSize") int pageSize
    );

    // 统计学生总数
    Long countAllStudents(
        @Param("studentNo") String studentNo,
        @Param("name") String name,
        @Param("majorCode") String majorCode,
        @Param("grade") String grade,
        @Param("classNo") String classNo
    );

    StudentVO selectByIdWithInfo(Long id);

    /**
     * 统计班级总人数（包括所有状态）
     * @param majorCode 专业代码
     * @param grade 年级
     * @param classNo 班级号
     * @return 班级总人数
     */
    Integer countClassTotalStudents(@Param("majorCode") String majorCode, 
                                  @Param("grade") String grade, 
                                  @Param("classNo") String classNo);
    
    /**
     * 统计班级在读学生人数
     * @param majorCode 专业代码
     * @param grade 年级
     * @param classNo 班级号
     * @return 在读学生人数
     */
    Integer countClassEnrolledStudents(@Param("majorCode") String majorCode, 
                                     @Param("grade") String grade, 
                                     @Param("classNo") String classNo);
}
