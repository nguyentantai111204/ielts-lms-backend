package com.ntt.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubLessonDTO {
    private int subLessonDTO;
    private String subName;
    private int time;
    private int lessonId;
}
