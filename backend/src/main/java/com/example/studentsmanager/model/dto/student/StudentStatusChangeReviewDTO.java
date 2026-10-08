package com.example.studentsmanager.model.dto.student;

import lombok.Data;

@Data
public class StudentStatusChangeReviewDTO {
    private boolean approved;
    private String comment;
}
