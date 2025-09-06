package com.ntt.lms.controller;

import com.ntt.lms.dto.TestSubmissionDTO;
import com.ntt.lms.pojo.*;
import com.ntt.lms.repository.TestRepository;
import com.ntt.lms.repository.TestSubmissionRepository;
import com.ntt.lms.repository.UsersRepository;
import com.ntt.lms.service.TestSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test-submissions")
@RequiredArgsConstructor
public class TestSubmissionController {

    private final TestRepository testRepository;
    private final UsersRepository usersRepository;
    private final TestSubmissionRepository testSubmissionRepository;
    private final TestSubmissionService testSubmissionService;

    // 1. Start test (chỉ tạo nếu chưa có)
    @PostMapping("/start/{testId}")
    public ResponseEntity<?> startTest(
            @PathVariable int testId,
            @RequestParam int userId) {
        try {
            Test test = testRepository.findById(testId)
                    .orElseThrow(() -> new RuntimeException("Test không tồn tại với ID: " + testId));
            Users user = usersRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User không tồn tại với ID: " + userId));
            TestSubmissionDTO dto = testSubmissionService.startTest(test, user);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            e.printStackTrace(); // log lỗi
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    // 2. Save answer
    @PostMapping("/{submissionId}/answer")
    public ResponseEntity<?> saveAnswer(
            @PathVariable int submissionId,
            @RequestParam int questionId,
            @RequestBody String userAnswerRaw) {

        String userAnswer = userAnswerRaw.trim();
        if (userAnswer.startsWith("\"") && userAnswer.endsWith("\"")) {
            userAnswer = userAnswer.substring(1, userAnswer.length() - 1);
        }

        TestSubmission submission = testSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        TestQuestion question = submission.getTest()
                .getSections().stream()
                .flatMap(s -> s.getParts().stream())
                .flatMap(p -> p.getGroups().stream())
                .flatMap(g -> g.getQuestions().stream())
                .filter(q -> q.getQuestionId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Question not found"));

        // GỌI service để lưu/cập nhật answer
        TestAnswer savedAnswer = testSubmissionService.saveAnswer(submission, question, userAnswer);

        return ResponseEntity.ok("Luu cau tra loi thanh cong");
    }

    // 3. Submit test (chỉ cập nhật, không tạo mới)
    @PutMapping("/{submissionId}/submit")
    public ResponseEntity<TestSubmission> submitTest(@PathVariable int submissionId) {
        return ResponseEntity.ok(testSubmissionService.submitTest(submissionId));
    }

    // 3b. Submit test với nhiều câu trả lời cùng lúc
    @PostMapping("/{submissionId}/submit-with-answers")
    public ResponseEntity<TestSubmission> submitTestWithAnswers(
            @PathVariable int submissionId,
            @RequestBody Map<Integer, String> answersMap) {

        TestSubmission submission = testSubmissionService.submitTestWithAnswers(submissionId, answersMap);
        return ResponseEntity.ok(submission);
    }



    @GetMapping("/{submissionId}/review")
    public ResponseEntity<TestSubmissionDTO> reviewTest(@PathVariable int submissionId) {
        return ResponseEntity.ok(testSubmissionService.getSubmissionDetails(submissionId));
    }

    @GetMapping("/count_submission")
    public ResponseEntity<?> countSubmission() {
        try {
            long count = testSubmissionService.countSubmission();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server: " + e.getMessage());
        }
    }

    @GetMapping("/count_submission_by_test/{testId}")
    public ResponseEntity<?> countSubmissionByTest(@PathVariable int testId) {
        try {
            long count = testSubmissionService.countSubmissionByTest(testId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server: " + e.getMessage());
        }
    }

    @GetMapping("/stat_submission/week/{testId}")
    public ResponseEntity<?> getWeeklySubmissions(@PathVariable int testId) {
        try {
            List<Map<String, Object>> result = testSubmissionService.countSubmissionPerWeek(testId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server: " + e.getMessage());
        }
    }



    @GetMapping("/{submissionId}")
    public ResponseEntity<TestSubmission> getSubmission(@PathVariable int submissionId) {
        return ResponseEntity.ok(testSubmissionService.getSubmissionById(submissionId));
    }

    @GetMapping("/test/{testId}/user/{userId}")
    public ResponseEntity<List<TestSubmission>> getUserSubmissionsForTest(
            @PathVariable int testId,
            @PathVariable int userId) {
        Test test = testRepository.findById(testId).orElseThrow();
        Users user = usersRepository.findById(userId).orElseThrow();
        return ResponseEntity.ok(testSubmissionService.getSubmissionsByUserAndTest(user, test));
    }

    @GetMapping("/test/{testId}/user/{userId}/has-started")
    public ResponseEntity<Boolean> hasUserStartedTest(
            @PathVariable int testId,
            @PathVariable int userId) {
        Test test = testRepository.findById(testId).orElseThrow();
        Users user = usersRepository.findById(userId).orElseThrow();
        return ResponseEntity.ok(testSubmissionService.hasUserStartedTest(user, test));
    }

    @GetMapping("/test/{testId}/user/{userId}/in-progress")
    public ResponseEntity<?> getInProgressSubmission(
            @PathVariable Integer testId,
            @PathVariable Integer userId
    ) {
        try {
            Test test = testRepository.findById(testId).orElseThrow();
            Users user = usersRepository.findById(userId).orElseThrow();

            TestSubmissionDTO dto = testSubmissionService.getInProgressSubmissionDTO(user, test);

            if (dto == null) {
                return ResponseEntity.ok().body("User chưa bắt đầu bài test này.");
            }

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Đã xảy ra lỗi: " + e.getMessage());
        }
    }


    @GetMapping("/band_statistics/test/{testId}")
    public ResponseEntity<?> getBandStatistics(@PathVariable Integer testId) {
        try {
            List<Map<String, Object>> stats = testSubmissionService.getBandStatistics(testId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server: " + e.getMessage());
        }
    }

    @GetMapping("/band_statistics/all")
    public ResponseEntity<?> getAllBandStatistics() {
        try {
            return ResponseEntity.ok(testSubmissionService.getAllBandStatistics());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server: " + e.getMessage());
        }
    }


}
