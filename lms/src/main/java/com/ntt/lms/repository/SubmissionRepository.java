package com.ntt.lms.repository;

import com.ntt.lms.pojo.Assignment;
import com.ntt.lms.pojo.Student;
import com.ntt.lms.pojo.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Integer> {

    List<Submission> findByStudentId(Student student);

    // Tìm tất cả bài nộp trong 1 assignment
    List<Submission> findAllByAssignmentId(Assignment assignment);

    // Tìm bài nộp theo studentId và assignmentId
    Submission findByStudentIdAndAssignmentId(Student student, Assignment assignment);

    // Nếu muốn chỉ check có tồn tại hay không
    boolean existsByStudentIdAndAssignmentId(Student student, Assignment assignment);
}

