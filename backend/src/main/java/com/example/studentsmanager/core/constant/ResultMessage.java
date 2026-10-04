package com.example.studentsmanager.constant;

public class ResultMessage {
    public static final String SUCCESS = "操作成功";
    public static final String ERROR = "操作失败";
    public static final String UNAUTHORIZED = "未授权";
    public static final String FORBIDDEN = "禁止访问";
    public static final String NOT_FOUND = "记录不存在";
    public static final String BAD_REQUEST = "请求参数错误";
    public static final String INTERNAL_SERVER_ERROR = "服务器内部错误";
    public static final String RECORD_NOT_FOUND = "记录不存在";
    public static final String USERNAME_EXISTS = "用户名已存在";
    public static final String STUDENT_NUMBER_EXISTS = "学号已存在";
    public static final String STUDENT_NOT_FOUND = "学生不存在";
    public static final String STUDENT_NUMBER_EMPTY = "学号不能为空";
    public static final String STUDENT_NAME_EMPTY = "姓名不能为空";
    public static final String STUDENT_CLASS_ID_EMPTY = "班级ID不能为空";
    public static final String STUDENT_GENDER_EMPTY = "性别不能为空";
    public static final String STUDENT_PHONE_EMPTY = "手机号不能为空";
    public static final String STUDENT_EMAIL_EMPTY = "邮箱不能为空";
    public static final String STUDENT_CLASS_EMPTY = "班级不能为空";
    public static final String PAGE_INVALID = "页码不能小于1";
    public static final String SIZE_INVALID = "每页数量不能小于1";
    
    // 新增错误消息
    public static final String CLASS_NOT_FOUND = "班级不存在";
    public static final String DATE_FORMAT_ERROR = "日期格式错误，请使用 yyyy-MM-dd 格式";
    public static final String STUDENT_ADMISSION_DATE_EMPTY = "入学日期不能为空";
    public static final String STUDENT_ADD_FAILED = "添加学生失败";
    public static final String MAJOR_NOT_FOUND = "专业不存在";
    public static final String MAJOR_INFO_EMPTY = "专业信息不能为空";
    public static final String MAJOR_CODE_EMPTY = "专业代码不能为空";
    public static final String GRADE_EMPTY = "年级不能为空";
    public static final String CLASS_NO_EMPTY = "班级号不能为空";
    public static final String MAJOR_GRADE_EMPTY = "年级不能为空";
    public static final String MAJOR_CLASS_NO_EMPTY = "班级号不能为空";
    public static final String PARAM_INVALID = "参数无效";
    public static final String STUDENT_UPDATE_FAILED = "更新学生失败";
    public static final String STUDENT_DELETE_FAILED = "删除学生失败";
    public static final String STUDENT_ID_INVALID = "学生ID无效";
    public static final String STUDENT_INFO_EMPTY = "学生信息为空";
    public static final String STUDENT_UPDATE_INFO_EMPTY = "更新学生信息为空";
    public static final String STUDENT_QUERY_FAILED = "查询学生信息失败";
    public static final String STUDENT_COUNT_FAILED = "统计学生人数失败";
    public static final String STUDENT_CONVERT_FAILED = "学生信息转换失败";

    // 用户相关消息
    public static final String USER_NOT_FOUND = "用户不存在";
    public static final String USER_DELETED = "用户已被删除";
    public static final String USER_DISABLED = "用户已被禁用";
    public static final String PASSWORD_ERROR = "密码错误";
    public static final String USER_QUERY_FAILED = "查询用户信息失败";
    public static final String USER_CREATE_FAILED = "创建用户失败";
    public static final String USER_UPDATE_FAILED = "更新用户失败";
    public static final String USER_DELETE_FAILED = "删除用户失败";
    public static final String USER_ALREADY_EXISTS = "用户已存在";

    // 专业相关消息
    public static final String MAJOR_QUERY_FAILED = "查询专业信息失败";
    public static final String MAJOR_CREATE_FAILED = "创建专业失败";
    public static final String MAJOR_UPDATE_FAILED = "更新专业失败";
    public static final String MAJOR_DELETE_FAILED = "删除专业失败";
    public static final String MAJOR_ALREADY_EXISTS = "专业已存在";

    // College相关错误信息
    public static final String COLLEGE_NOT_FOUND = "学院不存在";
    public static final String COLLEGE_ALREADY_EXISTS = "学院已存在";
    public static final String COLLEGE_CREATE_FAILED = "创建学院失败";
    public static final String COLLEGE_UPDATE_FAILED = "更新学院失败";
    public static final String COLLEGE_DELETE_FAILED = "删除学院失败";
    public static final String COLLEGE_QUERY_FAILED = "查询学院信息失败";
    public static final String COLLEGE_CODE_DUPLICATE = "学院代码已存在";
} 