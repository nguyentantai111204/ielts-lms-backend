package com.ntt.lms.repository;

import com.ntt.lms.pojo.Grading;
import com.ntt.lms.pojo.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface GradingRepository extends JpaRepository<Grading, Integer> {
    // Tìm điểm của sinh viên trong bài quiz
    @Query("SELECT COALESCE(g.grade, -1) FROM Grading g WHERE g.quizId.quizId = :quizId AND g.student_id.userId.userId = :studentId")
    int findGradeByQuizAndStudentID(@Param("quizId") int quizId, @Param("studentId") int studentId);

    // Kiểm tra xem sinh viên được chấm điểm chưa
    @Query("SELECT COUNT(g)>0 "+
            "FROM Grading g " +
            "WHERE g.quizId.quizId = :quizId AND g.student_id.userId.userId = :studentId")
    Optional<Boolean> boolFindGradeByQuizAndStudentID(@Param("quizId") int quizId, @Param("studentId") int studentId);

    // Kiểm tra xem sinh viên được chấm điểm chưa
    @Query("SELECT g.student_id.userAccountId, g.grade FROM Grading g WHERE g.quizId.quizId = :quizId")
    List<Object[]> findGradeByQuiz(@Param("quizId") int quizId);

    // Lấy danh sach sinh vien da duoc cham diem
    @Query("SELECT g.student_id.userAccountId FROM Grading g WHERE g.quizId.quizId = :quizId")
    List<Integer> findStudentByQuiz(@Param("quizId") int quizId);

    //Lấy danh sách điểm số của sinh viên trong quiz
    @Query("SELECT g.grade FROM Grading g WHERE g.quizId.quizId = :quizId")
    List<Integer> findGradeByQuizId(@Param("quizId") int quizId);

    List <Grading> findAllByQuizId (Quiz quizId);

}
