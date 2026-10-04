package com.example.studentsmanager.utils;

import com.example.studentsmanager.security.Sm3Pbkdf2PasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        Sm3Pbkdf2PasswordEncoder encoder = new Sm3Pbkdf2PasswordEncoder();
        System.out.println(encoder.encode("xiaoer"));
    }
} 
