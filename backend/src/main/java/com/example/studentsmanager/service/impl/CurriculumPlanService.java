package com.example.studentsmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.studentsmanager.exception.BusinessException;
import com.example.studentsmanager.mapper.CurriculumPlanMapper;
import com.example.studentsmanager.model.dto.curriculum.CurriculumPlanDTO;
import com.example.studentsmanager.model.entity.CurriculumPlan;
import com.example.studentsmanager.model.vo.curriculum.CurriculumMajorOptionVO;
import com.example.studentsmanager.model.vo.curriculum.CurriculumPlanVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CurriculumPlanService extends ServiceImpl<CurriculumPlanMapper, CurriculumPlan> {
    public List<CurriculumMajorOptionVO> getMajorOptions() {
        return baseMapper.selectMajorOptions();
    }

    public Page<CurriculumPlanVO> getPage(long page, long size, String majorCode, String grade) {
        String normalizedMajorCode = majorCode == null || majorCode.isBlank() ? null : majorCode.trim();
        String normalizedGrade = grade == null || grade.isBlank() ? null : grade.trim();
        Page<CurriculumPlan> plans = page(new Page<>(Math.max(1, page), Math.min(100, Math.max(1, size))),
                new LambdaQueryWrapper<CurriculumPlan>()
                        .like(normalizedMajorCode != null, CurriculumPlan::getMajorCode, normalizedMajorCode)
                        .eq(normalizedGrade != null, CurriculumPlan::getGrade, normalizedGrade)
                        .orderByAsc(CurriculumPlan::getMajorCode, CurriculumPlan::getGrade));
        Page<CurriculumPlanVO> result = new Page<>(plans.getCurrent(), plans.getSize(), plans.getTotal());
        result.setRecords(plans.getRecords().stream().map(this::toVO).toList());
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public CurriculumPlanVO create(CurriculumPlanDTO dto, String actor) {
        validate(dto);
        if (getBaseMapper().countActiveMajorGrade(dto.getMajorCode().trim(), dto.getGrade().trim()) == 0) {
            throw new BusinessException("请选择有效的专业和年级");
        }
        if (count(new LambdaQueryWrapper<CurriculumPlan>().eq(CurriculumPlan::getMajorCode, dto.getMajorCode().trim())
                .eq(CurriculumPlan::getGrade, dto.getGrade().trim())) > 0) {
            throw new BusinessException("该专业和年级已经配置培养方案");
        }
        CurriculumPlan plan = new CurriculumPlan();
        apply(plan, dto);
        plan.setCreateBy(actor); plan.setUpdateBy(actor); plan.setIsDeleted(0);
        save(plan);
        synchronizeRequiredCourses(plan, dto.getRequiredCourseCatalogIds());
        return toVO(plan);
    }

    @Transactional(rollbackFor = Exception.class)
    public CurriculumPlanVO update(Long id, CurriculumPlanDTO dto, String actor) {
        CurriculumPlan plan = getById(id);
        if (plan == null) throw new BusinessException("培养方案不存在");
        validate(dto);
        if (getBaseMapper().countActiveMajorGrade(dto.getMajorCode().trim(), dto.getGrade().trim()) == 0) {
            throw new BusinessException("请选择有效的专业和年级");
        }
        if (count(new LambdaQueryWrapper<CurriculumPlan>().eq(CurriculumPlan::getMajorCode, dto.getMajorCode().trim())
                .eq(CurriculumPlan::getGrade, dto.getGrade().trim()).ne(CurriculumPlan::getId, id)) > 0) {
            throw new BusinessException("该专业和年级已经配置培养方案");
        }
        apply(plan, dto); plan.setUpdateBy(actor); updateById(plan);
        if (dto.getRequiredCourseCatalogIds() != null) {
            synchronizeRequiredCourses(plan, dto.getRequiredCourseCatalogIds());
        }
        return toVO(plan);
    }

    public void delete(Long id) {
        if (getById(id) == null) throw new BusinessException("培养方案不存在");
        removeById(id);
    }

    private void validate(CurriculumPlanDTO dto) {
        if (dto.getPlanName() == null || dto.getPlanName().isBlank() || dto.getPlanName().trim().length() > 100) {
            throw new BusinessException("请填写不超过100字的方案名称");
        }
        if (dto.getMajorCode() == null || dto.getMajorCode().isBlank() || dto.getGrade() == null
                || !dto.getGrade().trim().matches("\\d{4}")) {
            throw new BusinessException("请填写有效的专业代码和4位入学年级");
        }
        BigDecimal total = value(dto.getTotalCredits());
        BigDecimal required = value(dto.getRequiredCredits());
        BigDecimal elective = value(dto.getElectiveCredits());
        if (total.signum() <= 0 || required.signum() < 0 || elective.signum() < 0
                || required.add(elective).compareTo(total) > 0) {
            throw new BusinessException("毕业总学分必须大于0，且必修、选修学分之和不能超过总学分");
        }
    }

    private BigDecimal value(BigDecimal value) { return value == null ? BigDecimal.ZERO : value; }

    private void apply(CurriculumPlan plan, CurriculumPlanDTO dto) {
        plan.setPlanName(dto.getPlanName().trim()); plan.setMajorCode(dto.getMajorCode().trim());
        plan.setGrade(dto.getGrade().trim()); plan.setTotalCredits(value(dto.getTotalCredits()));
        plan.setRequiredCredits(value(dto.getRequiredCredits())); plan.setElectiveCredits(value(dto.getElectiveCredits()));
    }

    private void synchronizeRequiredCourses(CurriculumPlan plan, List<Long> ids) {
        List<Long> uniqueIds = ids == null ? List.of() : ids.stream().filter(id -> id != null).distinct().toList();
        if (ids != null && uniqueIds.size() != ids.size()) throw new BusinessException("必修课程列表包含重复或无效编号");
        if (!uniqueIds.isEmpty() && baseMapper.countCatalogIds(uniqueIds) != uniqueIds.size()) {
            throw new BusinessException("必修课程列表包含不存在的课程");
        }
        if (!uniqueIds.isEmpty() && baseMapper.countRequiredCatalogIds(uniqueIds) != uniqueIds.size()) {
            throw new BusinessException("逐门必修清单只能包含课程类型为必修课的课程");
        }
        baseMapper.deletePlanCourses(plan.getId());
        for (Long catalogId : uniqueIds) baseMapper.insertPlanCourse(plan.getId(), catalogId);
    }

    private CurriculumPlanVO toVO(CurriculumPlan plan) {
        CurriculumPlanVO vo = new CurriculumPlanVO();
        org.springframework.beans.BeanUtils.copyProperties(plan, vo);
        vo.setRequiredCourseCatalogIds(baseMapper.selectRequiredCourseCatalogIds(plan.getId()));
        return vo;
    }
}
