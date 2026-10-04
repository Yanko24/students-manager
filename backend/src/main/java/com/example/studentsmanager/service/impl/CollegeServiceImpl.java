package com.example.studentsmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studentsmanager.exception.BusinessException;
import com.example.studentsmanager.constant.ResultCode;
import com.example.studentsmanager.mapper.CollegeMapper;
import com.example.studentsmanager.model.dto.college.CollegeQueryDTO;
import com.example.studentsmanager.model.dto.college.CollegeUpdateDTO;
import com.example.studentsmanager.model.entity.College;
import com.example.studentsmanager.model.vo.college.CollegeVO;
import com.example.studentsmanager.service.CollegeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CollegeServiceImpl extends ServiceImpl<CollegeMapper, College> implements CollegeService {

    @Override
    public IPage<CollegeVO> getCollegePage(CollegeQueryDTO queryDTO) {
        log.info("分页查询学院信息，查询条件：{}", queryDTO);
        try {
            // 构建查询条件
            LambdaQueryWrapper<College> wrapper = new LambdaQueryWrapper<>();
            wrapper.like(queryDTO.getCode() != null, College::getCode, queryDTO.getCode())
                    .like(queryDTO.getName() != null, College::getName, queryDTO.getName())
                    .eq(queryDTO.getStatus() != null, College::getStatus, queryDTO.getStatus())
                    .eq(College::getIsDeleted, 0)
                    .orderByDesc(College::getCreateTime);

            // 执行分页查询
            Page<College> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
            IPage<College> collegePage = this.page(page, wrapper);

            // 转换为VO
            return collegePage.convert(this::convertToVO);
        } catch (Exception e) {
            log.error("分页查询学院信息失败", e);
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "分页查询学院信息失败");
        }
    }

    @Override
    public CollegeVO getCollegeById(Long id) {
        log.info("根据ID查询学院信息，ID：{}", id);
        try {
            College college = this.getById(id);
            if (college == null || college.getIsDeleted() == 1) {
                throw new BusinessException(ResultCode.NOT_FOUND, "学院不存在");
            }
            return convertToVO(college);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询学院信息失败", e);
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "查询学院信息失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CollegeVO createCollege(CollegeUpdateDTO updateDTO) {
        log.info("创建学院，学院信息：{}", updateDTO);
        try {
            // 检查学院代码是否已存在
            LambdaQueryWrapper<College> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(College::getCode, updateDTO.getCode())
                    .eq(College::getIsDeleted, 0);
            if (this.count(wrapper) > 0) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "学院代码已存在");
            }

            // 转换为实体
            College college = new College();
            BeanUtils.copyProperties(updateDTO, college);
            college.setIsDeleted(0);

            // 保存学院信息
            this.save(college);
            log.info("创建学院成功，ID：{}", college.getId());

            return convertToVO(college);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建学院失败", e);
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "创建学院失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CollegeVO updateCollege(Long id, CollegeUpdateDTO updateDTO) {
        log.info("更新学院信息，ID：{}，更新信息：{}", id, updateDTO);
        try {
            // 检查学院是否存在
            College college = this.getById(id);
            if (college == null || college.getIsDeleted() == 1) {
                throw new BusinessException(ResultCode.NOT_FOUND, "学院不存在");
            }

            // 更新学院信息
            BeanUtils.copyProperties(updateDTO, college);
            this.updateById(college);
            log.info("更新学院信息成功，ID：{}", id);

            return convertToVO(college);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新学院信息失败", e);
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "更新学院信息失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCollege(Long id) {
        log.info("删除学院，ID：{}", id);
        try {
            // 检查学院是否存在
            College college = this.getById(id);
            if (college == null || college.getIsDeleted() == 1) {
                throw new BusinessException(ResultCode.NOT_FOUND, "学院不存在");
            }

            // 软删除
            college.setIsDeleted(1);
            this.updateById(college);
            log.info("删除学院成功，ID：{}", id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除学院失败", e);
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "删除学院失败");
        }
    }

    @Override
    public List<CollegeVO> getAllColleges() {
        log.info("获取所有学院列表");
        try {
            // 查询所有未删除的学院
            LambdaQueryWrapper<College> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(College::getIsDeleted, 0)
                    .orderByDesc(College::getCreateTime);

            List<College> colleges = this.list(wrapper);
            return colleges.stream()
                    .map(this::convertToVO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取学院列表失败", e);
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "获取学院列表失败");
        }
    }

    /**
     * 将实体转换为VO
     *
     * @param college 学院实体
     * @return 学院VO
     */
    private CollegeVO convertToVO(College college) {
        if (college == null) {
            return null;
        }
        CollegeVO vo = new CollegeVO();
        BeanUtils.copyProperties(college, vo);
        
        // 设置状态文字描述
        vo.setStatusText(college.getStatus() == 0 ? "禁用" : "启用");
        
        return vo;
    }
} 