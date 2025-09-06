package com.ntt.lms.validator;

import com.ntt.lms.pojo.Course;
import com.ntt.lms.pojo.Users;
import com.ntt.lms.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseValidator {

    private final CourseRepository courseRepository;

    public Course validateIsExitsCourseWithId(int courseId){
        return courseRepository.findById(courseId).orElseThrow(()->new IllegalArgumentException("Không tìm thấy khóa học với ID: "+courseId));
    }

    public void validateIsExitsCourseWithName(String name){
        Course course = courseRepository.findByCourseName(name);
        if(course != null){
            throw new IllegalArgumentException("Tên khóa học đã toồn tại");
        }
    }

    // Giáo viên chỉ được phép hành động những khóa học mà mình tạo ra
    public void validateInstructorHasPermissionWithCourse(Users currentUser, int courseId) {
        if (currentUser.getUserType().getUserTypeId() == 3) {
            boolean isOwner = courseRepository.existsByInstructor_UserIdAndCourseId(
                    currentUser.getUserId(), courseId
            );
            if (!isOwner) {
                throw new IllegalArgumentException("Bạn không được phép thực hiện hành động này trên khóa học của giáo viên khác.");
            }
        }
    }


}
