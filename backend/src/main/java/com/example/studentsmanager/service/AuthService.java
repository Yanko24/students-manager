package com.example.studentsmanager.service;

import com.example.studentsmanager.model.dto.auth.LoginRequest;
import com.example.studentsmanager.model.dto.auth.LoginResponse;
import com.example.studentsmanager.model.dto.auth.ChangePasswordRequest;
import com.example.studentsmanager.model.vo.auth.AccountProfileVO;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    AccountProfileVO getCurrentProfile(String username);
    void changePassword(String username, ChangePasswordRequest request);
}
