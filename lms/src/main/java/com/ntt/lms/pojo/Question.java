package com.ntt.lms.pojo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private int questionId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "quiz_id", referencedColumnName = "quiz_id")
    private Quiz quiz;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "level_id", referencedColumnName = "level_id")
    private QuestionLevel questionLevel;

    @Column(name = "question_text", nullable = false)
    private String questionText;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "type_id", referencedColumnName = "type_id")
    private QuestionType questionType;

    @Column(name = "options", columnDefinition = "json")
    private String options;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "course_id", referencedColumnName = "course_id")
    private Course courseId;

    @Column(name = "correct_answer", nullable = false)
    private String correctAnswer;


}
