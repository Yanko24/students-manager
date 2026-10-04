package com.example.studentsmanager.model.dto.student;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
public class StudentUpdateDTO {
    @NotBlank(message = "学号不能为空")
    private String studentNo;

    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    @NotNull(message = "性别不能为空")
    private Integer gender;

    @NotBlank(message = "电子邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "专业代码不能为空")
    private String majorCode;

    @NotBlank(message = "年级不能为空")
    private String grade;

    @NotBlank(message = "班级号不能为空")
    private String classNo;

    @NotNull(message = "入学日期不能为空")
    private LocalDate admissionDate;

    @NotNull(message = "出生日期不能为空")
    private LocalDate birthDate;

    @NotBlank(message = "地址不能为空")
    private String address;

    @NotNull(message = "状态不能为空")
    private Integer status;         // 状态：0-在读，1-休学，2-退学，3-毕业
} 