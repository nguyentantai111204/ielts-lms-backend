package com.ntt.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizResponseDto {
    private int quizId;

    private String title;

    private Date creation_date;

    private List<QuestionDto> questionList;

    private int course_id;
    private int lessonId;

    public QuizResponseDto(int quizId, String title, Date creationDate, int courseId, int lessonId) {
        this.quizId = quizId;
        this.title = title;
        this.creation_date = creationDate;
        this.course_id = courseId;
        this.lessonId = lessonId;
    }

}
