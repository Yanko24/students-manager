package com.example.studentsmanager.service.impl;

import com.example.studentsmanager.exception.BusinessException;
import com.example.studentsmanager.mapper.CourseSelectionMapper;
import com.example.studentsmanager.model.entity.Course;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CourseSelectionQueueService {
    private final CourseSelectionMapper courseSelectionMapper;

    public int promoteAvailable(Course course, String actor) {
        if (course == null || !Integer.valueOf(1).equals(course.getSelectionOpen())
                || Integer.valueOf(2).equals(course.getStatus())
                || (course.getSelectionEndAt() != null && LocalDateTime.now().isAfter(course.getSelectionEndAt()))) {
            return 0;
        }

        int capacity = course.getMaxStudents() == null ? 60 : course.getMaxStudents();
        int available = capacity - courseSelectionMapper.countActiveForCourse(course.getId());
        if (available <= 0) return 0;

        List<Long> nextSelectionIds = courseSelectionMapper.selectNextWaitlistedForUpdate(course.getId(), available);
        if (nextSelectionIds.isEmpty()) return 0;

        int promoted = courseSelectionMapper.promoteWaitlisted(course.getId(), nextSelectionIds, actor);
        if (promoted != nextSelectionIds.size()) {
            throw new BusinessException("候补队列状态已变化，请刷新后重试");
        }
        return promoted;
    }
}
