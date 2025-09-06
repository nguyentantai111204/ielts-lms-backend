package com.ntt.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizGradingDTO {
    private int gradeId;
    private int score;
    private int quizId;
    private StudentDto student;
}
