package com.ntt.lms.service;

import com.ntt.lms.dto.*;
import com.ntt.lms.pojo.Test;
import com.ntt.lms.pojo.TestSection;
import com.ntt.lms.pojo.Users;
import com.ntt.lms.repository.TestRepository;
import com.ntt.lms.utils.JwtService;
import com.ntt.lms.validator.AdminValidator;
import com.ntt.lms.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestService {
    private final TestRepository testRepository;

    private final UserValidator userValidator;
    private final AdminValidator adminValidator;

    public TestDTO getTestById(Integer testId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        return mapToDto(test);
    }

    public TestListDTO addTest(Test test) {
        test.setIsActive(true);
        Test saved = testRepository.save(test);
        return TestListDTO.builder()
                .testId(saved.getTestId())
                .title(saved.getTitle())
                .description(saved.getDescription())
                .isActive(saved.getIsActive())
                .type(saved.getType().name())
                .build();
    }

    public TestListDTO updateTest(int testId, Test updatedTest) {
        return testRepository.findById(testId)
                .map(test -> {
                    test.setTitle(updatedTest.getTitle());
                    test.setDescription(updatedTest.getDescription());
                    test.setType(updatedTest.getType());
                    test.setIsActive(updatedTest.getIsActive());
                    Test saved = testRepository.save(test);
                    return TestListDTO.builder()
                            .testId(saved.getTestId())
                            .title(saved.getTitle())
                            .description(saved.getDescription())
                            .isActive(saved.getIsActive())
                            .type(saved.getType().name())
                            .build();
                }).orElse(null);
    }


    public boolean deleteTest(Integer id) {
        if (!testRepository.existsById(id)) return false;
        testRepository.deleteById(id);
        return true;
    }


    public List<TestListDTO> getAllTests() {
        return testRepository.findAll().stream()
                .map(test -> TestListDTO.builder()
                        .testId(test.getTestId())
                        .title(test.getTitle())
                        .description(test.getDescription())
                        .isActive(test.getIsActive())
                        .type(test.getType().name())
                        .build())
                .collect(Collectors.toList());
    }

    public long countTest(){
        Users currentUser = JwtService.getCurrentUser();
        userValidator.validateUserAuthenticate(currentUser);
        adminValidator.validateHasAdminPermistion(currentUser);

        return testRepository.count();

    }

    public List<TestPreviewDTO> getAllTestPreviews() {
        List<Test> tests = testRepository.findAll();

        return tests.stream().map(test -> {
            TestPreviewDTO dto = new TestPreviewDTO();
            dto.setTestId(test.getTestId());
            dto.setTitle(test.getTitle());
            dto.setDescription(test.getDescription());
            dto.setType(test.getType().name());

            // Tính tổng thời gian
            int totalDuration = test.getSections().stream()
                    .map(TestSection::getDurationMinutes)
                    .filter(Objects::nonNull)
                    .reduce(0, Integer::sum);
            dto.setTotalDurationMinutes(totalDuration);

            // Tính danh sách section preview
            List<TestSectionPreviewDTO> sectionPreviews = test.getSections().stream()
                    .map(section -> {
                        TestSectionPreviewDTO sDto = new TestSectionPreviewDTO();
                        sDto.setSectionType(section.getSectionType().name());
                        sDto.setDurationMinutes(section.getDurationMinutes());

                        int totalQuestions = section.getParts().stream()
                                .flatMap(part -> part.getGroups().stream())
                                .flatMap(group -> group.getQuestions().stream())
                                .mapToInt(q -> 1)
                                .sum();

                        sDto.setTotalQuestions(totalQuestions);
                        sDto.setTotalParts(section.getParts().size());
                        return sDto;
                    }).collect(Collectors.toList());

            dto.setSections(sectionPreviews);
            return dto;
        }).collect(Collectors.toList());
    }



    public TestPreviewDTO getTestPreview(Integer testId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        TestPreviewDTO dto = new TestPreviewDTO();
        dto.setTestId(test.getTestId());
        dto.setTitle(test.getTitle());
        dto.setDescription(test.getDescription());
        dto.setType(test.getType().name());
        dto.setTotalDurationMinutes(test.getSections().stream()
                .map(TestSection::getDurationMinutes)
                .filter(Objects::nonNull)
                .reduce(0, Integer::sum));

        List<TestSectionPreviewDTO> sectionPreviews = test.getSections().stream()
                .map(section -> {
                    TestSectionPreviewDTO sDto = new TestSectionPreviewDTO();
                    sDto.setSectionType(section.getSectionType().name());
                    sDto.setDurationMinutes(section.getDurationMinutes());

                    int totalQuestions = section.getParts().stream()
                            .flatMap(part -> part.getGroups().stream())
                            .flatMap(group -> group.getQuestions().stream())
                            .mapToInt(q -> 1)
                            .sum();
                    sDto.setTotalQuestions(totalQuestions);
                    sDto.setTotalParts(section.getParts().size());

                    return sDto;
                }).collect(Collectors.toList());

        dto.setSections(sectionPreviews);
        return dto;
    }


    private TestDTO mapToDto(Test test) {
        return TestDTO.builder()
                .testId(test.getTestId())
                .title(test.getTitle())
                .description(test.getDescription())
                .type(test.getType().name())
                .durationMinutes(test.getDurationMinutes())
                .createdAt(test.getCreatedAt())
                .isActive(test.getIsActive())
                .sections(test.getSections().stream()
                        .map(section -> TestSectionDTO.builder()
                                .sectionId(section.getSectionId())
                                .sectionType(section.getSectionType().name())
                                .orderNumber(section.getOrderNumber())
                                .durationMinutes(section.getDurationMinutes())
                                .parts(section.getParts().stream()
                                        .map(part -> TestPartDTO.builder()
                                                .partId(part.getPartId())
                                                .partNumber(part.getPartNumber())
                                                .orderNumber(part.getOrderNumber())
                                                .title(part.getTitle())
                                                .description(part.getDescription())
                                                .audioUrl(part.getAudioUrl())
                                                .passage(part.getPassage())
                                                .groups(part.getGroups().stream()
                                                        .map(group -> TestQuestionGroupDTO.builder()
                                                                .groupId(group.getGroupId())
                                                                .title(group.getTitle())
                                                                .description(group.getDescription())
                                                                .orderNumber(group.getOrderNumber())
                                                                .questionGroupText(group.getQuestionGroupText())
                                                                .questions(group.getQuestions().stream()
                                                                        .map(q -> TestQuestionDTO.builder()
                                                                                .questionId(q.getQuestionId())
                                                                                .questionNumber(q.getQuestionNumber())
                                                                                .content(q.getContent())
                                                                                .questionType(q.getQuestionType() != null ? q.getQuestionType().getTypeName().name() : null)
                                                                                .options(q.getOptions())
                                                                                .build())
                                                                        .collect(Collectors.toList()))
                                                                .build())
                                                        .collect(Collectors.toList()))
                                                .build())
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .build();

    }


//    private TestDTO mapToDtoForReview(Test test, Map<Integer, List<String>> userAnswersMap) {
//        return TestDTO.builder()
//                .testId(test.getTestId())
//                .title(test.getTitle())
//                .description(test.getDescription())
//                .type(test.getType().name())
//                .durationMinutes(test.getDurationMinutes())
//                .createdAt(test.getCreatedAt())
//                .isActive(test.getIsActive())
//                .sections(test.getSections().stream().map(section ->
//                                TestSectionDTO.builder()
//                                        .sectionId(section.getSectionId())
//                                        .sectionType(section.getSectionType().name())
//                                        .orderNumber(section.getOrderNumber())
//                                        .durationMinutes(section.getDurationMinutes())
//                                        .parts(section.getParts().stream().map(part ->
//                                                        TestPartDTO.builder()
//                                                                .partId(part.getPartId())
//                                                                .partNumber(part.getPartNumber())
//                                                                .orderNumber(part.getOrderNumber())
//                                                                .title(part.getTitle())
//                                                                .description(part.getDescription())
//                                                                .audioUrl(part.getAudioUrl())
//                                                                .passage(part.getPassage())
//                                                                .groups(part.getGroups().stream().map(group ->
//                                                                                TestQuestionGroupDTO.builder()
//                                                                                        .groupId(group.getGroupId())
//                                                                                        .title(group.getTitle())
//                                                                                        .description(group.getDescription())
//                                                                                        .orderNumber(group.getOrderNumber())
//                                                                                        .questionGroupText(group.getQuestionGroupText())
//                                                                                        .questions(group.getQuestions().stream()
//                                                                                                .map(q -> TestQuestionPreviewDTO.builder()
//                                                                                                        .questionId(q.getQuestionId())
//                                                                                                        .questionNumber(q.getQuestionNumber())
//                                                                                                        .content(q.getContent())
//                                                                                                        .questionType(q.getQuestionType() != null ? q.getQuestionType().toString() : null)
//                                                                                                        .options(q.getOptions())
//                                                                                                        .correctAnswers(q.getCorrectAnswers()) // Lấy từ DB
//                                                                                                        .userAnswers(userAnswersMap.getOrDefault(q.getQuestionId(), List.of()))
//                                                                                                        .build())
//                                                                                                .collect(Collectors.toList()))
//                                                                                        .build())
//                                                                        .collect(Collectors.toList()))
//                                                                .build())
//                                                .collect(Collectors.toList()))
//                                        .build())
//                        .collect(Collectors.toList()))
//                .build();
//    }


}
