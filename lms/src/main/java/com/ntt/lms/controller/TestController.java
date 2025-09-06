package com.ntt.lms.controller;

import com.ntt.lms.dto.TestDTO;
import com.ntt.lms.dto.TestListDTO;
import com.ntt.lms.dto.TestPreviewDTO;
import com.ntt.lms.pojo.Test;
import com.ntt.lms.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    // Lấy tất cả test
    @GetMapping
    public List<TestListDTO> getAllTests() {
        return testService.getAllTests();
    }

    // Lấy test theo id
    @GetMapping("/{id}")
    public ResponseEntity<TestDTO> getTest(@PathVariable int id) {
        TestDTO test = testService.getTestById(id);
        if (test == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(test);
    }

    // Lấy preview test
    @GetMapping("/{testId}/preview")
    public ResponseEntity<TestPreviewDTO> getTestPreview(@PathVariable int testId) {
        TestPreviewDTO preview = testService.getTestPreview(testId);
        if (preview == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(preview);
    }

    // Lấy tất cả test preview
    @GetMapping("/all_tests_preview")
    public ResponseEntity<List<TestPreviewDTO>> getAllTestPreview() {
        return ResponseEntity.ok(testService.getAllTestPreviews());
    }

    // Đếm số lượng test
    @GetMapping("/count_test")
    public ResponseEntity<Long> countTests() {
        return ResponseEntity.ok(testService.countTest());
    }

    // Thêm test mới
    @PostMapping("/add")
    public ResponseEntity<TestListDTO> addTest(@RequestBody Test test) {
        try {
            TestListDTO saved = testService.addTest(test);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Sửa test
    @PutMapping("/update/{id}")
    public ResponseEntity<TestListDTO> updateTest(
            @PathVariable int id,
            @RequestBody Test updatedTest) {
        TestListDTO saved = testService.updateTest(id, updatedTest);
        if (saved == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(saved);
    }

    // Xóa test
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteTest(@PathVariable int id) {
        boolean deleted = testService.deleteTest(id);
        if (!deleted) return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Xóa thành công");
    }
}
