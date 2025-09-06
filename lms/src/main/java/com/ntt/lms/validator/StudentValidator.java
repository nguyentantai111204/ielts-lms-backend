package com.ntt.lms.validator;

import com.ntt.lms.pojo.Course;
import com.ntt.lms.pojo.Student;
import com.ntt.lms.pojo.Users;
import com.ntt.lms.repository.EnrollmentRepository;
import com.ntt.lms.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudentValidator {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;

    //Kiểm tra xem sinh viên có trong khóa học hay không
    public void validateStudentEnrolledInCourse(Users currentUser, Course course) {
        if (currentUser.getUserType().getUserTypeId() == 2) {
            boolean exists = enrollmentRepository.findByCourse(course).stream()
                    .anyMatch(e -> e.getStudent().getUserAccountId() == currentUser.getUserId());

            if (!exists) {
                throw new IllegalArgumentException("Bạn không được phép vào khóa học này");
            }
        }
    }

    public Student validateIsExitsStudent(int studentId){
        return studentRepository.findById(studentId).orElseThrow(()-> new IllegalArgumentException("Không tìm thấy sinh viên với Id: "+ studentId));
    }


    public void validateIsStudent(Users user) {
        if (user == null || user.getUserType() == null) {
            throw new IllegalArgumentException("Người dùng không hợp lệ.");
        }
        if (!"STUDENT".equalsIgnoreCase(user.getUserType().getUserTypeName())) {
            throw new IllegalArgumentException("Chỉ học viên mới được thực hiện hành động này.");
        }
    }

    public void validateStudentHasPermission(Users user, int studentId) {
        if (user == null || user.getUserType() == null) {
            throw new IllegalArgumentException("Người dùng không hợp lệ.");
        }
        if (!"STUDENT".equalsIgnoreCase(user.getUserType().getUserTypeName())) {
            throw new IllegalArgumentException("Chỉ học viên mới được thực hiện hành động này.");
        }

        if(user.getUserId() != studentId)
        {
            throw new IllegalArgumentException("Bạn không được thực hiện hành động này.");
        }
    }

}
