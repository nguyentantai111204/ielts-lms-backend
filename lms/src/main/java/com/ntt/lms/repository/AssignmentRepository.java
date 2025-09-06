package com.ntt.lms.repository;

import com.ntt.lms.pojo.Assignment;
import com.ntt.lms.pojo.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {
    List<Assignment> findByLessonId(Lesson lesson);

}
