package com.ntt.lms.service;

import com.ntt.lms.dto.CourseOutcomeDto;
import com.ntt.lms.pojo.CourseOutcome;
import com.ntt.lms.repository.CourseOutcomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseOutcomeService {

    private final CourseOutcomeRepository courseOutcomeRepository;

    public List<CourseOutcomeDto> getOutcomesByCourseId(int courseId) {
        List<CourseOutcome> outcomes = courseOutcomeRepository.findByCourse_CourseId(courseId);
        return outcomes.stream()
                .map(outcome -> new CourseOutcomeDto(outcome.getId(), outcome.getDescription()))
                .collect(Collectors.toList());
    }
}
