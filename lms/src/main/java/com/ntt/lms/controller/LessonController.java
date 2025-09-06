package com.ntt.lms.controller;

import com.ntt.lms.dto.LessonDto;
import com.ntt.lms.dto.LessonPreviewDto;
import com.ntt.lms.dto.StudentDto;
import com.ntt.lms.pojo.Lesson;
import com.ntt.lms.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/lesson")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @PostMapping("/add_lesson")
    public ResponseEntity<String> addLesson(@RequestBody Lesson lesson){
        try{
            this.lessonService.addLesson(lesson);
            return ResponseEntity.ok("Them bai hoc thanh cong");
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get_all_lessons/{courseId}")
    public ResponseEntity<?> getAllLessons(@PathVariable int courseId){
        try{
            List<LessonDto>lessons =  this.lessonService.getLessonsByCourseId(courseId);
            return ResponseEntity.ok(lessons);
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/preview-lessons/{courseId}")
    public ResponseEntity<?> getLessonPreview(@PathVariable int courseId) {
        try {
            List<LessonPreviewDto> lessons = lessonService.getCourseLessonPreview(courseId);
            return ResponseEntity.ok(lessons);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    @GetMapping("/lesson_id/{lessonId}")
    public ResponseEntity<?> getLessonById(@PathVariable int lessonId){
        try{
            LessonDto lesson =  this.lessonService.getLessonById(lessonId);
            return ResponseEntity.ok(lesson);
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update/lesson_id/{lessonId}")
    public ResponseEntity<String>updateLesson(@PathVariable int lessonId, @RequestBody Lesson updateLesson){
        try {
            this.lessonService.updateLesson(lessonId, updateLesson);
            return ResponseEntity.ok("Cap nhat bai hoc thanh cong");
        }catch (IllegalArgumentException e){
           return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("delete/lesson_id/{lessonId}/course_id/{courseId}")
    public ResponseEntity<String> deleteLesson(@PathVariable int lessonId, @PathVariable int courseId){
        try {
            this.lessonService.deleteLesson(lessonId, courseId);
            return ResponseEntity.ok("Xoa bai hoc thanh cong");
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Thêm sinh viên vào bài học
    @PostMapping("/student_enter_lesson/course_id/{courseId}/lesson_id/{lessonId}/otp/{otp}")
    public ResponseEntity<?> studentEnterLesson(@PathVariable int courseId, @PathVariable int lessonId, @PathVariable String otp){
        try {
            this.lessonService.studentEnterLesson(courseId, lessonId, otp);
            return ResponseEntity.ok("Sinh vien gia nhap bai hoc thanh cong");
        }catch  (IllegalArgumentException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Điểm danh bài học
    @GetMapping("/attendances/{lessonId}")
    public ResponseEntity<List<StudentDto>> trackLessonAttendances(@PathVariable int lessonId) {
        try {
            List<StudentDto> students = lessonService.lessonAttendance(lessonId);
            return ResponseEntity.ok(students);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Collections.emptyList());
        }
    }

    @GetMapping("/attendance-status/{lessonId}")
    public ResponseEntity<?> getAttendanceStatus(@PathVariable int lessonId) {
        try {
            return ResponseEntity.ok(lessonService.getAttendanceStatus(lessonId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
