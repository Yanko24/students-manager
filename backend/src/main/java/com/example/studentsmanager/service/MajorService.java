package com.example.studentsmanager.service;

import com.example.studentsmanager.model.vo.major.MajorVO;
import com.example.studentsmanager.model.dto.major.MajorQueryDTO;
import com.example.studentsmanager.model.dto.major.MajorUpdateDTO;
import com.example.studentsmanager.core.response.PageResult;

import java.util.List;

public interface MajorService {
    /**
     * 分页查询专业列表
     */
    PageResult<MajorVO> getMajorPage(MajorQueryDTO queryDTO);

    /**
     * 获取专业详情
     */
    MajorVO getMajorById(String code, String grade, String classNo);

    /**
     * 创建专业
     */
    MajorVO createMajor(MajorUpdateDTO majorUpdateDTO);

    /**
     * 更新专业
     */
    MajorVO updateMajor(String code, String grade, String classNo, MajorUpdateDTO updateDTO);

    /**
     * 删除专业
     */
    void deleteMajor(String code, String grade, String classNo);

    /**
     * 获取所有专业
     */
    List<MajorVO> getAllMajors();

    /**
     * 添加专业
     */
    void addMajor(MajorVO majorVO);

    /**
     * 搜索专业
     */
    List<MajorVO> searchMajors(MajorQueryDTO queryDTO);
} 