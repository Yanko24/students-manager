package com.example.studentsmanager.constant;

public interface ResultCode {
    // 通用状态码
    int SUCCESS = 200;
    int ERROR = 500;
    int UNAUTHORIZED = 401;
    int FORBIDDEN = 403;
    int NOT_FOUND = 404;
    int BAD_REQUEST = 400;
    int INTERNAL_SERVER_ERROR = 500;

    // 业务状态码
    int USERNAME_EXISTS = 1001;
    int STUDENT_NUMBER_EXISTS = 1002;
    int INVALID_CREDENTIALS = 1003;
} 