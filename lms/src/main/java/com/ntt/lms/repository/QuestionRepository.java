package com.ntt.lms.repository;

import com.ntt.lms.pojo.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    //Tìm tất cả câu hỏi trong khóa học
    @Query("SELECT q FROM Question q WHERE q.courseId.courseId = :courseId")
    List<Question> findQuestionByCourseId(@Param("courseId") int courseId);

    // Tìm các câu hỏi trong Quiz
    @Query("SELECT q FROM Question q WHERE q.quiz.quizId = :quizId")
    List<Question> findQuestionsByQuizId(@Param("quizId") int quizId);

    // Phân loại câu hỏi trong khóa học
    @Query("SELECT q FROM Question q WHERE q.courseId.courseId = :courseId AND q.questionType.typeId = :questionType")
    List<Question> findQuestionsByCourseIdAndQuestionType(@Param("courseId") int courseId, @Param("questionType") int questionType);

    // Tìm câu hỏi theo loại trong khóa học và chưa nằm trong Quiz nào
    @Query("SELECT q FROM Question q WHERE q.courseId.courseId = :courseId AND q.questionType.typeId = :questionType AND q.quiz.quizId IS NULL ")
    List<Question> findEmptyQuestionsByCourseIdAndQuestionType(@Param("courseId") int courseId, @Param("questionType") int questionType);
}
