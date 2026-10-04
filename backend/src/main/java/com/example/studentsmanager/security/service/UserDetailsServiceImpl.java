package com.example.studentsmanager.security.service;

import com.example.studentsmanager.mapper.UserMapper;
import com.example.studentsmanager.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 先查询用户是否存在
        User user = userMapper.findActiveUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 检查用户状态
        if (user.getIsDeleted() == 1) {
            throw new UsernameNotFoundException("用户已被删除");
        }
        if (user.getStatus() == 1) {
            throw new UsernameNotFoundException("用户已被禁用");
        }

        // 将角色名转换为大写，并添加 ROLE_ 前缀
        String role = "ROLE_" + user.getRole().toUpperCase();
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        if (Boolean.TRUE.equals(user.getMustChangePassword())) {
            authorities.add(new SimpleGrantedAuthority("ROLE_PASSWORD_CHANGE_REQUIRED"));
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
} 
