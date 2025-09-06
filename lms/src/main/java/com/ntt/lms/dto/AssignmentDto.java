package com.ntt.lms.dto;

import com.ntt.lms.pojo.Assignment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentDto {
    private int assignmentId;
    private String assignmentTitle;
    private String assignmentDescription;
    private Date dueDate;
    private int courseId;
    private int lessonId;


    public AssignmentDto(Assignment a) {
        this.assignmentId = a.getAssignmentId();
        this.assignmentTitle = a.getTitle();
        this.assignmentDescription = a.getDescription();
        this.courseId = a.getCourseId().getCourseId();
        this.lessonId = a.getLessonId().getLessonId();
    }

}
