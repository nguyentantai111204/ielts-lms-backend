package com.ntt.lms.service;

import com.ntt.lms.dto.*;
import com.ntt.lms.pojo.*;
import com.ntt.lms.repository.*;
import com.ntt.lms.utils.JwtService;
import com.ntt.lms.validator.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonAttendanceRepository lessonAttendanceRepository;
    private final SubLessonRepository subLessonRepository;

    private final UserValidator userValidator;
    private final LessonValidator lessonValidator;
    private final PermissionValidator permissionValidator;
    private final CourseValidator courseValidator;
    private final StudentValidator studentValidator;
    private final InstructorValidator instructorValidator;


    public void addLesson(Lesson lesson) {
        Users currentUser = JwtService.getCurrentUser();
        userValidator.validateUserAuthenticate(currentUser);
        permissionValidator.validateHasPermissionAdminOrInstructor(currentUser);

        int courseId = lesson.getCourseId().getCourseId();
        Course course = courseValidator.validateIsExitsCourseWithId(courseId);

        lesson.setCourseId(course);

        lessonValidator.validateIsExitsLessonWithName(lesson.getLessonName(), course);
        courseValidator.validateInstructorHasPermissionWithCourse(currentUser, courseId);
        lessonValidator.validateDataRequest(lesson);

        lesson.setCreationTime(new Date(System.currentTimeMillis()));
        lessonRepository.save(lesson);
    }



    public List<LessonDto> getLessonsByCourseId(int courseId) {
        Users currentUser = JwtService.getCurrentUser();
        userValidator.validateUserAuthenticate(currentUser);

        Course course = courseValidator.validateIsExitsCourseWithId(courseId);

        if(currentUser.getUserType().getUserTypeId() == 2){
            Student student = studentValidator.validateIsExitsStudent(currentUser.getUserId());
            boolean enrolled = enrollmentRepository.existsByStudentAndCourse(student, course);

            if(!enrolled){
                throw new IllegalArgumentException("Bạn chưa tham gia khóa học này.");
            }
        }

        List<Lesson> lessons = lessonRepository.findByCourseId(course);
        return convertToLessonDtoList(lessons, courseId);
    }

    public List<LessonPreviewDto> getCourseLessonPreview(int courseId) {
        Course course = courseValidator.validateIsExitsCourseWithId(courseId);

        List<Lesson> lessons = lessonRepository.findByCourseId(course);

        return lessons.stream()
                .map(lesson -> {
                    List<SubLesson> subLessons = subLessonRepository.findByLessonId(lesson);
                    List<SubLessonPreviewDto> subLessonDtos = subLessons.stream()
                            .map(sl -> new SubLessonPreviewDto(sl.getSubName(), sl.getTime()))
                            .collect(Collectors.toList());

                    return new LessonPreviewDto(
                            lesson.getLessonId(),
                            lesson.getLessonName(),
                            lesson.getLessonOrder(),
                            subLessonDtos
                    );
                })
                .collect(Collectors.toList());
    }




    private List<LessonDto> convertToLessonDtoList(List<Lesson> lessons, int courseId) {
        return lessons.stream()
                .map(lesson -> new LessonDto(
                        lesson.getLessonId(),
                        courseId,
                        lesson.getLessonName(),
                        lesson.getLessonDescription(),
                        lesson.getLessonOrder(),
                        lesson.getOTP(),
                        lesson.getContent(),
                        lesson.getCreationTime()
                ))
                .collect(Collectors.toList());
    }

    public LessonDto getLessonById(int lessonId) {
        Users currentUser = JwtService.getCurrentUser();
        userValidator.validateUserAuthenticate(currentUser);

        Lesson lesson = lessonValidator.validateIsExitsLessonWithId(lessonId);
        return new LessonDto(
                lesson.getLessonId(),
                lesson.getCourseId().getCourseId(),
                lesson.getLessonName(),
                lesson.getLessonDescription(),
                lesson.getLessonOrder(),
                lesson.getOTP(),
                lesson.getContent(),
                lesson.getCreationTime()
        );
    }

    public void updateLesson(int lessonId, Lesson updatedLesson) {
        Users currentUser = JwtService.getCurrentUser();
        userValidator.validateUserAuthenticate(currentUser);
        permissionValidator.validateHasPermissionAdminOrInstructor(currentUser);

        Lesson lesson = lessonValidator.validateIsExitsLessonWithId(lessonId);

        lesson.setLessonName(updatedLesson.getLessonName());
        lesson.setLessonDescription(updatedLesson.getLessonDescription());
        lesson.setLessonOrder(updatedLesson.getLessonOrder());
        lesson.setContent(updatedLesson.getContent());
        lesson.setOTP(updatedLesson.getOTP());

        lessonRepository.save(lesson);
    }
    public void deleteLesson(int lessonId, int courseId) {
        Users currentUser = JwtService.getCurrentUser();
        userValidator.validateUserAuthenticate(currentUser);
        courseValidator.validateIsExitsCourseWithId(courseId);
        courseValidator.validateInstructorHasPermissionWithCourse(currentUser,courseId);

        Lesson lesson = lessonValidator.validateIsExitsLessonWithId(lessonId);

        if(lesson != null){
            lessonRepository.deleteById(lessonId);
        }
    }



    public void studentEnterLesson(int courseId, int lessonId, String otp) {
        Users currentUser = JwtService.getCurrentUser();
        userValidator.validateUserAuthenticate(currentUser);

        Course course = courseValidator.validateIsExitsCourseWithId(courseId);
        Lesson lesson = lessonValidator.validateIsExitsLessonWithId(lessonId);

        //Nếu là giáo viên của khóa học → cho phép vào lesson mà KHÔNG kiểm tra OTP hay enrollment
        if (course.getInstructorId().getUserAccountId() == currentUser.getUserId()) {
            // Cho phép truy cập lesson ngay
            return;
        }

        //Nếu không phải giáo viên thì phải kiểm tra tham gia khóa học
        List<Enrollment> enrollments = enrollmentRepository.findByCourse(course);
        boolean isEnrolled = enrollments.stream()
                .anyMatch(e -> e.getStudent().getUserAccountId() == currentUser.getUserId());

        if (!isEnrolled) {
            throw new IllegalArgumentException("Bạn chưa tham gia khóa học");
        }

        //Kiểm tra OTP nếu là học viên
        if (!Objects.equals(lesson.getOTP(), otp)) {
            throw new IllegalArgumentException("OTP không đúng.");
        }

        //Ghi nhận điểm danh nếu chưa điểm danh
        Student student = new Student();
        student.setUserAccountId(currentUser.getUserId());

        boolean enteredAlready = lessonAttendanceRepository.existsByLessonIdAndStudentId(lesson, student);
        if (enteredAlready) {
            return;
        }

        LessonAttendance lessonAttendance = new LessonAttendance();
        lessonAttendance.setLessonId(lesson);
        lessonAttendance.setStudentId(student);
        lessonAttendanceRepository.save(lessonAttendance);
    }


    public List<StudentDto> lessonAttendance(int lessonId) {
        Users currentUser = JwtService.getCurrentUser();
        userValidator.validateUserAuthenticate(currentUser);
        permissionValidator.validateHasPermissionAdminOrInstructor(currentUser);

        Lesson lesson = lessonValidator.validateIsExitsLessonWithId(lessonId);

        List<LessonAttendance> lessonAttendances = lessonAttendanceRepository.findAllByLessonId(lesson);

        List<StudentDto> attendances = new ArrayList<>();
        for (LessonAttendance lessonAttendance : lessonAttendances) {
            Student student = lessonAttendance.getStudentId();
            StudentDto dto = new StudentDto();
            dto.setUserAccountId(student.getUserAccountId());
            dto.setFirstName(student.getFirstName());
            dto.setLastName(student.getLastName());
            dto.setEmail(student.getUserId().getEmail());
            attendances.add(dto);
        }
        return attendances;
    }

    public List<StudentAttendanceDto> getAttendanceStatus(int lessonId) {
        Users currentUser = JwtService.getCurrentUser();
        userValidator.validateUserAuthenticate(currentUser);
        instructorValidator.validateHasPermissionInstructor(currentUser);

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityNotFoundException("Buổi học không tồn tại"));

        Course course = lesson.getCourseId();

        // Lấy tất cả sinh viên đã đăng ký khóa học (Enrollment)
        List<Enrollment> enrollments = enrollmentRepository.findByCourse(course);
        List<Student> students = enrollments.stream()
                .map(Enrollment::getStudent)
                .toList();

        // Lấy danh sách điểm danh
        List<LessonAttendance> attendances = lessonAttendanceRepository.findAllByLessonId(lesson);
        Set<Integer> attendedIds = attendances.stream()
                .map(a -> a.getStudentId().getUserAccountId())
                .collect(Collectors.toSet());

        // Trả về DTO
        return students.stream()
                .map(s -> new StudentAttendanceDto(
                        s.getUserAccountId(),
                        s.getFirstName(),
                        s.getLastName(),
                        s.getUserId().getEmail(),
                        attendedIds.contains(s.getUserAccountId()) // true nếu có điểm danh
                ))
                .collect(Collectors.toList());
    }






}
