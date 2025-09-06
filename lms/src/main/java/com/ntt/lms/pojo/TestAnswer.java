package com.ntt.lms.pojo;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "test_answer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer answerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private TestSubmission submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private TestQuestion question;


    @Column(columnDefinition = "TEXT")
    private String userAnswer;


    private Boolean isCorrect;

 
    private Double score;

    // feedback cho Writing/Speaking
    @Column(columnDefinition = "TEXT")
    private String feedback;

    // thời điểm trả lời (optional, phục vụ thống kê)
    private LocalDateTime answeredAt;
}
