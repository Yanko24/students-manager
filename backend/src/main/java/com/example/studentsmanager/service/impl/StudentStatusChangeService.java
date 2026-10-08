package com.example.studentsmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.studentsmanager.exception.BusinessException;
import com.example.studentsmanager.mapper.MajorMapper;
import com.example.studentsmanager.mapper.StudentStatusChangeMapper;
import com.example.studentsmanager.model.dto.student.StudentStatusChangeDTO;
import com.example.studentsmanager.model.dto.student.StudentStatusChangeReviewDTO;
import com.example.studentsmanager.model.entity.Major;
import com.example.studentsmanager.model.entity.Student;
import com.example.studentsmanager.model.entity.StudentStatusChangeRequest;
import com.example.studentsmanager.service.StudentService;
import com.example.studentsmanager.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import org.springframework.scheduling.annotation.Scheduled;

@Service
public class StudentStatusChangeService extends ServiceImpl<StudentStatusChangeMapper, StudentStatusChangeRequest> {
    private static final Map<String, Integer> TARGET_STATUS = Map.of(
            "SUSPENSION", 1, "RETURN", 0, "WITHDRAWAL", 2, "MAJOR_TRANSFER", 0);
    private final StudentService studentService;
    private final UserService userService;
    private final MajorMapper majorMapper;
    private final OperationAuditService auditService;
    private final SystemNotificationService notificationService;

    public StudentStatusChangeService(StudentService studentService, UserService userService, MajorMapper majorMapper,
                                      OperationAuditService auditService, SystemNotificationService notificationService) {
        this.studentService = studentService;
        this.userService = userService;
        this.majorMapper = majorMapper;
        this.auditService = auditService;
        this.notificationService = notificationService;
    }

    public List<StudentStatusChangeRequest> mine(String username) {
        Student student = currentStudent(username);
        return list(new LambdaQueryWrapper<StudentStatusChangeRequest>().eq(StudentStatusChangeRequest::getStudentId, student.getId())
                .orderByDesc(StudentStatusChangeRequest::getCreateTime));
    }

    public Page<StudentStatusChangeRequest> page(long page, long size, String status) {
        return baseMapper.selectAdminPage(
                new Page<>(Math.max(1, page), Math.min(100, Math.max(1, size))),
                status == null || status.isBlank() ? null : status.trim());
    }

    @Transactional(rollbackFor = Exception.class)
    public StudentStatusChangeRequest apply(String username, StudentStatusChangeDTO dto) {
        Student student = currentStudent(username);
        if (student.getStatus() == null || student.getStatus() == 2 || student.getStatus() == 3) {
            throw new BusinessException("当前学籍状态不能提交异动申请");
        }
        if (dto.getChangeType() == null || !TARGET_STATUS.containsKey(dto.getChangeType())) throw new BusinessException("学籍异动类型无效");
        if (dto.getEffectiveDate() == null) throw new BusinessException("请填写生效日期");
        if (dto.getReason() == null || dto.getReason().isBlank() || dto.getReason().trim().length() > 1000) throw new BusinessException("请填写异动原因（不超过1000字）");
        if ("SUSPENSION".equals(dto.getChangeType()) && student.getStatus() != 0) throw new BusinessException("只有在读学生可以申请休学");
        if ("RETURN".equals(dto.getChangeType()) && student.getStatus() != 1) throw new BusinessException("只有休学学生可以申请复学");
        if ("WITHDRAWAL".equals(dto.getChangeType()) && student.getStatus() != 0 && student.getStatus() != 1) throw new BusinessException("当前状态不能申请退学");
        if ("MAJOR_TRANSFER".equals(dto.getChangeType())) {
            if (student.getStatus() != 0) throw new BusinessException("只有在读学生可以申请转专业");
            if (dto.getTargetMajorCode() == null || dto.getTargetMajorCode().isBlank()) throw new BusinessException("请选择转入专业");
            Major target = findTargetMajor(dto.getTargetMajorCode(), dto.getTargetClassNo(), student.getGrade());
            if (target == null) throw new BusinessException("转入专业当前不可用，或没有该年级班级");
            if (target.getCode().equals(student.getMajorCode())) throw new BusinessException("转入专业与当前专业相同");
        }
        if (count(new LambdaQueryWrapper<StudentStatusChangeRequest>().eq(StudentStatusChangeRequest::getStudentId, student.getId())
                .and(w -> w.eq(StudentStatusChangeRequest::getStatus, "PENDING")
                        .or().eq(StudentStatusChangeRequest::getStatus, "APPROVED").isNull(StudentStatusChangeRequest::getAppliedAt))) > 0) {
            throw new BusinessException("你有待审核或尚未生效的学籍异动申请");
        }
        StudentStatusChangeRequest request = new StudentStatusChangeRequest();
        request.setStudentId(student.getId()); request.setChangeType(dto.getChangeType()); request.setCurrentStatus(student.getStatus());
        request.setTargetStatus(TARGET_STATUS.get(dto.getChangeType()));
        request.setTargetMajorCode("MAJOR_TRANSFER".equals(dto.getChangeType()) ? dto.getTargetMajorCode().trim() : null);
        request.setTargetClassNo("MAJOR_TRANSFER".equals(dto.getChangeType()) ? dto.getTargetClassNo().trim() : null);
        request.setEffectiveDate(dto.getEffectiveDate()); request.setReason(dto.getReason().trim()); request.setStatus("PENDING");
        save(request);
        auditService.record(username, "STUDENT_STATUS_APPLY", "STUDENT_STATUS_CHANGE", request.getId(),
                "提交" + request.getChangeType() + "申请：" + request.getReason());
        notificationService.notifyAdmins("STUDENT_STATUS_REVIEW", "收到学籍异动申请", "有新的学籍异动申请待审核，申请编号：" + request.getId());
        return request;
    }

    @Transactional(rollbackFor = Exception.class)
    public StudentStatusChangeRequest review(Long id, StudentStatusChangeReviewDTO dto, String reviewer) {
        StudentStatusChangeRequest request = getById(id);
        if (request == null || !"PENDING".equals(request.getStatus())) throw new BusinessException("申请不存在或已处理");
        if (dto.getComment() == null || dto.getComment().isBlank() || dto.getComment().trim().length() > 1000) throw new BusinessException("请填写审核意见（不超过1000字）");
        Student student = studentService.getById(request.getStudentId());
        if (student == null || !request.getCurrentStatus().equals(student.getStatus())) throw new BusinessException("学生当前学籍状态已变化，请刷新申请后再审核");
        request.setReviewComment(dto.getComment().trim()); request.setReviewedBy(reviewer);
        request.setReviewedAt(java.time.LocalDateTime.now()); request.setStatus(dto.isApproved() ? "APPROVED" : "REJECTED");
        if (dto.isApproved()) {
            if ("MAJOR_TRANSFER".equals(request.getChangeType())) {
                Major target = findTargetMajor(request.getTargetMajorCode(), request.getTargetClassNo(), student.getGrade());
                if (target == null) throw new BusinessException("转入专业已不可用，不能批准");
                student.setMajorCode(target.getCode());
                student.setClassNo(target.getClassNo());
            }
            if (!request.getEffectiveDate().isAfter(java.time.LocalDate.now())) {
                applyApprovedChange(request, student, reviewer);
                request.setAppliedAt(java.time.LocalDateTime.now());
            }
        }
        updateById(request);
        auditService.record(reviewer, dto.isApproved() ? "STUDENT_STATUS_APPROVE" : "STUDENT_STATUS_REJECT",
                "STUDENT_STATUS_CHANGE", request.getId(), request.getReviewComment());
        var studentUser = userService.getById(student.getUserId());
        if (studentUser != null) notificationService.notifyUser(studentUser.getId(), "STUDENT_STATUS_REVIEW",
                dto.isApproved() ? "学籍异动申请已通过" : "学籍异动申请未通过",
                "你的" + request.getChangeType() + "申请已" + (dto.isApproved() ? "通过" : "拒绝") + "。审核意见：" + request.getReviewComment());
        return request;
    }

    @Scheduled(cron = "0 10 0 * * *")
    @Transactional(rollbackFor = Exception.class)
    public void applyDueApprovedChanges() {
        List<StudentStatusChangeRequest> due = list(new LambdaQueryWrapper<StudentStatusChangeRequest>()
                .eq(StudentStatusChangeRequest::getStatus, "APPROVED")
                .isNull(StudentStatusChangeRequest::getAppliedAt)
                .le(StudentStatusChangeRequest::getEffectiveDate, java.time.LocalDate.now()));
        for (StudentStatusChangeRequest request : due) {
            Student student = studentService.getById(request.getStudentId());
            if (student == null || !request.getCurrentStatus().equals(student.getStatus())) continue;
            try {
                applyApprovedChange(request, student, request.getReviewedBy());
                request.setAppliedAt(java.time.LocalDateTime.now());
                updateById(request);
            } catch (BusinessException ignored) {
                // Keep the approved request unapplied so an administrator can resolve the changed major or student record.
            }
        }
    }

    private void applyApprovedChange(StudentStatusChangeRequest request, Student student, String actor) {
        if ("MAJOR_TRANSFER".equals(request.getChangeType())) {
            Major target = findTargetMajor(request.getTargetMajorCode(), request.getTargetClassNo(), student.getGrade());
            if (target == null) throw new BusinessException("转入专业已不可用，无法生效");
            student.setMajorCode(target.getCode()); student.setClassNo(target.getClassNo());
        }
        student.setStatus(request.getTargetStatus()); student.setUpdateBy(actor);
        studentService.updateById(student);
    }

    private Major findTargetMajor(String majorCode, String classNo, String grade) {
        if (majorCode == null || majorCode.isBlank() || classNo == null || classNo.isBlank()) return null;
        return majorMapper.selectOne(new LambdaQueryWrapper<Major>().eq(Major::getCode, majorCode.trim())
                .eq(Major::getClassNo, classNo.trim()).eq(Major::getGrade, grade).eq(Major::getStatus, 0));
    }

    private Student currentStudent(String username) {
        var user = userService.findByUsername(username);
        if (user == null) throw new BusinessException("当前账号不存在");
        Student student = studentService.getOne(new LambdaQueryWrapper<Student>().eq(Student::getUserId, user.getId()));
        if (student == null) throw new BusinessException("当前账号未关联学生档案");
        return student;
    }
}
