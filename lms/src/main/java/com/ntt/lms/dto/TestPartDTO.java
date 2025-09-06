// TestPartDto.java
package com.ntt.lms.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TestPartDTO {
    private Integer partId;
    private Integer partNumber;
    private Integer orderNumber;
    private String title;
    private String description;
    private String audioUrl;
    private String passage;
    private List<TestQuestionGroupDTO> groups;
}
