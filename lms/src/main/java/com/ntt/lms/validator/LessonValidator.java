package com.ntt.lms.validator;

import com.ntt.lms.pojo.Course;
import com.ntt.lms.pojo.Lesson;
import com.ntt.lms.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LessonValidator {

    private final LessonRepository lessonRepository;

    public void validateIsExitsLessonWithName(String name, Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Không thể kiểm tra tên bài học nếu không có khóa học.");
        }
        Lesson lesson = lessonRepository.findByLessonNameAndCourseId(name, course);
        if (lesson != null) {
            throw new IllegalArgumentException("Bài học tên \"" + name + "\" đã tồn tại trong khóa học \"" + course.getCourseName() + "\".");
        }
    }

    public Lesson validateIsExitsLessonWithId(int lessonId) {
       return lessonRepository.findById(lessonId).orElseThrow(()-> new IllegalArgumentException("Không tìm thấy khóa học với id: "+ lessonId));
    }

    public void validateDataRequest(Lesson lesson){
        if(lesson.getLessonName() == null){
            throw new IllegalArgumentException("Tên bài học không được trống");
        }
        if(lesson.getCourseId() == null){
            throw new IllegalArgumentException("Khóa học không được trống");
        }
        if(lesson.getOTP() == null){
            throw new IllegalArgumentException("OTP không được trống");
        }
    }

    public void validateLessonOrder(int lessonId, int lessonOrder){
        Lesson lesson = validateIsExitsLessonWithId(lessonId);
        if(lesson.getLessonOrder() == lessonOrder){
            throw new IllegalArgumentException("Đã có bài học đang ở thứ tự số "+lessonOrder);
        }
    }




}
