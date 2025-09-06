package com.ntt.lms.repository;

import com.ntt.lms.pojo.CourseOutcome;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseOutcomeRepository extends JpaRepository<CourseOutcome, Integer> {
    List<CourseOutcome> findByCourse_CourseId(int courseId);

}
