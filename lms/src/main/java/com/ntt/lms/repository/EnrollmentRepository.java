package com.ntt.lms.repository;

import com.ntt.lms.pojo.Course;
import com.ntt.lms.pojo.Enrollment;
import com.ntt.lms.pojo.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    boolean existsByStudentAndCourse(Student student, Course course);
    List<Enrollment> findByCourse(Course course);
    Enrollment findByStudentAndCourse(Student student, Course course);
    @Query("SELECT e.course FROM Enrollment e WHERE e.student.userAccountId = :studentId AND e.status = :status")
    List<Course> findCoursesByStudentIdAndStatus(int studentId, Enrollment.EnrollmentStatus status);
}