package com.ntt.lms.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestDTO {
    private Integer testId;
    private String title;
    private String description;
    private String type;
    private Integer durationMinutes;
    private LocalDateTime createdAt;
    private Boolean isActive;
    private List<TestSectionDTO> sections;

}
