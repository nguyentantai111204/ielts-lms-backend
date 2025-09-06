package com.ntt.lms.controller;

import com.ntt.lms.dto.ChangePasswordRequest;
import com.ntt.lms.dto.NotificationDTO;
import com.ntt.lms.dto.StudentInfoDTO;
import com.ntt.lms.dto.StudentProfileDTO;
import com.ntt.lms.pojo.Student;
import com.ntt.lms.repository.UsersRepository;
import com.ntt.lms.service.NotificationsService;
import com.ntt.lms.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/student")
public class StudentController {

    private final StudentService studentService;
    private final NotificationsService notificationsService;

    private final UsersRepository usersRepository;

    @PutMapping("/update_profile/{studentId}")
    public ResponseEntity<String> updateStudent(
            @PathVariable int studentId,
            @ModelAttribute StudentInfoDTO studentDTO,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        try {
            studentService.updateProfile(studentId, studentDTO, file);
            return ResponseEntity.ok("Cập nhật hồ sơ thành công");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi server: " + e.getMessage());
        }
    }


    @PutMapping("/update_avatar/{studentId}")
    public ResponseEntity<String> updateAvatar(
            @PathVariable int studentId,
            @RequestParam("file") MultipartFile file) {
        try {
            String avatarUrl = studentService.updateAvatar(studentId, file);
            return ResponseEntity.ok(avatarUrl);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Lỗi khi upload file: " + e.getMessage());
        }
    }

    @GetMapping("/get_all_student")
    public ResponseEntity<List<StudentInfoDTO>> getAllStudents() {
        try {
            List<StudentInfoDTO> students = studentService.getAllStudents();
            return ResponseEntity.ok(students);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete_student/{studentId}")
    public ResponseEntity<?> deleteStudent(@PathVariable int studentId) {
        try {
            studentService.deleteStudent(studentId);
            return ResponseEntity.ok().body("Xóa học sinh thành công");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi server: " + e.getMessage());
        }
    }


    @GetMapping("/profile/{studentId}")
    public ResponseEntity<StudentProfileDTO> getProfile(@PathVariable int studentId) {
        try {
            return ResponseEntity.ok(studentService.getStudentProfile(studentId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/allnotifications/{userId}")
    public List<NotificationDTO> getAllNotifications(@PathVariable int userId)  {
        return notificationsService.getAllNotifications(userId);
    }


    @GetMapping("/unreadnotifications/{userId}")
    public List<NotificationDTO> getUnreadNotifications(@PathVariable int userId) {
        return notificationsService.getAllUnreadNotifications(userId );
    }

    @PutMapping("/{notificationId}/is_read")
    public ResponseEntity<String> markAsRead(@PathVariable int notificationId) {
        notificationsService.markAsRead(notificationId);
        return ResponseEntity.ok("Thông báo đã được đánh dấu là đã đọc");
    }

    @PutMapping("/change_password/{studentId}")
    public ResponseEntity<String> changePassword(
            @PathVariable int studentId,
            @RequestBody ChangePasswordRequest request
    ) {
        try {
            studentService.changePassword(studentId, request);
            return ResponseEntity.ok("Đổi mật khẩu thành công!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi server: " + e.getMessage());
        }
    }

    @GetMapping("/count_student")
    public ResponseEntity<?> countAllStudents() {
        try {
            long count = studentService.countStudent();
            return ResponseEntity.ok(count); // Trả về số lượng student
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server: " + e.getMessage());
        }
    }

    @GetMapping("/student-growth")
    public ResponseEntity<?> getStudentGrowth() {
        try {
            return ResponseEntity.ok(studentService.getStudentRegistrationStatsLast6Months());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server: " + e.getMessage());
        }
    }




}
