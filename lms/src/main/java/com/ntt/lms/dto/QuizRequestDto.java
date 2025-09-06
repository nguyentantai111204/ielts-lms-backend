package com.ntt.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizRequestDto {
    private String title;
    private int questionCount;
    private int typeId;
    private List<QuestionDto> questionList;
    private boolean randomized;
    private int course_id;
    private int lessonId;
}
