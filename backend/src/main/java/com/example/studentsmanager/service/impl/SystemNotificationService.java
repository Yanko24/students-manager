package com.example.studentsmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.studentsmanager.exception.BusinessException;
import com.example.studentsmanager.mapper.SystemNotificationMapper;
import com.example.studentsmanager.model.entity.SystemNotification;
import com.example.studentsmanager.model.entity.User;
import com.example.studentsmanager.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SystemNotificationService extends ServiceImpl<SystemNotificationMapper, SystemNotification> {
    private final UserService userService;
    public SystemNotificationService(UserService userService) { this.userService = userService; }

    public List<SystemNotification> mine(String username) {
        User user = userService.findByUsername(username);
        if (user == null) throw new BusinessException("当前账号不存在");
        return list(new LambdaQueryWrapper<SystemNotification>().eq(SystemNotification::getUserId, user.getId())
                .orderByDesc(SystemNotification::getCreateTime).last("LIMIT 100"));
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

    public void notifyAdmins(String type, String title, String message) {
        userService.list(new LambdaQueryWrapper<User>().select(User::getId).eq(User::getRole, "admin").eq(User::getStatus, 0))
                .forEach(user -> notifyUser(user.getId(), type, title, message));
    }

    public void markRead(Long id, String username) {
        User user = userService.findByUsername(username);
        SystemNotification notification = getById(id);
        if (user == null || notification == null || !user.getId().equals(notification.getUserId())) throw new BusinessException("通知不存在");
        if (notification.getReadAt() == null) { notification.setReadAt(LocalDateTime.now()); updateById(notification); }
    }
}
