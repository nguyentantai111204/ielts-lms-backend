package com.ntt.lms.repository;

import com.ntt.lms.pojo.SubmissionStatus;
import com.ntt.lms.pojo.Test;
import com.ntt.lms.pojo.TestSubmission;
import com.ntt.lms.pojo.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TestSubmissionRepository extends JpaRepository<TestSubmission, Integer> {
    List<TestSubmission> findByUser(Users user);
    List<TestSubmission> findByUserAndTest(Users user, Test test);
    Optional<TestSubmission> findByUserAndTestAndStatus(Users user, Test test, SubmissionStatus status);
    List<TestSubmission> findByTest_TestId(Integer testId);

    long countByTest_TestId(Integer testId);

    @Query("SELECT FUNCTION('YEAR', s.startedAt) AS year, FUNCTION('WEEK', s.startedAt) AS week, COUNT(s) AS count " +
            "FROM TestSubmission s " +
            "WHERE s.test.testId = :testId " +
            "GROUP BY FUNCTION('YEAR', s.startedAt), FUNCTION('WEEK', s.startedAt) " +
            "ORDER BY year ASC, week ASC")
    List<Object[]> countSubmissionPerWeek(@Param("testId") int testId);

}
