package com.ntt.lms.service;

import com.ntt.lms.dto.TestAnswerDTO;
import com.ntt.lms.dto.TestSubmissionDTO;
import com.ntt.lms.pojo.*;
import com.ntt.lms.repository.TestAnswerRepository;
import com.ntt.lms.repository.TestQuestionRepository;
import com.ntt.lms.repository.TestRepository;
import com.ntt.lms.repository.TestSubmissionRepository;
import com.ntt.lms.utils.JwtService;
import com.ntt.lms.validator.AdminValidator;
import com.ntt.lms.validator.UserValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestSubmissionService {

    private final TestSubmissionRepository submissionRepo;
    private final TestAnswerRepository answerRepo;
    private final TestQuestionRepository questionRepo;
    private final TestRepository testRepository;


    private final UserValidator userValidator;
    private final AdminValidator adminValidator;

    @Transactional
    public TestSubmissionDTO startTest(Test test, Users user) {
        Optional<TestSubmission> existing = submissionRepo
                .findByUserAndTestAndStatus(user, test, SubmissionStatus.IN_PROGRESS);

        TestSubmission submission;
        if (existing.isPresent()) {
            submission = existing.get();
        } else {
            submission = new TestSubmission();
            submission.setTest(test);
            submission.setUser(user);
            submission.setStartedAt(LocalDateTime.now());
            submission.setStatus(SubmissionStatus.IN_PROGRESS);
            submission = submissionRepo.save(submission);
        }

        // Fetch dữ liệu đầy đủ trước khi tạo DTO
        Test fetchedTest = submission.getTest();
        Users fetchedUser = submission.getUser();

        TestSubmissionDTO dto = new TestSubmissionDTO();
        dto.setSubmissionId(submission.getSubmissionId());
        dto.setTestId(fetchedTest.getTestId());
        dto.setTestTitle(fetchedTest.getTitle());
        dto.setUserId(fetchedUser.getUserId());
        dto.setUserName(fetchedUser.getEmail());
        dto.setStartedAt(submission.getStartedAt());
        dto.setStatus(submission.getStatus());

        return dto;
    }

    // 2. Lưu câu trả lời tạm thời
    @Transactional
    public TestAnswer saveAnswer(TestSubmission submission, TestQuestion question, String userAnswer) {
        TestAnswer existingAnswer = answerRepo.findBySubmissionAndQuestion(submission, question);

        if (existingAnswer != null) {
            existingAnswer.setUserAnswer(userAnswer);
            existingAnswer.setAnsweredAt(LocalDateTime.now());
            return answerRepo.save(existingAnswer);
        } else {
            TestAnswer answer = new TestAnswer();
            answer.setSubmission(submission);
            answer.setQuestion(question);
            answer.setUserAnswer(userAnswer);
            answer.setAnsweredAt(LocalDateTime.now());
            return answerRepo.save(answer);
        }
    }

    // 3. Nộp bài
    @Transactional
    public TestSubmission submitTest(int submissionId) {
        TestSubmission submission = submissionRepo.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài nộp với ID: " + submissionId));

        if (submission.getStatus() == SubmissionStatus.SUBMITTED ||
                submission.getStatus() == SubmissionStatus.GRADED) {
            throw new RuntimeException("Bài đã được nộp trước đó");
        }

        submission.setSubmittedAt(LocalDateTime.now());
        submission.setStatus(SubmissionStatus.SUBMITTED);

        autoGrade(submission);

        return submissionRepo.save(submission);
    }

    // 4. Submit test với nhiều câu trả lời
    @Transactional
    public TestSubmission submitTestWithAnswers(int submissionId, Map<Integer, String> answersMap) {
        TestSubmission submission = submissionRepo.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài nộp với ID: " + submissionId));

        if (submission.getStatus() == SubmissionStatus.SUBMITTED ||
                submission.getStatus() == SubmissionStatus.GRADED) {
            throw new RuntimeException("Bài đã được nộp trước đó");
        }

        if (answersMap != null && !answersMap.isEmpty()) {
            saveAnswersBatch(submission, answersMap);
        }

        submission.setSubmittedAt(LocalDateTime.now());
        submission.setStatus(SubmissionStatus.SUBMITTED);

        autoGrade(submission);

        return submissionRepo.save(submission);
    }

    // 5. Lưu batch câu trả lời
    private void saveAnswersBatch(TestSubmission submission, Map<Integer, String> answersMap) {
        for (Map.Entry<Integer, String> entry : answersMap.entrySet()) {
            Integer questionId = entry.getKey();
            String userAnswer = entry.getValue();

            TestQuestion question = questionRepo.findById(questionId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy câu hỏi với ID: " + questionId));

            saveAnswer(submission, question, userAnswer);
        }
    }

    // 6. Auto grading (Listening + Reading)
    private void autoGrade(TestSubmission submission) {
        List<TestAnswer> answers = answerRepo.findBySubmission(submission);

        double listening = 0, reading = 0;

        for (TestAnswer ans : answers) {
            TestQuestion q = ans.getQuestion();
            TestSectionType sectionType = q.getGroup().getPart().getSection().getSectionType();

            if (sectionType == TestSectionType.LISTENING || sectionType == TestSectionType.READING) {
                boolean correct = compareAnswers(q.getCorrectAnswers(), ans.getUserAnswer());

                if (correct) {
                    ans.setIsCorrect(true);
                    ans.setScore(1.0);
                    if (sectionType == TestSectionType.LISTENING) listening++;
                    else reading++;
                } else {
                    ans.setIsCorrect(false);
                    ans.setScore(0.0);
                }
                answerRepo.save(ans);
            }
        }

        submission.setListeningScore(listening);
        submission.setReadingScore(reading);
        submission.setTotalScore(listening + reading);
        submission.setStatus(SubmissionStatus.GRADED);
    }

    private boolean compareAnswers(List<String> correctAnswers, String userAnswer) {
        if (correctAnswers == null || userAnswer == null) return false;

        List<String> correct = correctAnswers.stream()
                .map(this::normalizeAnswer)
                .toList();

        List<String> user = Arrays.stream(userAnswer.split(","))
                .map(this::normalizeAnswer)
                .toList();

        for (String ua : user) {
            if (correct.contains(ua)) return true;
        }
        return false;
    }

    private String normalizeAnswer(String input) {
        return input.trim().replaceAll("\\s+", " ").replaceAll("[\"']", "").toUpperCase();
    }

    // 7. Lấy submission chi tiết
    @Transactional
    public TestSubmissionDTO getSubmissionDetails(int submissionId) {
        TestSubmission submission = submissionRepo.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài nộp với ID: " + submissionId));

        Test fetchedTest = submission.getTest();
        Users fetchedUser = submission.getUser();

        List<TestAnswerDTO> answerDTOs = answerRepo.findBySubmission(submission).stream()
                .map(this::convertToAnswerDTO)
                .collect(Collectors.toList());

        TestSubmissionDTO dto = new TestSubmissionDTO();
        dto.setSubmissionId(submission.getSubmissionId());
        dto.setTestId(fetchedTest.getTestId());
        dto.setTestTitle(fetchedTest.getTitle());
        dto.setUserId(fetchedUser.getUserId());
        dto.setUserName(fetchedUser.getEmail());
        dto.setStartedAt(submission.getStartedAt());
        dto.setSubmittedAt(submission.getSubmittedAt());
        dto.setStatus(submission.getStatus());
        dto.setListeningScore(submission.getListeningScore());
        dto.setReadingScore(submission.getReadingScore());
        dto.setTotalScore(submission.getTotalScore());
        dto.setAnswers(answerDTOs);

        return dto;
    }

    private TestAnswerDTO convertToAnswerDTO(TestAnswer answer) {
        TestAnswerDTO dto = new TestAnswerDTO();
        dto.setAnswerId(answer.getAnswerId());
        dto.setQuestionId(answer.getQuestion().getQuestionId());
        dto.setQuestionText(answer.getQuestion().getContent());
        dto.setUserAnswer(answer.getUserAnswer());
        dto.setCorrectAnswers(answer.getQuestion().getCorrectAnswers());
        dto.setIsCorrect(answer.getIsCorrect());
        dto.setScore(answer.getScore());
        dto.setAnsweredAt(answer.getAnsweredAt());
        return dto;
    }

    // 6. Lấy thông tin submission theo ID
    public TestSubmission getSubmissionById(int submissionId) {
        return submissionRepo.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài nộp với ID: " + submissionId));
    }

    // 7. Lấy tất cả bài nộp của một user cho một test
    public List<TestSubmission> getSubmissionsByUserAndTest(Users user, Test test) {
        return submissionRepo.findByUserAndTest(user, test);
    }

    // 8. Kiểm tra xem user đã bắt đầu bài test chưa
    public boolean hasUserStartedTest(Users user, Test test) {
        List<TestSubmission> submissions = submissionRepo.findByUserAndTest(user, test);
        return submissions.stream()
                .anyMatch(s -> s.getStatus() == SubmissionStatus.IN_PROGRESS ||
                        s.getStatus() == SubmissionStatus.SUBMITTED ||
                        s.getStatus() == SubmissionStatus.GRADED);
    }

    @Transactional
    public TestSubmissionDTO getInProgressSubmissionDTO(Users user, Test test) {
        // Lấy bài submission IN_PROGRESS
        TestSubmission submission = submissionRepo.findByUserAndTest(user, test).stream()
                .filter(s -> s.getStatus() == SubmissionStatus.IN_PROGRESS)
                .findFirst()
                .orElse(null);

        if (submission == null) return null;

        // Tạo DTO cơ bản
        TestSubmissionDTO dto = new TestSubmissionDTO(submission);

        // Lấy danh sách answers và map sang DTO
        List<TestAnswerDTO> answerDTOs = answerRepo.findBySubmission(submission).stream()
                .map(ans -> {
                    TestAnswerDTO aDto = new TestAnswerDTO();
                    aDto.setAnswerId(ans.getAnswerId());
                    aDto.setQuestionId(ans.getQuestion().getQuestionId());
                    aDto.setQuestionText(ans.getQuestion().getContent());
                    aDto.setUserAnswer(ans.getUserAnswer());
                    aDto.setCorrectAnswers(ans.getQuestion().getCorrectAnswers());
                    aDto.setIsCorrect(ans.getIsCorrect());
                    aDto.setScore(ans.getScore());
                    aDto.setAnsweredAt(ans.getAnsweredAt());
                    return aDto;
                })
                .collect(Collectors.toList());

        dto.setAnswers(answerDTOs);

        // Optionally: tính thêm tổng số câu, số câu trả lời, số câu đúng
        dto.setTotalQuestions(answerDTOs.size());
        dto.setAnsweredQuestions((int) answerDTOs.stream().filter(a -> a.getUserAnswer() != null).count());
        dto.setCorrectAnswers((int) answerDTOs.stream().filter(a -> Boolean.TRUE.equals(a.getIsCorrect())).count());

        return dto;
    }

    public long countSubmission()
    {
        Users user = JwtService.getCurrentUser();
        userValidator.validateUserAuthenticate(user);
        adminValidator.validateHasAdminPermistion(user);

        return submissionRepo.count();
    }

    public long countSubmissionByTest(int testId) {
        Users user = JwtService.getCurrentUser();
        userValidator.validateUserAuthenticate(user);
        adminValidator.validateHasAdminPermistion(user);

        return submissionRepo.countByTest_TestId(testId);
    }

    public List<Map<String, Object>> countSubmissionPerWeek(int testId) {
        List<Object[]> results = submissionRepo.countSubmissionPerWeek(testId);

        List<Map<String, Object>> weeklyStats = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            Integer year = ((Number) row[0]).intValue();
            Integer week = ((Number) row[1]).intValue();
            Long count = ((Number) row[2]).longValue();
            map.put("year", year);
            map.put("week", week);
            map.put("count", count);
            map.put("weekLabel", "Tuần " + week + " - " + year);
            weeklyStats.add(map);
        }
        return weeklyStats;
    }


    public List<Map<String, Object>> getAllBandStatistics() {
        Users currentUser = JwtService.getCurrentUser();

        userValidator.validateUserAuthenticate(currentUser);
        adminValidator.validateHasAdminPermistion(currentUser);

        // lấy tất cả submissions
        List<TestSubmission> submissions = submissionRepo.findAll();

        // cache tổng số câu hỏi cho từng test để tránh query nhiều lần
        Map<Integer, Integer> testQuestionCountCache = new HashMap<>();

        Map<String, Long> bandCount = submissions.stream()
                .map(submission -> {
                    Test test = submission.getTest();
                    int testId = test.getTestId();

                    // nếu chưa có trong cache thì tính số câu hỏi
                    int totalQuestions = testQuestionCountCache.computeIfAbsent(testId, id ->
                            test.getSections().stream()
                                    .flatMap(section -> section.getParts().stream())
                                    .flatMap(part -> part.getGroups().stream())
                                    .flatMap(group -> group.getQuestions().stream())
                                    .mapToInt(q -> 1)
                                    .sum()
                    );

                    return convertRawToBand(submission.getTotalScore(), totalQuestions);
                })
                .collect(Collectors.groupingBy(band -> band, Collectors.counting()));

        // build response
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : bandCount.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("band", entry.getKey());
            item.put("count", entry.getValue());
            result.add(item);
        }

        return result;
    }


    public List<Map<String, Object>> getBandStatistics(int testId) {
        Users currentUser = JwtService.getCurrentUser();

        userValidator.validateUserAuthenticate(currentUser);
        adminValidator.validateHasAdminPermistion(currentUser);

        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài test"));

        List<TestSubmission> submissions = submissionRepo.findByTest_TestId(testId);

        int totalQuestions = test.getSections().stream()
                .flatMap(section -> section.getParts().stream())
                .flatMap(part -> part.getGroups().stream())
                .flatMap(group -> group.getQuestions().stream())
                .mapToInt(q -> 1)
                .sum();

        Map<String, Long> bandCount = submissions.stream()
                .map(s->convertRawToBand(s.getTotalScore(), totalQuestions))
                        .collect(Collectors.groupingBy(band->band, Collectors.counting()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : bandCount.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("band", entry.getKey());
            item.put("count", entry.getValue());
            result.add(item);
        }
        return result;
    }



    private String convertRawToBand(double rawScore, int totalQuestions) {
        // chuẩn IELTS là 40 câu, nếu test <40 thì scale lên
        double scaledScore = ((double) rawScore / totalQuestions) * 40;

        if (scaledScore >= 39) return "9.0";
        else if (scaledScore >= 37) return "8.5";
        else if (scaledScore >= 35) return "8.0";
        else if (scaledScore >= 32) return "7.5";
        else if (scaledScore >= 30) return "7.0";
        else if (scaledScore >= 26) return "6.5";
        else if (scaledScore >= 23) return "6.0";
        else if (scaledScore >= 18) return "5.5";
        else if (scaledScore >= 16) return "5.0";
        else if (scaledScore >= 13) return "4.5";
        else if (scaledScore >= 10) return "4.0";
        else if (scaledScore >= 7)  return "3.5";
        else if (scaledScore >= 5)  return "3.0";
        else if (scaledScore >= 3)  return "2.5";
        else return "2.0";
    }

}