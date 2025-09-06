package com.ntt.lms.pojo;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "test_submission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer submissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;



    private Double totalScore;

    // điểm từng kỹ năng
    private Double listeningScore;
    private Double readingScore;


    @Enumerated(EnumType.STRING)
    private SubmissionStatus status;

}
