package com.ntt.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDto {
    private int question_id;
    private String question_text;
    private int type;
    private String options;
    private int course_id;
    private String correct_answer;
}
