package com.example.studentsmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studentsmanager.constant.ResultMessage;
import com.example.studentsmanager.exception.BusinessException;
import com.example.studentsmanager.mapper.MajorMapper;
import com.example.studentsmanager.model.vo.major.MajorVO;
import com.example.studentsmanager.model.dto.major.MajorQueryDTO;
import com.example.studentsmanager.model.dto.major.MajorUpdateDTO;
import com.example.studentsmanager.model.entity.Major;
import com.example.studentsmanager.core.response.PageResult;
import com.example.studentsmanager.service.MajorService;
import com.example.studentsmanager.utils.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MajorServiceImpl extends ServiceImpl<MajorMapper, Major> implements MajorService {

    @Override
    public PageResult<MajorVO> getMajorPage(MajorQueryDTO queryDTO) {
        log.debug("开始分页查询专业信息，查询条件：{}", queryDTO);
        try {
            // 计算分页参数
            int page = queryDTO.getPage();
            int size = queryDTO.getSize();
            int offset = (page - 1) * size;

            List<MajorVO> majors = baseMapper.getMajorPage(
                queryDTO.getCode(),
                queryDTO.getName(),
                queryDTO.getGrade(),
                queryDTO.getCollegeId(),
                queryDTO.getStatus(),
                offset,
                size
            );
            long total = baseMapper.countAllMajors(
                queryDTO.getCode(),
                queryDTO.getName(),
                queryDTO.getGrade(),
                queryDTO.getCollegeId(),
                queryDTO.getStatus()
            );
            log.debug("分页查询专业信息完成，总记录数：{}", total);
            return new PageResult<>(majors, total, size, page);
        } catch (Exception e) {
            log.error("分页查询专业信息异常，查询条件：{}，错误信息：{}", queryDTO, e.getMessage(), e);
            throw new BusinessException(ResultMessage.MAJOR_QUERY_FAILED);
        }
    }

    @Override
    public MajorVO getMajorById(String code, String grade, String classNo) {
        log.debug("开始查询专业信息，专业代码：{}，年级：{}，班级号：{}", code, grade, classNo);
        try {
            MajorVO majorVO = baseMapper.getMajorByIdWithDetails(code, grade, classNo);
            if (majorVO == null) {
                log.warn("查询专业信息失败，专业代码：{}，年级：{}，班级号：{}，原因：专业不存在", code, grade, classNo);
                throw new BusinessException(ResultMessage.MAJOR_NOT_FOUND);
            }
            log.debug("查询专业信息完成，专业代码：{}，年级：{}，班级号：{}", code, grade, classNo);
            return majorVO;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询专业信息异常，专业代码：{}，年级：{}，班级号：{}，错误信息：{}", code, grade, classNo, e.getMessage(), e);
            throw new BusinessException(ResultMessage.MAJOR_QUERY_FAILED);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MajorVO createMajor(MajorUpdateDTO majorUpdateDTO) {
        log.info("开始创建专业信息，专业信息：{}", majorUpdateDTO);
        try {
            // 检查是否已存在
            Major existingMajor = getMajorByCodeAndGradeAndClassNo(
                majorUpdateDTO.getCode(), majorUpdateDTO.getGrade(), majorUpdateDTO.getClassNo());
            if (existingMajor != null) {
                log.warn("创建专业信息失败，专业代码：{}，年级：{}，班级号：{}，原因：专业已存在", 
                    majorUpdateDTO.getCode(), majorUpdateDTO.getGrade(), majorUpdateDTO.getClassNo());
                throw new BusinessException(ResultMessage.MAJOR_ALREADY_EXISTS);
            }

            // 创建专业
            Major major = new Major();
            BeanUtils.copyProperties(majorUpdateDTO, major);
            setCreateInfo(major);
            this.save(major);

            log.info("创建专业信息成功，专业代码：{}，年级：{}，班级号：{}", 
                major.getCode(), major.getGrade(), major.getClassNo());
            return convertToDTO(major);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建专业信息异常，错误信息：{}", e.getMessage(), e);
            throw new BusinessException(ResultMessage.MAJOR_CREATE_FAILED);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MajorVO updateMajor(String code, String grade, String classNo, MajorUpdateDTO updateDTO) {
        log.info("开始更新专业信息，专业代码：{}，年级：{}，班级号：{}，更新内容：{}", 
            code, grade, classNo, updateDTO);
        try {
            // 检查是否存在
            Major major = getMajorByCodeAndGradeAndClassNo(code, grade, classNo);
            if (major == null) {
                log.warn("更新专业信息失败，专业代码：{}，年级：{}，班级号：{}，原因：专业不存在", code, grade, classNo);
                throw new BusinessException(ResultMessage.MAJOR_NOT_FOUND);
            }

            // 更新专业
            BeanUtils.copyProperties(updateDTO, major);
            setUpdateInfo(major);
            this.updateById(major);

            log.info("更新专业信息成功，专业代码：{}，年级：{}，班级号：{}", code, grade, classNo);
            return convertToDTO(major);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新专业信息异常，专业代码：{}，年级：{}，班级号：{}，错误信息：{}", 
                code, grade, classNo, e.getMessage(), e);
            throw new BusinessException(ResultMessage.MAJOR_UPDATE_FAILED);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMajor(String code, String grade, String classNo) {
        log.info("开始删除专业信息，专业代码：{}，年级：{}，班级号：{}", code, grade, classNo);
        try {
            // 检查是否存在
            Major major = getMajorByCodeAndGradeAndClassNo(code, grade, classNo);
            if (major == null) {
                log.warn("删除专业信息失败，专业代码：{}，年级：{}，班级号：{}，原因：专业不存在", code, grade, classNo);
                throw new BusinessException(ResultMessage.MAJOR_NOT_FOUND);
            }

            // 删除专业
            this.removeById(major);
            log.info("删除专业信息成功，专业代码：{}，年级：{}，班级号：{}", code, grade, classNo);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除专业信息异常，专业代码：{}，年级：{}，班级号：{}，错误信息：{}", 
                code, grade, classNo, e.getMessage(), e);
            throw new BusinessException(ResultMessage.MAJOR_DELETE_FAILED);
        }
    }

    @Override
    public List<MajorVO> getAllMajors() {
        log.debug("开始查询所有专业信息");
        try {
            MajorQueryDTO query = new MajorQueryDTO();
            List<Major> majors = baseMapper.searchMajors(query);
            List<MajorVO> result = majors.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            log.debug("查询所有专业信息完成，记录数：{}", result.size());
            return result;
        } catch (Exception e) {
            log.error("查询所有专业信息异常，错误信息：{}", e.getMessage(), e);
            throw new BusinessException(ResultMessage.MAJOR_QUERY_FAILED);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMajor(MajorVO majorVO) {
        log.info("开始添加专业信息，专业信息：{}", majorVO);
        try {
            Major major = new Major();
            BeanUtils.copyProperties(majorVO, major);
            setCreateInfo(major);
            this.save(major);
            log.info("添加专业信息成功，专业代码：{}，年级：{}，班级号：{}", 
                major.getCode(), major.getGrade(), major.getClassNo());
        } catch (Exception e) {
            log.error("添加专业信息异常，错误信息：{}", e.getMessage(), e);
            throw new BusinessException(ResultMessage.MAJOR_CREATE_FAILED);
        }
    }

    @Override
    public List<MajorVO> searchMajors(MajorQueryDTO query) {
        log.debug("开始搜索专业信息，查询条件：{}", query);
        try {
            List<Major> majors = baseMapper.searchMajors(query);
            List<MajorVO> result = majors.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            log.debug("搜索专业信息完成，记录数：{}", result.size());
            return result;
        } catch (Exception e) {
            log.error("搜索专业信息异常，查询条件：{}，错误信息：{}", query, e.getMessage(), e);
            throw new BusinessException(ResultMessage.MAJOR_QUERY_FAILED);
        }
    }

    /**
     * 根据专业代码、年级和班级号获取专业信息
     */
    private Major getMajorByCodeAndGradeAndClassNo(String code, String grade, String classNo) {
        return this.getOne(new LambdaQueryWrapper<Major>()
                .eq(Major::getCode, code)
                .eq(Major::getGrade, grade)
                .eq(Major::getClassNo, classNo));
    }

    /**
     * 设置创建信息
     */
    private void setCreateInfo(Major major) {
        major.setCreateTime(LocalDateTime.now());
        major.setUpdateTime(LocalDateTime.now());
        major.setCreateBy(SecurityUtils.getCurrentUsername());
        major.setUpdateBy(SecurityUtils.getCurrentUsername());
    }

    /**
     * 设置更新信息
     */
    private void setUpdateInfo(Major major) {
        major.setUpdateTime(LocalDateTime.now());
        major.setUpdateBy(SecurityUtils.getCurrentUsername());
    }

    private MajorVO convertToDTO(Major major) {
        if (major == null) {
            return null;
        }
        MajorVO majorVO = new MajorVO();
        BeanUtils.copyProperties(major, majorVO);

        // 设置学院名称
        if (major.getCollege() != null) {
            majorVO.setCollegeName(major.getCollege().getName());
        }

        // 设置班主任信息
        if (major.getHeadTeacher() != null) {
            majorVO.setHeadTeacherNumber(major.getHeadTeacher().getTeacherNumber());
            if (major.getHeadTeacher().getUser() != null) {
                majorVO.setHeadTeacherName(major.getHeadTeacher().getUser().getRealName());
            }
        }

        // 计算学生数量
        Integer studentCount = baseMapper.countClassEnrolledStudents(major.getCode(), major.getGrade(), major.getClassNo());
        majorVO.setStudentCount(studentCount);

        // 设置状态文字描述
        if (major.getStatus() != null) {
            switch (major.getStatus()) {
                case 0:
                    majorVO.setStatusText("正常");
                    break;
                case 1:
                    majorVO.setStatusText("停招");
                    break;
                case 2:
                    majorVO.setStatusText("撤销");
                    break;
                default:
                    majorVO.setStatusText("未知");
            }
        }

        return majorVO;
    }
} 