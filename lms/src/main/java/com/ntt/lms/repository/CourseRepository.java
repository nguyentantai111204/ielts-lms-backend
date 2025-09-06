package com.ntt.lms.repository;

import com.ntt.lms.pojo.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    Course findByCourseName(String courseName);

    @Query("SELECT CASE WHEN COUNT(course)>0 THEN true ELSE false END "+
          " FROM Course course "+
            "WHERE course.instructorId.userAccountId =:instructor "+
            " AND course.courseId = :courseId"
    )
    boolean findByInstructorId(int instructorId, int courseId);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Course c WHERE c.instructorId.userAccountId = :instructorId AND c.courseId = :courseId")
    boolean existsByInstructor_UserIdAndCourseId(int instructorId, int courseId);

    Optional<Course> findBySlug(String slug);


    List<Course> findByInstructorId_UserAccountId(int instructorId);
}
