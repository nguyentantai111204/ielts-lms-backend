package com.ntt.lms.repository;

import com.ntt.lms.pojo.SubLesson;
import com.ntt.lms.pojo.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubLessonRepository extends JpaRepository<SubLesson, Integer> {
    List<SubLesson> findByLessonId(Lesson lesson);
}
