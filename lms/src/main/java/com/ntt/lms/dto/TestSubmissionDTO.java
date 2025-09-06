package com.ntt.lms.dto;

import com.ntt.lms.pojo.SubmissionStatus;
import com.ntt.lms.pojo.TestSubmission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestSubmissionDTO {
    private Integer submissionId;
    private Integer testId;
    private String testTitle;
    private Integer userId;
    private String userName;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
    private SubmissionStatus status;
    private Double totalScore;
    private Double listeningScore;
    private Double readingScore;
    private Integer totalQuestions;
    private Integer answeredQuestions;
    private Integer correctAnswers;
    private List<TestAnswerDTO> answers;


    public TestSubmissionDTO(TestSubmission submission) {
        this.submissionId = submission.getSubmissionId();
        this.testId = submission.getTest().getTestId();
        this.testTitle = submission.getTest().getTitle();
        this.userId = submission.getUser().getUserId();
        this.userName = submission.getUser().getEmail(); // hoặc getFullName() tùy entity
        this.startedAt = submission.getStartedAt();
        this.submittedAt = submission.getSubmittedAt();
        this.status = submission.getStatus();
        this.totalScore = submission.getTotalScore();
        this.listeningScore = submission.getListeningScore();
        this.readingScore = submission.getReadingScore();
        // các trường thống kê khác nếu cần
    }

}