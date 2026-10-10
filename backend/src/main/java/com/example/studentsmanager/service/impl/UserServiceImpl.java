package com.example.studentsmanager.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.studentsmanager.constant.ResultMessage;
import com.example.studentsmanager.exception.BusinessException;
import com.example.studentsmanager.mapper.UserMapper;
import com.example.studentsmanager.model.entity.User;
import com.example.studentsmanager.service.UserService;
import com.example.studentsmanager.utils.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User findByUsername(String username) {
        log.debug("开始查询用户信息");
        try {
            User user = baseMapper.findByUsername(username);
            log.debug("查询用户信息完成");
            return user;
        } catch (Exception e) {
            log.error("查询用户信息异常", e);
            throw new BusinessException(ResultMessage.USER_QUERY_FAILED);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createUser(User user) {
        log.info("开始创建用户信息");
        try {
            // 检查用户名是否已存在
            if (findByUsername(user.getUsername()) != null) {
                log.warn("创建用户信息失败，原因：用户名已存在");
                throw new BusinessException(ResultMessage.USER_ALREADY_EXISTS);
            }

            // 设置创建信息
            user.setPassword(passwordEncoder.encode("xiaoer"));
            user.setMustChangePassword(true);
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            user.setCreateBy(SecurityUtils.getCurrentUsername());
            user.setUpdateBy(SecurityUtils.getCurrentUsername());

            boolean result = save(user);
            if (result) {
                log.info("创建用户信息成功，用户ID：{}", user.getId());
            } else {
                log.warn("创建用户信息失败");
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建用户信息异常，错误信息：{}", e.getMessage(), e);
            throw new BusinessException(ResultMessage.USER_CREATE_FAILED);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(User user) {
        log.info("开始更新用户信息，用户ID：{}", user.getId());
        try {
            // 检查用户是否存在
            User existingUser = getById(user.getId());
            if (existingUser == null) {
                log.warn("更新用户信息失败，用户ID：{}，原因：用户不存在", user.getId());
                throw new BusinessException(ResultMessage.USER_NOT_FOUND);
            }

            // 设置更新信息
            user.setUpdateTime(LocalDateTime.now());
            user.setUpdateBy(SecurityUtils.getCurrentUsername());

            boolean result = updateById(user);
            if (result) {
                log.info("更新用户信息成功，用户ID：{}", user.getId());
            } else {
                log.warn("更新用户信息失败，用户ID：{}", user.getId());
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新用户信息异常，用户ID：{}，错误信息：{}", user.getId(), e.getMessage(), e);
            throw new BusinessException(ResultMessage.USER_UPDATE_FAILED);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(Long id) {
        log.info("开始删除用户信息，用户ID：{}", id);
        try {
            // 检查用户是否存在
            User existingUser = getById(id);
            if (existingUser == null) {
                log.warn("删除用户信息失败，用户ID：{}，原因：用户不存在", id);
                throw new BusinessException(ResultMessage.USER_NOT_FOUND);
            }

            boolean result = removeById(id);
            if (result) {
                log.info("删除用户信息成功，用户ID：{}", id);
            } else {
                log.warn("删除用户信息失败，用户ID：{}", id);
            }
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除用户信息异常，用户ID：{}，错误信息：{}", id, e.getMessage(), e);
            throw new BusinessException(ResultMessage.USER_DELETE_FAILED);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean softDeleteUser(Long id, String updateBy) {
        log.info("开始软删除用户信息，用户ID：{}，操作人：{}", id, updateBy);
        try {
            // 检查用户是否存在
            User existingUser = getById(id);
            if (existingUser == null) {
                log.warn("软删除用户信息失败，用户ID：{}，原因：用户不存在", id);
                throw new BusinessException(ResultMessage.USER_NOT_FOUND);
            }

            int result = baseMapper.softDeleteUser(id, updateBy);
            if (result > 0) {
                log.info("软删除用户信息成功，用户ID：{}", id);
                return true;
            } else {
                log.warn("软删除用户信息失败，用户ID：{}", id);
                return false;
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("软删除用户信息异常，用户ID：{}，错误信息：{}", id, e.getMessage(), e);
            throw new BusinessException(ResultMessage.USER_DELETE_FAILED);
        }
    }
}
