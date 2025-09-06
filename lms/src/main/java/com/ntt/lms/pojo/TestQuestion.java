package com.ntt.lms.pojo;

import jakarta.persistence.*;
import lombok.*;
import com.ntt.lms.pojo.StringListConverter;
import java.util.List;

@Entity
@Table(name = "test_question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private TestQuestionGroup group;

    @Column(name = "question_number", nullable = false)
    private Integer questionNumber;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private QuestionType questionType;


    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> options;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> correctAnswers;

}
