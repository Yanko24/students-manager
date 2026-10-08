package com.example.studentsmanager;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.example.studentsmanager.mapper")
@EnableScheduling
public class StudentsManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(StudentsManagerApplication.class, args);
    }
}
