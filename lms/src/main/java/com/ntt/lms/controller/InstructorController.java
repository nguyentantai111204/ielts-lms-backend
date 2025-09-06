package com.ntt.lms.controller;

import com.ntt.lms.dto.InstructorDTO;
import com.ntt.lms.dto.NotificationDTO;
import com.ntt.lms.pojo.Instructor;

import com.ntt.lms.service.InstructorService;
import com.ntt.lms.service.NotificationsService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/instructor")
public class InstructorController {

    private final InstructorService instructorService;
    private final NotificationsService notificationsService;



    @PutMapping("/update_profile/{instructorId}")
    public ResponseEntity<String> updateInstructor(
            @PathVariable int instructorId,
            @ModelAttribute InstructorDTO instructorDTO,
            @RequestParam(value = "avatarFile", required = false) MultipartFile file
    ) {
        try {
            instructorService.updateProfile(instructorId, instructorDTO, file);
            return ResponseEntity.ok("Cập nhật hồ sơ thành công.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi server: " + e.getMessage());
        }
    }


    @GetMapping("/notifications/{instructor-id}")
    public List<NotificationDTO> getAllNotifications(@PathVariable int instructotId) {
        return notificationsService.getAllNotifications(instructotId);
    }

    @GetMapping("/unreadnotifications/{instructor-id}")
    public List<NotificationDTO> getUnreadNotifications(@PathVariable int instructotId) {
        return notificationsService.getAllUnreadNotifications(instructotId);
    }

    @GetMapping("/get_all_instructors")
    public List<InstructorDTO> getAllInstructors() {
        return instructorService.getAllInstructors();
    }

    @DeleteMapping("/delete/{instructorId}")
    public ResponseEntity<?> deleteStudent(@PathVariable int instructorId) {
        try {
            instructorService.deleteInstructor(instructorId);
            return ResponseEntity.ok().body("Xóa học sinh thành công");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi server: " + e.getMessage());
        }
    }




}
