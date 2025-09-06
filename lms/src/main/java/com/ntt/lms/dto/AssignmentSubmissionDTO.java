package com.ntt.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentSubmissionDTO {
    private int submissionId;
    private String feedback;
    private String filePath;
    private Float  grade;
    private String submissionText;
    private Date submitAt;
    private int assignmentId;
    private StudentDto studentDto;
}
