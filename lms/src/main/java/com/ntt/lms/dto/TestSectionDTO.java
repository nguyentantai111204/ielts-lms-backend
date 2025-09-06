// TestSectionDto.java
package com.ntt.lms.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TestSectionDTO {
    private Integer sectionId;
    private String sectionType;
    private Integer orderNumber;
    private Integer durationMinutes;
    private List<TestPartDTO> parts;
}
