package com.example.studentsmanager.service.impl;

import com.example.studentsmanager.model.dto.auth.LoginRequest;
import com.example.studentsmanager.model.dto.auth.LoginResponse;
import com.example.studentsmanager.model.dto.auth.ChangePasswordRequest;
import com.example.studentsmanager.model.entity.User;
import com.example.studentsmanager.model.entity.Student;
import com.example.studentsmanager.model.entity.Teacher;
import com.example.studentsmanager.model.vo.auth.AccountProfileVO;
import com.example.studentsmanager.model.vo.student.StudentVO;
import com.example.studentsmanager.service.StudentService;
import com.example.studentsmanager.service.TeacherService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.studentsmanager.exception.BusinessException;
import com.example.studentsmanager.constant.ResultCode;
import com.example.studentsmanager.service.AuthService;
import com.example.studentsmanager.service.UserService;
import com.example.studentsmanager.utils.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final StudentService studentService;
    private final TeacherService teacherService;

    @Override
    public LoginResponse login(LoginRequest request) {
        // 先检查用户是否存在且状态正常
        User user = userService.findByUsername(request.getUsername());
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        if (user.getIsDeleted() == 1) {
            throw new BadCredentialsException("用户已被删除");
        }
        if (user.getStatus() == 1) {
            throw new BadCredentialsException("用户已被禁用");
        }

        try {
            // 进行认证
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // Rehash successfully authenticated legacy BCrypt credentials using PBKDF2-HMAC-SM3.
            if (passwordEncoder.upgradeEncoding(user.getPassword())) {
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                userService.updateById(user);
            }

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);

            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setUsername(user.getUsername());
            response.setName(user.getRealName());
            response.setUserId(user.getId());
            response.setRole(user.getRole().toLowerCase());
            response.setMustChangePassword(Boolean.TRUE.equals(user.getMustChangePassword()));
            
            return response;
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("用户名或密码错误");
        }
    }

    @Override
    public AccountProfileVO getCurrentProfile(String username) {
        User user = userService.findByUsername(username);
        if (user == null) throw new UsernameNotFoundException("用户不存在");

        AccountProfileVO profile = new AccountProfileVO();
        profile.setUserId(user.getId());
        profile.setUsername(user.getUsername());
        profile.setRealName(user.getRealName());
        profile.setRole(user.getRole() == null ? null : user.getRole().toLowerCase());
        profile.setGender(user.getGender());
        profile.setPhone(user.getPhone());
        profile.setEmail(user.getEmail());
        profile.setAccountStatus(user.getStatus());
        profile.setAccountCreatedAt(user.getCreateTime());

        if ("student".equalsIgnoreCase(user.getRole())) {
            Student student = studentService.getOne(new LambdaQueryWrapper<Student>()
                    .eq(Student::getUserId, user.getId()));
            if (student == null) throw new BusinessException("学生档案不存在");
            StudentVO detail = studentService.getStudentById(student.getId());
            profile.setStudentNo(detail.getStudentNo());
            profile.setBirthDate(detail.getBirthDate());
            profile.setAdmissionDate(detail.getAdmissionDate());
            profile.setAddress(detail.getAddress());
            profile.setMajorCode(detail.getMajorCode());
            profile.setMajorName(detail.getMajorName());
            profile.setCollegeName(detail.getCollegeName());
            profile.setGrade(detail.getGrade());
            profile.setClassNo(detail.getClassNo());
            profile.setStudentStatus(detail.getStatus());
            profile.setStudentStatusText(detail.getStatusText());
        } else if ("teacher".equalsIgnoreCase(user.getRole())) {
            Teacher teacher = teacherService.getOne(new LambdaQueryWrapper<Teacher>()
                    .eq(Teacher::getUserId, user.getId()));
            if (teacher == null) throw new BusinessException("教师档案不存在");
            profile.setTeacherNo(teacher.getTeacherNumber());
            profile.setDepartment(teacher.getDepartment());
            profile.setTitle(teacher.getTitle());
            profile.setHireDate(teacher.getHireDate());
            profile.setTeacherStatus(teacher.getStatus());
        }
        return profile;
    }

    @Override
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "当前密码不正确");
        }
        if (request.getNewPassword().equals(request.getCurrentPassword())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "新密码不能与当前密码相同");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "两次输入的新密码不一致");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setMustChangePassword(false);
        userService.updateById(user);
    }
}
