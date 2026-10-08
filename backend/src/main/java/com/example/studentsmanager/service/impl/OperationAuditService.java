package com.example.studentsmanager.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.studentsmanager.mapper.OperationAuditMapper;
import com.example.studentsmanager.model.entity.OperationAudit;
import org.springframework.stereotype.Service;

@Service
public class OperationAuditService extends ServiceImpl<OperationAuditMapper, OperationAudit> {
    public Page<OperationAudit> page(long page, long size, String actor, String action) {
        return page(new Page<>(Math.max(1, page), Math.min(100, Math.max(1, size))),
                new LambdaQueryWrapper<OperationAudit>()
                        .like(actor != null && !actor.isBlank(), OperationAudit::getActor, actor == null ? null : actor.trim())
                        .eq(action != null && !action.isBlank(), OperationAudit::getAction, action == null ? null : action.trim())
                        .orderByDesc(OperationAudit::getCreateTime, OperationAudit::getId));
    }

    public void record(String actor, String action, String entityType, Object entityId, String summary) {
        OperationAudit audit = new OperationAudit();
        audit.setActor(actor); audit.setAction(action); audit.setEntityType(entityType);
        audit.setEntityId(String.valueOf(entityId)); audit.setSummary(summary);
        save(audit);
    }
}
