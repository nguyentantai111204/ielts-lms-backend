package com.ntt.lms.repository;

import com.ntt.lms.pojo.TestQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestQuestionRepository extends JpaRepository<TestQuestion, Integer> {
}
