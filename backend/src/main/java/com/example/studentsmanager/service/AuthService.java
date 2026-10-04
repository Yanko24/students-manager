package com.example.studentsmanager.service;

import com.example.studentsmanager.model.dto.auth.LoginRequest;
import com.example.studentsmanager.model.dto.auth.LoginResponse;
import com.example.studentsmanager.model.dto.auth.ChangePasswordRequest;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    void changePassword(String username, ChangePasswordRequest request);
}
