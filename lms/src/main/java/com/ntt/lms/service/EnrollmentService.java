package com.ntt.lms.service;

import com.ntt.lms.dto.StudentDto;
import com.ntt.lms.pojo.Course;
import com.ntt.lms.pojo.Enrollment;
import com.ntt.lms.pojo.Student;
import com.ntt.lms.pojo.Users;
import com.ntt.lms.repository.CourseRepository;
import com.ntt.lms.repository.EnrollmentRepository;
import com.ntt.lms.repository.StudentRepository;
import com.ntt.lms.utils.JwtService;
import com.ntt.lms.validator.CourseValidator;
import com.ntt.lms.validator.InstructorValidator;
import com.ntt.lms.validator.StudentValidator;
import com.ntt.lms.validator.UserValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final NotificationsService notificationsService;

    private final StudentValidator studentValidator;
    private final CourseValidator courseValidator;
    private final InstructorValidator instructorValidator;
    private final UserValidator userValidator;

    public void enrollInCourse(Enrollment request) {
        Users currentUser = JwtService.getCurrentUser();
        userValidator.validateUserAuthenticate(currentUser);
        instructorValidator.validateHasPermissionInstructor(currentUser);

        Student student = studentValidator.validateIsExitsStudent(request.getStudent().getUserAccountId());

        Course course = courseValidator.validateIsExitsCourseWithId(request.getCourse().getCourseId());

        if (enrollmentRepository.existsByStudentAndCourse(student, course)) {
            throw new IllegalArgumentException("Học sinh đã đăng ký khóa học này.");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(new Date());
        enrollment.setStatus(Enrollment.EnrollmentStatus.ACTIVE);
        enrollmentRepository.save(enrollment);
        notificationsService.sendNotification(
                "Thêm sinh viên " + student.getUserAccountId() + " vào khóa học thành công",
                course.getInstructorId().getUserAccountId()
        );
    }

    public List<StudentDto> viewEnrolledStudents(int courseId){
        Users currentUser = JwtService.getCurrentUser();
        userValidator.validateUserAuthenticate(currentUser);
        instructorValidator.validateHasPermissionInstructor(currentUser);

        Course currentCourse = this.courseRepository.findById(courseId).orElseThrow(()-> new EntityNotFoundException("Khoa hoc khong ton tai"));

        List<Enrollment>enrollments = enrollmentRepository.findByCourse(currentCourse);
        List<Student> students = new ArrayList<Student>();
        for (Enrollment enrollment : enrollments) {
            students.add(enrollment.getStudent());
        }
        return convertToDtoList(students);
    }

    public void removeEnrolledStudent(int courseId, int studentId){
        Users currentUser = JwtService.getCurrentUser();
        userValidator.validateUserAuthenticate(currentUser);
        instructorValidator.validateHasPermissionInstructor(currentUser);

        Course course = this.courseRepository.findById(courseId).orElseThrow(()-> new IllegalArgumentException("Khoa hoc khong ton tai"));
        Student student  = this.studentRepository.findById(studentId).orElseThrow(()-> new IllegalArgumentException("Sinh vien khong ton tai"));

        boolean isEnrolled = enrollmentRepository.existsByStudentAndCourse(student, course);

        if(!isEnrolled){
            throw new IllegalArgumentException("Sinh vien chua tham gia vao khoa hoc");
        }

        Enrollment enrollment = enrollmentRepository.findByStudentAndCourse(student,course);
        enrollmentRepository.deleteById(enrollment.getEnrollmentId());
    }



    private List<StudentDto> convertToDtoList(List<Student> students) {
        return students.stream()
                .map(student -> new StudentDto(
                        student.getUserAccountId(),
                        student.getUserId().getEmail(),
                        student.getFirstName(),
                        student.getLastName()
                ))
                .collect(Collectors.toList());
    }






}
