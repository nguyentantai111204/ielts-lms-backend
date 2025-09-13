package com.ntt.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class HistoryTestDTO {
    private int testSubmissionId;
    private String testName;
    private int testId;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
    private int userId;
    private double totalScore;
}
