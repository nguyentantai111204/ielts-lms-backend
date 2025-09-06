package com.ntt.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonDto {
    private int lessonId;
    private int courseId;
    private String lessonName;
    private String lessonDescription;
    private int lessonOrder;
    private String OTP;
    private String content;
    private Date creationTime;
}
