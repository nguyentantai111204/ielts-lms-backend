package com.ntt.lms.controller;

import com.ntt.lms.dto.StudentDto;
import com.ntt.lms.pojo.Enrollment;
import com.ntt.lms.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollment")
@RequiredArgsConstructor
public class   EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/enroll")
    public ResponseEntity<String> enrollInCourse(@RequestBody Enrollment enrollment){
        try {
            enrollmentService.enrollInCourse(enrollment);
            return ResponseEntity.ok("Sinh viên tham gia khóa học thành công");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/view_enrolled_students/{courseId}")
    public ResponseEntity<?> viewEnrolledStudents(@PathVariable int courseId){
        try {
            List<StudentDto> students = enrollmentService.viewEnrolledStudents(courseId);
            return ResponseEntity.ok(students);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/remove_enrolled_student/student_id/{studentId}/course_id/{courseId}")
    public ResponseEntity<String> removeEnrolledStudent(@PathVariable int studentId, @PathVariable int courseId){
        try {
            enrollmentService.removeEnrolledStudent(courseId, studentId);
            return ResponseEntity.ok("Xóa sinh viên khỏi khóa học thành công");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
