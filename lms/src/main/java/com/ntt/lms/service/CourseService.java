package com.ntt.lms.service;

import com.ntt.lms.dto.CourseDto;
import com.ntt.lms.dto.CourseUpdateRequest;
import com.ntt.lms.dto.StudentDto;
import com.ntt.lms.pojo.Course;
import com.ntt.lms.pojo.Enrollment;
import com.ntt.lms.pojo.Instructor;
import com.ntt.lms.pojo.Users;
import com.ntt.lms.repository.CourseRepository;
import com.ntt.lms.repository.EnrollmentRepository;
import com.ntt.lms.repository.InstructorRepository;
import com.ntt.lms.repository.UsersRepository;
import com.ntt.lms.utils.CloudinaryService;
import com.ntt.lms.utils.JwtService;

import com.ntt.lms.validator.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UsersRepository usersRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final InstructorRepository instructorRepository;

    private final CloudinaryService cloudinaryService;
    private final EnrollmentService enrollmentService;
    private final NotificationsService notificationsService;


    private final UserValidator userValidator;
    private final InstructorValidator instructorValidator;
    private final CourseValidator courseValidator;
    private final StudentValidator studentValidator;
    private final PermissionValidator permissionValidator;
    private final AdminValidator adminValidator;



    public void addCourse(CourseUpdateRequest addRequest, MultipartFile file) {
        Users currentUser = JwtService.getCurrentUser();
        userValidator.validateUserAuthenticate(currentUser);
        permissionValidator.validateHasPermissionAdminOrInstructor(currentUser);

        if (addRequest.getCourseName() != null) {
            courseValidator.validateIsExitsCourseWithName(addRequest.getCourseName());
        }

        Course course = new Course();
        course.setCourseName(addRequest.getCourseName());
        course.setDescription(addRequest.getDescription());
        course.setDuration(addRequest.getDuration() != null ? addRequest.getDuration() : 0);
        course.setCreationDate(new Date());

  
        Instructor instructor;
        if (currentUser.getUserType().getUserTypeId() == 1) {
            if (addRequest.getInstructorId() == null) {
                throw new IllegalArgumentException("Vui lòng chọn giáo viên");
            }
            instructor = instructorRepository.findById(addRequest.getInstructorId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Không tìm thấy giáo viên với ID: " + addRequest.getInstructorId()));
        } else {
            instructor = instructorRepository.findById(currentUser.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin instructor"));
        }
        course.setInstructorId(instructor);


        if (file != null && !file.isEmpty()) {
            try {
                String fileUrl = cloudinaryService.uploadFile(file);
                course.setMedia(fileUrl);
            } catch (IOException e) {
                throw new RuntimeException("Upload file thất bại", e);
            }
        }

        courseRepository.save(course);
    }



    public List<CourseDto> getAllCourses() {
//        Users currentUser = JwtService.getCurrentUser();
//        userValidator.validateUserAuthenticate(currentUser);

        List<Course> courses = courseRepository.findAll();

        return convertToCourseDtoList(courses);
    }

    public long countCourse(){
        Users currentUser = JwtService.getCurrentUser();
        userValidator.validateUserAuthenticate(currentUser);
        adminValidator.validateHasAdminPermistion(currentUser);

        return courseRepository.count();
    }


    public CourseDto getCourseById(int courseId){
        Users currentUser = JwtService.getCurrentUser();
        userValidator.validateUserAuthenticate(currentUser);

        Course course = courseValidator.validateIsExitsCourseWithId(courseId);

        studentValidator.validateStudentEnrolledInCourse(currentUser, course);

        return new CourseDto(
                course.getCourseId(),
                course.getCourseName(),
                course.getDescription(),
                course.getDuration(),
                course.getMedia(),
                course.getInstructorId().getFirstName(),
                course.getInstructorId().getUserAccountId()
        );
    }

    public void updateCourse(int courseId, CourseUpdateRequest updateRequest, MultipartFile file) {
        Users currentUser = JwtService.getCurrentUser();

        userValidator.validateUserAuthenticate(currentUser);
        Course existingCourse = courseValidator.validateIsExitsCourseWithId(courseId);
        permissionValidator.validateHasPermissionAdminOrInstructor(currentUser);
        courseValidator.validateInstructorHasPermissionWithCourse(currentUser, courseId);

        // Nếu có JSON thì mới update
        if (updateRequest != null) {
            if (updateRequest.getCourseName() != null) {
                existingCourse.setCourseName(updateRequest.getCourseName());
            }
            if (updateRequest.getDescription() != null) {
                existingCourse.setDescription(updateRequest.getDescription());
            }
            if (updateRequest.getDuration() != null && updateRequest.getDuration() > 0) {
                existingCourse.setDuration(updateRequest.getDuration());
            }
            if (updateRequest.getInstructorId() != null) {
                Instructor instructor = instructorRepository.findById(updateRequest.getInstructorId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Không tìm thấy giáo viên với ID: " + updateRequest.getInstructorId()));
                existingCourse.setInstructorId(instructor);
            }

        }

        // Nếu có file thì update media
        if (file != null && !file.isEmpty()) {
            try {
                String fileUrl = cloudinaryService.uploadFile(file);
                existingCourse.setMedia(fileUrl);
            } catch (IOException e) {
                throw new RuntimeException("Tải file lên Cloudinary thất bại", e);
            }
        }

        courseRepository.save(existingCourse);
    }





    public void deleteCourse(int courseId) {
        Users currentUser = JwtService.getCurrentUser();

        userValidator.validateUserAuthenticate(currentUser);

        courseValidator.validateIsExitsCourseWithId(courseId);
        permissionValidator.validateHasPermissionAdminOrInstructor(currentUser);
        courseValidator.validateInstructorHasPermissionWithCourse(currentUser,courseId);

        this.courseRepository.deleteById(courseId);
    }

    public void uploadMediaFile(int courseId, MultipartFile file){
        Users currentUser = JwtService.getCurrentUser();

        // Kiềm tra điều kiện cần
        courseValidator.validateIsExitsCourseWithId(courseId);
        permissionValidator.validateHasPermissionAdminOrInstructor(currentUser);
        courseValidator.validateInstructorHasPermissionWithCourse(currentUser,courseId);

        Course course = courseValidator.validateIsExitsCourseWithId(courseId);

        try {
            String fileUrl =  cloudinaryService.uploadFile(file);
            course.setMedia(fileUrl);
            courseRepository.save(course);
        } catch (IOException e) {
            throw new RuntimeException("Tải file lên Cloudinary thất bại", e);
        }
    }

    public List<CourseDto> getActiveCoursesForCurrentStudent() {
        Users currentUser = JwtService.getCurrentUser();
        userValidator.validateUserAuthenticate(currentUser);
        studentValidator.validateIsStudent(currentUser);

        int studentId = currentUser.getUserId();

        List<Course> courses = enrollmentRepository.findCoursesByStudentIdAndStatus(studentId, Enrollment.EnrollmentStatus.ACTIVE);

        return convertToCourseDtoList(courses);
    }

    public List<CourseDto> getCoursesForCurrentInstructor() {
        Users currentUser = JwtService.getCurrentUser();
        userValidator.validateUserAuthenticate(currentUser);
        instructorValidator.validateHasPermissionInstructor(currentUser);

        int instructorId = currentUser.getUserId();

        // Lấy tất cả course mà instructor này dạy
        List<Course> courses = courseRepository.findByInstructorId_UserAccountId(instructorId);

        return convertToCourseDtoList(courses);
    }



    public void sendNotificationsToEnrolledStudents(int courseId){
        List<StudentDto> students = enrollmentService.viewEnrolledStudents(courseId);
        String message = "Khóa học " + getCourseById(courseId).getCourseName() + " đã được cập nhật";
        for (StudentDto student : students){
            notificationsService.sendNotification(message,student.getUserAccountId());
        }
    }

    public CourseDto getCourseDtoBySlug(String slug) {
        Course course = courseRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khóa học với slug: " + slug));

        return new CourseDto(
                course.getCourseId(),
                course.getCourseName(),
                course.getDescription(),
                course.getDuration(),
                course.getMedia(),
                course.getInstructorId().getFirstName(),
                course.getInstructorId().getUserAccountId()
        );
    }




    // Chuyển DTO --> Entity
    private List<CourseDto> convertToCourseDtoList(List<Course> courses) {
        return courses.stream()
                .map(course -> new CourseDto(
                        course.getCourseId(),
                        course.getCourseName(),
                        course.getDescription(),
                        course.getDuration(),
                        course.getMedia(),
                        course.getInstructorId().getFirstName() + course.getInstructorId().getLastName(),
                        course.getInstructorId().getUserAccountId()
                ))
                .collect(Collectors.toList());
    }






}
