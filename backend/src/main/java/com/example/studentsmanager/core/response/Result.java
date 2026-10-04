package com.example.studentsmanager.core.response;

import com.example.studentsmanager.constant.ResultCode;
import com.example.studentsmanager.constant.ResultMessage;
import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    private Result() {
    }

    private Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS, ResultMessage.SUCCESS);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS, ResultMessage.SUCCESS, data);
    }

    public static <T> Result<T> error() {
        return new Result<>(ResultCode.ERROR, ResultMessage.ERROR);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(ResultCode.ERROR, message);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message);
    }

    public static <T> Result<T> error(Integer code, String message, T data) {
        return new Result<>(code, message, data);
    }
} 