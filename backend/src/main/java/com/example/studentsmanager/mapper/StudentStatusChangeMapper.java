package com.example.studentsmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentsmanager.model.entity.StudentStatusChangeRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StudentStatusChangeMapper extends BaseMapper<StudentStatusChangeRequest> {

    @Select("SELECT r.*, s.student_no AS student_no, u.real_name AS student_name " +
            "FROM student_status_change_requests r " +
            "LEFT JOIN students s ON s.id = r.student_id AND s.is_deleted = 0 " +
            "LEFT JOIN users u ON u.id = s.user_id AND u.is_deleted = 0 " +
            "WHERE (#{status} IS NULL OR #{status} = '' OR r.status = #{status}) " +
            "ORDER BY r.create_time DESC")
    Page<StudentStatusChangeRequest> selectAdminPage(Page<StudentStatusChangeRequest> page,
                                                       @Param("status") String status);
}
