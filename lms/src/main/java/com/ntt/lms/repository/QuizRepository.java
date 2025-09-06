package com.ntt.lms.repository;

import com.ntt.lms.pojo.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Integer> {
    @Query("SELECT q FROM Quiz q WHERE q.course.courseId = :courseId ")
    List<Quiz> getQuizzesByCourseId(@Param("courseId") int courseId );

    @Query("SELECT q FROM Quiz q WHERE q.lesson.lessonId = :lessonId ")
    List<Quiz> getQuizzesByLessonId(@Param("lessonId") int lessonId );
    boolean existsByQuizIdAndCourse_CourseId(int quizId, int courseId);
    boolean existsByTitleAndCourse_CourseId(String quizName, int courseId);
}
