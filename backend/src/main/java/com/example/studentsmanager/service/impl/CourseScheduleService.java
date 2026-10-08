package com.example.studentsmanager.service.impl;

import com.example.studentsmanager.exception.BusinessException;
import com.example.studentsmanager.mapper.CourseScheduleMapper;
import com.example.studentsmanager.mapper.CourseSelectionMapper;
import com.example.studentsmanager.model.dto.course.CourseScheduleDTO;
import com.example.studentsmanager.model.entity.Course;
import com.example.studentsmanager.model.entity.CourseSchedule;
import com.example.studentsmanager.model.vo.course.CourseScheduleVO;
import com.example.studentsmanager.model.vo.course.CourseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseScheduleService {
    private static final List<String> PARITIES = List.of("ALL", "ODD", "EVEN");
    private final CourseScheduleMapper scheduleMapper;
    private final CourseSelectionMapper courseSelectionMapper;

    @Transactional(rollbackFor = Exception.class)
    public void replace(Course course, List<CourseScheduleDTO> schedules, Long excludeCourseId) {
        List<CourseScheduleDTO> requested = schedules == null ? Collections.emptyList() : schedules;
        if (!requested.isEmpty() && (course.getTeacherId() == null || course.getSemester() == null || course.getSemester().isBlank())) {
            throw new BusinessException("填写上课安排前，请先设置开课学期和授课教师");
        }
        List<CourseSchedule> normalized = new ArrayList<>();
        for (CourseScheduleDTO item : requested) {
            if (item == null) throw new BusinessException("排课时段不能为空");
            String parity = item.getWeekParity() == null || item.getWeekParity().isBlank()
                    ? "ALL" : item.getWeekParity().trim().toUpperCase();
            String classroom = item.getClassroom() == null ? "" : item.getClassroom().trim();
            if (item.getDayOfWeek() == null || item.getDayOfWeek() < 1 || item.getDayOfWeek() > 7
                    || item.getStartPeriod() == null || item.getEndPeriod() == null
                    || item.getStartPeriod() < 1 || item.getEndPeriod() > 12 || item.getStartPeriod() > item.getEndPeriod()
                    || item.getWeekStart() == null || item.getWeekEnd() == null
                    || item.getWeekStart() < 1 || item.getWeekEnd() > 30 || item.getWeekStart() > item.getWeekEnd()
                    || !PARITIES.contains(parity) || classroom.isBlank() || classroom.length() > 100) {
                throw new BusinessException("请检查排课时段：星期、节次、周次、单双周和教室均需填写有效值");
            }
            CourseSchedule candidate = new CourseSchedule();
            candidate.setCourseId(course.getId());
            candidate.setDayOfWeek(item.getDayOfWeek());
            candidate.setStartPeriod(item.getStartPeriod());
            candidate.setEndPeriod(item.getEndPeriod());
            candidate.setWeekStart(item.getWeekStart());
            candidate.setWeekEnd(item.getWeekEnd());
            candidate.setWeekParity(parity);
            candidate.setClassroom(classroom);
            for (CourseSchedule previous : normalized) {
                if (overlaps(candidate, previous)) throw new BusinessException("同一课程的排课时段存在时间冲突");
            }
            List<CourseSchedule> candidates = scheduleMapper.selectTeacherRoomCandidates(
                    course.getSemester(), candidate.getDayOfWeek(), course.getTeacherId(), classroom, excludeCourseId);
            for (CourseSchedule existing : candidates) {
                if (!overlaps(candidate, existing)) continue;
                throw new BusinessException(Objects.equals(existing.getClassroom(), classroom)
                        ? "教室 " + classroom + " 在该时段已被占用"
                        : "授课教师在该时段已有其他课程");
            }
            if (excludeCourseId != null) {
                for (Long studentId : courseSelectionMapper.selectActiveStudentIds(excludeCourseId)) {
                    List<CourseSchedule> studentSchedules = scheduleMapper.selectStudentCandidates(
                            studentId, course.getSemester(), candidate.getDayOfWeek(), excludeCourseId);
                    if (studentSchedules.stream().anyMatch(existing -> overlaps(candidate, existing))) {
                        throw new BusinessException("调整后的上课时间与已申请学生的其他课程冲突，无法保存排课");
                    }
                }
            }
            normalized.add(candidate);
        }

        if (excludeCourseId != null) scheduleMapper.deleteByCourseId(excludeCourseId);
        normalized.forEach(scheduleMapper::insert);
    }

    public void attach(List<CourseVO> courses) {
        if (courses == null || courses.isEmpty()) return;
        List<Long> ids = courses.stream().map(CourseVO::getId).filter(Objects::nonNull).distinct().toList();
        if (ids.isEmpty()) return;
        Map<Long, List<CourseScheduleVO>> schedules = scheduleMapper.selectByCourseIds(ids).stream()
                .collect(Collectors.groupingBy(CourseSchedule::getCourseId,
                        Collectors.mapping(this::toVO, Collectors.toList())));
        courses.forEach(course -> course.setSchedules(schedules.getOrDefault(course.getId(), List.of())));
    }

    public void attach(CourseVO course) {
        if (course == null) return;
        course.setSchedules(scheduleMapper.selectByCourseId(course.getId()).stream().map(this::toVO).toList());
    }

    public boolean hasStudentConflict(Course course, Long studentId) {
        List<CourseSchedule> target = scheduleMapper.selectByCourseId(course.getId());
        for (CourseSchedule scheduled : target) {
            List<CourseSchedule> candidates = scheduleMapper.selectStudentCandidates(
                    studentId, course.getSemester(), scheduled.getDayOfWeek(), course.getId());
            for (CourseSchedule existing : candidates) {
                if (overlaps(scheduled, existing)) return true;
            }
        }
        return false;
    }

    private CourseScheduleVO toVO(CourseSchedule schedule) {
        CourseScheduleVO vo = new CourseScheduleVO();
        vo.setId(schedule.getId()); vo.setDayOfWeek(schedule.getDayOfWeek());
        vo.setStartPeriod(schedule.getStartPeriod()); vo.setEndPeriod(schedule.getEndPeriod());
        vo.setWeekStart(schedule.getWeekStart()); vo.setWeekEnd(schedule.getWeekEnd());
        vo.setWeekParity(schedule.getWeekParity()); vo.setClassroom(schedule.getClassroom());
        return vo;
    }

    private boolean overlaps(CourseSchedule first, CourseSchedule second) {
        if (!Objects.equals(first.getDayOfWeek(), second.getDayOfWeek())) return false;
        if (first.getStartPeriod() > second.getEndPeriod() || second.getStartPeriod() > first.getEndPeriod()) return false;
        if (first.getWeekStart() > second.getWeekEnd() || second.getWeekStart() > first.getWeekEnd()) return false;
        String firstParity = first.getWeekParity() == null ? "ALL" : first.getWeekParity();
        String secondParity = second.getWeekParity() == null ? "ALL" : second.getWeekParity();
        return "ALL".equals(firstParity) || "ALL".equals(secondParity) || firstParity.equals(secondParity);
    }
}
