package com.ntt.lms.controller;

import com.ntt.lms.dto.CourseDto;
import com.ntt.lms.dto.CourseOutcomeDto;
import com.ntt.lms.dto.CourseUpdateRequest;
import com.ntt.lms.pojo.Course;
import com.ntt.lms.pojo.CourseOutcome;
import com.ntt.lms.pojo.Location;
import com.ntt.lms.service.CourseOutcomeService;
import com.ntt.lms.service.CourseService;
import com.ntt.lms.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    private final CourseOutcomeService courseOutcomeService;

    @PostMapping("/add_course")
    public ResponseEntity<String> addCourse(
            @RequestPart("course") CourseUpdateRequest addRequest,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            courseService.addCourse(addRequest, file);
            return ResponseEntity.ok("Tạo khóa học thành công.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/course_id/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable int id) {
        try {
            CourseDto courseDTO = courseService.getCourseById(id);
            return ResponseEntity.ok(courseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{slug}")
    public ResponseEntity<?> getCourseBySlug(@PathVariable String slug) {
        try {
            CourseDto courseDto = courseService.getCourseDtoBySlug(slug);
            return ResponseEntity.ok(courseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/count_course")
    public ResponseEntity<?> countCourse() {
        try {
            long count = courseService.countCourse();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server: " + e.getMessage());
        }
    }



    @GetMapping("/all_courses")
    public ResponseEntity<?> getAllCourses() {
        try {
            List<CourseDto> courseDTOList = courseService.getAllCourses();
            return ResponseEntity.ok(courseDTOList);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update/course_id/{courseId}")
    public ResponseEntity<?> updateCourse(
            @PathVariable int courseId,
            @RequestPart(value = "course", required = false) CourseUpdateRequest updatedCourse,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            courseService.updateCourse(courseId, updatedCourse, file);
            return ResponseEntity.ok("Cập nhật khóa học thành công.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Đã xảy ra lỗi: " + e.getMessage());
        }
    }



    @DeleteMapping("/delete/course_id/{courseId}")
    public ResponseEntity<String> deleteCourse(@PathVariable int courseId) {
        try {
            courseService.deleteCourse(courseId);
            return ResponseEntity.ok("Xoa khoa hoc thanh cong.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/upload_media/{courseId}")
    public ResponseEntity<String> uploadMedia(@PathVariable int courseId,
                                              @RequestParam("file") MultipartFile file) {
        try {
            courseService.uploadMediaFile(courseId, file);
            return ResponseEntity.ok("Tải tep thanh cong.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body("Loi server: " + e.getMessage());
        }
    }

    @GetMapping("/student/my-courses")
    public ResponseEntity<?> getMyCourses() {
        try {
            List<CourseDto> courses = courseService.getActiveCoursesForCurrentStudent();
            return ResponseEntity.ok(courses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/instructor/my-courses")
    public ResponseEntity<List<CourseDto>> getCoursesForCurrentInstructor() {
        List<CourseDto> courses = courseService.getCoursesForCurrentInstructor();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{courseId}/outcomes")
    public ResponseEntity<?> getCourseOutcomes(@PathVariable int courseId) {
        try {
            List<CourseOutcomeDto> outcomes = courseOutcomeService.getOutcomesByCourseId(courseId);
            return ResponseEntity.ok(outcomes);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi lấy mục tiêu khóa học.");
        }
    }





}
