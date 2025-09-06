package com.ntt.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestAnswerDTO {
    private Integer answerId;
    private Integer questionId;
    private String questionText;
    private String questionType;
    private String userAnswer;
    private List<String> correctAnswers;
    private Boolean isCorrect;
    private Double score;
    private LocalDateTime answeredAt;
}
