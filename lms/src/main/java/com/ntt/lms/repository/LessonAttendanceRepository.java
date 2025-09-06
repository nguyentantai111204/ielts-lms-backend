package com.ntt.lms.repository;

import com.ntt.lms.pojo.LessonAttendance;
import com.ntt.lms.pojo.Lesson;
import com.ntt.lms.pojo.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonAttendanceRepository extends JpaRepository<LessonAttendance, Integer> {
    // Kiem tra sinh vien hoan thanh bai hoc chua
    boolean existsByLessonIdAndStudentId(Lesson lessonId , Student studentId);
    List<LessonAttendance> findAllByLessonId (Lesson LessonId);

}
