package com.ntt.lms.controller;

import com.ntt.lms.dto.*;
import com.ntt.lms.service.AssignmentService;
import com.ntt.lms.service.NotificationsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assignment")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final NotificationsService notificationsService;

    @PostMapping("/add_assignment")
    public ResponseEntity<String> addAssignment(@RequestBody AssignmentDto assignment)
    {
        try {
            assignmentService.addAssignment(assignment);
            return ResponseEntity.ok("Chủ đề đã tạo thành công");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<List<AssignmentDto>> getAssignmentsByLessonId(@PathVariable int lessonId) {
        List<AssignmentDto> assignments = assignmentService.getAssignmentsByLessonId(lessonId);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/{assignmentId}")
    public ResponseEntity<?> getAssignmentById(@PathVariable int assignmentId) {
        try {
            Map<String, Object> assignment = assignmentService.getAssignmentById(assignmentId);
            return ResponseEntity.ok(assignment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }



    @PostMapping(value = "/uploadAssignment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAssignment(
            @ModelAttribute UploadAssignmentDto assignmentRequest) {
        try {
            assignmentService.uploadAssignment(assignmentRequest);
            return ResponseEntity.ok("Bài làm đã được tải lên");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi server: " + e.getMessage());
        }
    }




    @PutMapping("/gradeAssignment")
    public ResponseEntity<String> gradeAssignment(@RequestBody GradeAssignmentDto gradeAssignmentDto){
        try {
            assignmentService.gradeAssignment(gradeAssignmentDto.getStudentId(), gradeAssignmentDto.getAssignmentId(), gradeAssignmentDto.getGrade());
            String message = "Điểm bài tập "+gradeAssignmentDto.getAssignmentId()+" đã được chấm điểm";
            notificationsService.sendNotification(message, gradeAssignmentDto.getStudentId());
            return ResponseEntity.ok("Bài tập đã được chấm điểm thành công.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/saveAssignmentFeedback")
    public ResponseEntity<String> saveAssignmentFeedback(@RequestBody SaveAssignmentDto saveAssignmentDto){
        try {
            assignmentService.saveAssignmentFeedback(saveAssignmentDto.getStudentId(), saveAssignmentDto.getAssignmentId(), saveAssignmentDto.getFeedback());
            return ResponseEntity.ok("Phản hồi về bài tập đã được lưu thành công.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/getFeedback")
    public ResponseEntity<String> getFeedback(@RequestBody GetFeedbackDto getFeedbackDto){
        try {
            return ResponseEntity.ok(assignmentService.getFeedback(getFeedbackDto.getAssignmentId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/submissions/{assignmentId}")
    public ResponseEntity<?> trackAssignmentSubmissions(@PathVariable int assignmentId) {
        try {
            List<AssignmentSubmissionDTO> submissions = assignmentService.assignmentSubmissions(assignmentId);
            return ResponseEntity.ok(submissions);
        } catch (IllegalArgumentException e) {
            // Trả về object JSON chứa message
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi server: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

}
