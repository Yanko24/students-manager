package com.example.studentsmanager.service;

import com.example.studentsmanager.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<User> {
    User findByUsername(String username);
    boolean createUser(User user);
    boolean updateUser(User user);
    boolean deleteUser(Long id);
    boolean softDeleteUser(Long id, String updateBy);
} 