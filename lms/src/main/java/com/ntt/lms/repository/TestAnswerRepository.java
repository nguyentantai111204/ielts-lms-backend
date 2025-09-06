package com.ntt.lms.repository;

import com.ntt.lms.pojo.TestAnswer;
import com.ntt.lms.pojo.TestQuestion;
import com.ntt.lms.pojo.TestSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestAnswerRepository extends JpaRepository<TestAnswer, Integer> {
    List<TestAnswer> findBySubmission(TestSubmission submission);
    TestAnswer findBySubmissionAndQuestion(TestSubmission submission, TestQuestion question);

}
