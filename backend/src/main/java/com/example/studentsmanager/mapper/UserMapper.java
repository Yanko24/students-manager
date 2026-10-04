package com.example.studentsmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.studentsmanager.model.entity.User;
import com.example.studentsmanager.model.dto.user.UserContactData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import com.example.studentsmanager.security.handler.Sm4StringTypeHandler;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT COUNT(*) FROM users WHERE username = #{username}")
    long countByUsernameIncludingDeleted(@Param("username") String username);

    @Select("SELECT id, phone, email FROM users")
    java.util.List<UserContactData> selectAllContactData();

    @Update("UPDATE users SET phone = #{phone}, email = #{email} WHERE id = #{id}")
    int updateContactData(UserContactData contactData);

    @Select("SELECT * FROM users WHERE username = #{username}")
    @Results(id = "userContactResult", value = {
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "username", property = "username"),
            @Result(column = "password", property = "password"),
            @Result(column = "role", property = "role"),
            @Result(column = "real_name", property = "realName"),
            @Result(column = "gender", property = "gender"),
            @Result(column = "phone", property = "phone", typeHandler = Sm4StringTypeHandler.class),
            @Result(column = "email", property = "email", typeHandler = Sm4StringTypeHandler.class),
            @Result(column = "status", property = "status"),
            @Result(column = "must_change_password", property = "mustChangePassword"),
            @Result(column = "create_time", property = "createTime"),
            @Result(column = "update_time", property = "updateTime"),
            @Result(column = "create_by", property = "createBy"),
            @Result(column = "update_by", property = "updateBy"),
            @Result(column = "is_deleted", property = "isDeleted")
    })
    User findByUsername(String username);

    @Select("SELECT * FROM users WHERE username = #{username} AND is_deleted = 0 AND status = 0")
    @ResultMap("userContactResult")
    User findActiveUserByUsername(String username);

    @Update("UPDATE users SET is_deleted = 1, status = 1, update_time = NOW(), update_by = #{updateBy} WHERE id = #{id} AND is_deleted = 0")
    int softDeleteUser(@Param("id") Long id, @Param("updateBy") String updateBy);
} 
