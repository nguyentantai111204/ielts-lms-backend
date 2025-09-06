package com.ntt.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestSectionPreviewDTO {
    private String sectionType;
    private Integer durationMinutes;
    private Integer totalQuestions;
    private Integer totalParts;
}