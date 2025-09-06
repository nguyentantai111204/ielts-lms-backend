package com.ntt.lms.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestQuestionPreviewDTO {
    private Integer questionId;
    private Integer questionNumber;
    private String content;
    private String questionType;
    private List<String> options;
    private List<String> correctAnswers;
    private List<String> userAnswers;
}
