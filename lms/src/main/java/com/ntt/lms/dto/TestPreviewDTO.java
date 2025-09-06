package com.ntt.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestPreviewDTO {
    private Integer testId;
    private String title;
    private String description;
    private Integer totalDurationMinutes;
    private String type;
    private List<TestSectionPreviewDTO> sections;
}
