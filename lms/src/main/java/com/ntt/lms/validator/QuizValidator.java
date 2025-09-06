package com.ntt.lms.validator;

import com.ntt.lms.pojo.Quiz;
import com.ntt.lms.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuizValidator {

    private final QuizRepository quizRepository;

    public Quiz validateIsNotExitsQuizWithQuizIdAndCourseId(int courseId, int quizId) {
        if(courseId != 0){
            boolean exists = quizRepository.existsByQuizIdAndCourse_CourseId(quizId, courseId);
            if (!exists) {
                throw new IllegalArgumentException("Quiz không tồn tại với ID và khóa học tương ứng.");
            }
        }
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz không tồn tại."));
    }


    public void validateIsExitsQuizWithQuizTitleAndCourseId(String quizTitle , int courseId ){
        if(quizRepository.existsByTitleAndCourse_CourseId(quizTitle, courseId)){
            throw new IllegalArgumentException("Đã tồn tại quiz "+quizTitle+ " trong khóa học id: "+ courseId);
        }
    }


}
