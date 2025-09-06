package com.ntt.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LessonPreviewDto {
    private int lessonId;
    private String title;
    private int orderNumber;
    private List<SubLessonPreviewDto> subLessons;
}
