package com.example.studentsmanager.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studentsmanager.model.dto.college.CollegeQueryDTO;
import com.example.studentsmanager.model.dto.college.CollegeUpdateDTO;
import com.example.studentsmanager.model.entity.College;
import com.example.studentsmanager.model.vo.college.CollegeVO;

import java.util.List;

public interface CollegeService extends IService<College> {
    
    /**
     * 分页查询学院信息
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    IPage<CollegeVO> getCollegePage(CollegeQueryDTO queryDTO);
    
    /**
     * 根据ID查询学院信息
     *
     * @param id 学院ID
     * @return 学院信息
     */
    CollegeVO getCollegeById(Long id);
    
    /**
     * 创建学院
     *
     * @param updateDTO 学院信息
     * @return 创建后的学院信息
     */
    CollegeVO createCollege(CollegeUpdateDTO updateDTO);
    
    /**
     * 更新学院信息
     *
     * @param id        学院ID
     * @param updateDTO 更新信息
     * @return 更新后的学院信息
     */
    CollegeVO updateCollege(Long id, CollegeUpdateDTO updateDTO);
    
    /**
     * 删除学院
     *
     * @param id 学院ID
     */
    void deleteCollege(Long id);
    
    /**
     * 获取所有学院列表
     *
     * @return 学院列表
     */
    List<CollegeVO> getAllColleges();
} 