package com.ntt.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
public class CourseDto {
    private int courseId;
    private String courseName;
    private String description;
    private int duration;
    private String instructorName;
    private int instructorId;
    private String media;


    public CourseDto(int courseId, String courseName, String description, int duration, String media ,String instructorName, int instructorId) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.description = description;
        this.duration = duration;
        this.media = media;
        this.instructorName = instructorName;
        this.instructorId = instructorId;
    }
}
