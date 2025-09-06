package com.ntt.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveAssignmentDto {
    private int studentId;
    private int assignmentId;
    private String feedback;
}
