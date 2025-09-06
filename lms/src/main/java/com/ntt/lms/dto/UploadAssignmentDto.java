package com.ntt.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadAssignmentDto {
    private int assignmentId;
    private String assignmentTitle;
    private String assignmentDescription;
    private int courseId;
    private int lessonId;
    private String submissionText;
    private MultipartFile filePath;

}
