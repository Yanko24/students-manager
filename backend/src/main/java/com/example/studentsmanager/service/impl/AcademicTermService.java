package com.example.studentsmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.studentsmanager.exception.BusinessException;
import com.example.studentsmanager.mapper.AcademicTermMapper;
import com.example.studentsmanager.model.dto.AcademicTermDTO;
import com.example.studentsmanager.model.entity.AcademicTerm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AcademicTermService extends ServiceImpl<AcademicTermMapper, AcademicTerm> {
    private static final Pattern TERM_PATTERN = Pattern.compile("^(\\d{4})-(\\d{4})-([12])$");

    public List<AcademicTerm> getActiveTerms() {
        return list(new LambdaQueryWrapper<AcademicTerm>().eq(AcademicTerm::getIsActive, 1)
                .orderByDesc(AcademicTerm::getAcademicYear).orderByAsc(AcademicTerm::getTermNo));
    }

    public List<AcademicTerm> getAllTerms() {
        return list(new LambdaQueryWrapper<AcademicTerm>().orderByDesc(AcademicTerm::getAcademicYear).orderByAsc(AcademicTerm::getTermNo));
    }

    public AcademicTerm getCurrentTerm() {
        return getOne(new LambdaQueryWrapper<AcademicTerm>().eq(AcademicTerm::getIsCurrent, 1)
                .eq(AcademicTerm::getIsActive, 1).last("LIMIT 1"));
    }

    @Transactional(rollbackFor = Exception.class)
    public AcademicTerm create(AcademicTermDTO dto, String actor) {
        AcademicTerm term = from(dto);
        if (count(new LambdaQueryWrapper<AcademicTerm>().eq(AcademicTerm::getTermCode, term.getTermCode())) > 0) {
            throw new BusinessException("该学期已经存在");
        }
        term.setIsCurrent(0);
        term.setCreateBy(actor);
        term.setUpdateBy(actor);
        save(term);
        return term;
    }

    @Transactional(rollbackFor = Exception.class)
    public AcademicTerm update(Long id, AcademicTermDTO dto, String actor) {
        AcademicTerm term = getById(id);
        if (term == null) throw new BusinessException("学期不存在");
        AcademicTerm updated = from(dto);
        if (!term.getTermCode().equals(updated.getTermCode())
                && (baseMapper.countCourseReferences(term.getTermCode()) > 0 || baseMapper.countScoreReferences(term.getTermCode()) > 0)) {
            throw new BusinessException("该学期已关联课程或成绩，不能修改学期编码");
        }
        if (count(new LambdaQueryWrapper<AcademicTerm>().eq(AcademicTerm::getTermCode, updated.getTermCode()).ne(AcademicTerm::getId, id)) > 0) {
            throw new BusinessException("该学期编码已被使用");
        }
        term.setTermCode(updated.getTermCode());
        term.setAcademicYear(updated.getAcademicYear());
        term.setTermNo(updated.getTermNo());
        term.setStartDate(updated.getStartDate());
        term.setEndDate(updated.getEndDate());
        term.setIsActive(updated.getIsActive());
        if (Integer.valueOf(1).equals(term.getIsCurrent()) && !Integer.valueOf(1).equals(term.getIsActive())) {
            throw new BusinessException("当前学期不能停用，请先切换当前学期");
        }
        term.setUpdateBy(actor);
        updateById(term);
        return term;
    }

    @Transactional(rollbackFor = Exception.class)
    public AcademicTerm setCurrent(Long id, String actor) {
        baseMapper.lockAllTerms();
        AcademicTerm target = getById(id);
        if (target == null || !Integer.valueOf(1).equals(target.getIsActive())) {
            throw new BusinessException("请选择有效的学期");
        }
        baseMapper.clearCurrent(actor);
        baseMapper.setCurrent(id, actor);
        target.setIsCurrent(1);
        return target;
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        AcademicTerm term = getById(id);
        if (term == null) throw new BusinessException("学期不存在");
        if (Integer.valueOf(1).equals(term.getIsCurrent())) throw new BusinessException("当前学期不能删除");
        if (baseMapper.countCourseReferences(term.getTermCode()) > 0 || baseMapper.countScoreReferences(term.getTermCode()) > 0) {
            throw new BusinessException("学期已关联课程或成绩，不能删除；可以将其停用");
        }
        removeById(id);
    }

    private AcademicTerm from(AcademicTermDTO dto) {
        String termCode = dto.getTermCode() == null ? "" : dto.getTermCode().trim();
        Matcher matcher = TERM_PATTERN.matcher(termCode);
        if (!matcher.matches() || Integer.parseInt(matcher.group(2)) != Integer.parseInt(matcher.group(1)) + 1) {
            throw new BusinessException("学期编码格式应为 YYYY-YYYY-1 或 YYYY-YYYY-2");
        }
        if (dto.getStartDate() != null && dto.getEndDate() != null && dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BusinessException("学期结束日期不能早于开始日期");
        }
        if ((dto.getStartDate() == null) != (dto.getEndDate() == null)) {
            throw new BusinessException("学期开始和结束日期需要同时填写");
        }
        int active = dto.getIsActive() == null ? 1 : dto.getIsActive();
        if (active != 0 && active != 1) throw new BusinessException("学期启用状态无效");
        AcademicTerm term = new AcademicTerm();
        term.setTermCode(termCode);
        term.setAcademicYear(matcher.group(1) + "-" + matcher.group(2));
        term.setTermNo(Integer.parseInt(matcher.group(3)));
        term.setStartDate(dto.getStartDate());
        term.setEndDate(dto.getEndDate());
        term.setIsActive(active);
        return term;
    }
}
