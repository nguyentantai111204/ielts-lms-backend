package com.ntt.lms.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestListDTO {
    private Integer testId;
    private String title;
    private String description;
    private Boolean isActive;
    private String type;
}
