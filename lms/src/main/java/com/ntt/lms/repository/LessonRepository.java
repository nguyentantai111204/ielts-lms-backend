package com.ntt.lms.repository;

import com.ntt.lms.pojo.Course;
import com.ntt.lms.pojo.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Integer> {
    List<Lesson> findByCourseId(Course course);
    Lesson findByLessonNameAndCourseId(String lessonName, Course course);
}
