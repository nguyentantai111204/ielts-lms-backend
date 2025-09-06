// TestQuestionGroupDto.java
package com.ntt.lms.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TestQuestionGroupDTO {
    private Integer groupId;
    private String title;
    private String description;
    private Integer orderNumber;
    private String questionGroupText;
    private List<TestQuestionDTO> questions;
}
