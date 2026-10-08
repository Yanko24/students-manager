package com.example.studentsmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.studentsmanager.exception.BusinessException;
import com.example.studentsmanager.mapper.SystemNotificationMapper;
import com.example.studentsmanager.model.entity.SystemNotification;
import com.example.studentsmanager.model.entity.User;
import com.example.studentsmanager.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;

@Service
public class SystemNotificationService extends ServiceImpl<SystemNotificationMapper, SystemNotification> {
    private final UserService userService;
    public SystemNotificationService(UserService userService) { this.userService = userService; }

    public Page<SystemNotification> mine(String username, long page, long size, boolean unreadOnly, String type) {
        User user = userService.findByUsername(username);
        if (user == null) throw new BusinessException("当前账号不存在");
        long current = Math.max(1, page);
        long pageSize = Math.min(100, Math.max(1, size));
        LambdaQueryWrapper<SystemNotification> query = new LambdaQueryWrapper<SystemNotification>()
                .eq(SystemNotification::getUserId, user.getId())
                .isNull(unreadOnly, SystemNotification::getReadAt)
                .eq(type != null && !type.isBlank(), SystemNotification::getNotificationType, type.trim())
                .orderByDesc(SystemNotification::getCreateTime);
        return page(new Page<>(current, pageSize), query);
    }

    @Transactional(rollbackFor = Exception.class)
    public int unreadCount(String username) {
        User user = userService.findByUsername(username);
        if (user == null) throw new BusinessException("当前账号不存在");
        return Math.toIntExact(count(new LambdaQueryWrapper<SystemNotification>().eq(SystemNotification::getUserId, user.getId())
                .isNull(SystemNotification::getReadAt)));
    }

    public void notifyUser(Long userId, String type, String title, String message) {
        SystemNotification notification = new SystemNotification();
        notification.setUserId(userId); notification.setNotificationType(type); notification.setTitle(title); notification.setMessage(message);
        save(notification);
    }

    @Transactional(rollbackFor = Exception.class)
    public void notifyAdmins(String type, String title, String message) {
        userService.list(new LambdaQueryWrapper<User>().select(User::getId).eq(User::getRole, "admin").eq(User::getStatus, 0))
                .forEach(user -> notifyUser(user.getId(), type, title, message));
    }

    @Transactional(rollbackFor = Exception.class)
    public void notifyRoles(Collection<String> roles, String type, String title, String message) {
        if (roles == null || roles.isEmpty()) return;
        userService.list(new LambdaQueryWrapper<User>().select(User::getId)
                        .in(User::getRole, roles).eq(User::getStatus, 0))
                .forEach(user -> notifyUser(user.getId(), type, title, message));
    }

    @Transactional(rollbackFor = Exception.class)
    public int markAllRead(String username) {
        User user = userService.findByUsername(username);
        if (user == null) throw new BusinessException("当前账号不存在");
        return getBaseMapper().update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<SystemNotification>()
                .eq(SystemNotification::getUserId, user.getId())
                .isNull(SystemNotification::getReadAt)
                .set(SystemNotification::getReadAt, LocalDateTime.now()));
    }

    public void markRead(Long id, String username) {
        User user = userService.findByUsername(username);
        SystemNotification notification = getById(id);
        if (user == null || notification == null || !user.getId().equals(notification.getUserId())) throw new BusinessException("通知不存在");
        if (notification.getReadAt() == null) { notification.setReadAt(LocalDateTime.now()); updateById(notification); }
    }
}
