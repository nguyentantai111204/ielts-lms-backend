package com.ntt.lms.service;

import com.ntt.lms.dto.AssignmentDto;
import com.ntt.lms.dto.AssignmentSubmissionDTO;
import com.ntt.lms.dto.StudentDto;
import com.ntt.lms.dto.UploadAssignmentDto;
import com.ntt.lms.pojo.*;
import com.ntt.lms.repository.AssignmentRepository;
import com.ntt.lms.repository.EnrollmentRepository;
import com.ntt.lms.repository.LessonRepository;
import com.ntt.lms.repository.SubmissionRepository;
import com.ntt.lms.utils.CloudinaryService;
import com.ntt.lms.utils.JwtService;
import com.ntt.lms.validator.CourseValidator;
import com.ntt.lms.validator.PermissionValidator;
import com.ntt.lms.validator.StudentValidator;
import com.ntt.lms.validator.UserValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonRepository lessonRepository;


    private final UserValidator userValidator;
    private final PermissionValidator permissionValidator;
    private final CourseValidator courseValidator;
    private final StudentValidator studentValidator;

    private final NotificationsService notificationsService;
    private final EnrollmentService enrollmentService;
    private final CloudinaryService cloudinaryService;

    // Tạo chủ đề bài tập
    public void addAssignment(AssignmentDto assignmentRequest) {
        Users currentUser = JwtService.getCurrentUser();

        Course course = courseValidator.validateIsExitsCourseWithId(assignmentRequest.getCourseId());
        Lesson lesson = lessonRepository.findById(assignmentRequest.getLessonId()).orElseThrow();
        userValidator.validateUserAuthenticate(currentUser);
        permissionValidator.validateHasPermissionAdminOrInstructor(currentUser);
        courseValidator.validateInstructorHasPermissionWithCourse(currentUser, course.getCourseId());

        boolean exist = assignmentRepository.existsById(assignmentRequest.getAssignmentId());
        if (exist) {
            throw new IllegalArgumentException("Assignment đã tồn tại");
        }
        Assignment assignment = new Assignment();
        assignment.setDescription(assignmentRequest.getAssignmentDescription());
        assignment.setTitle(assignmentRequest.getAssignmentTitle());
        assignment.setDueDate(new Date());
        assignment.setCourseId(course);
        assignment.setLessonId(lesson);

        List<StudentDto> enrolledStudents = enrollmentService.viewEnrolledStudents(assignmentRequest.getCourseId());
        for(StudentDto student : enrolledStudents)
        {
            notificationsService.sendNotification("Một Assignment mới với Id: "+assignmentRequest.getAssignmentId()+" vừa được tải lên " +
                    "trong khóa: "+course.getCourseName(),student.getUserAccountId());
        }


        assignmentRepository.save(assignment);
    }

    public Map<String, Object> getAssignmentById(int assignmentId) {
        Users currentUser = JwtService.getCurrentUser();

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy assignment với ID: " + assignmentId));

        Course course = assignment.getCourseId();

        userValidator.validateUserAuthenticate(currentUser);

        boolean alreadySubmitted = false;

        int userTypeId = currentUser.getUserType().getUserTypeId();
        if (userTypeId == 1 || userTypeId == 3) {
            permissionValidator.validateHasPermissionAdminOrInstructor(currentUser);
            courseValidator.validateInstructorHasPermissionWithCourse(currentUser, course.getCourseId());
        } else if (userTypeId == 2) {
            Student student = studentValidator.validateIsExitsStudent(currentUser.getUserId());
            studentValidator.validateStudentEnrolledInCourse(currentUser, course);

            alreadySubmitted = submissionRepository.existsByStudentIdAndAssignmentId(student, assignment);
        }

        // DTO
        AssignmentDto dto = new AssignmentDto();
        dto.setAssignmentId(assignment.getAssignmentId());
        dto.setAssignmentTitle(assignment.getTitle());
        dto.setAssignmentDescription(assignment.getDescription());
        dto.setCourseId(course.getCourseId());
        dto.setLessonId(assignment.getLessonId().getLessonId());
        dto.setDueDate(assignment.getDueDate());

        // Gói response
        Map<String, Object> response = new HashMap<>();
        response.put("assignment", dto);
        response.put("alreadySubmitted", alreadySubmitted);

        return response;
    }




    // Lấy danh sách Assignment theo lessonId và trả về DTO
    public List<AssignmentDto> getAssignmentsByLessonId(int lessonId) {
        Users currentUser = JwtService.getCurrentUser();
        userValidator.validateUserAuthenticate(currentUser);

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài học với Id: " + lessonId));

        Course course = courseValidator.validateIsExitsCourseWithId(lesson.getCourseId().getCourseId());

        // Nếu là Admin hoặc Instructor
        if (currentUser.getUserType().getUserTypeId() == 1 || currentUser.getUserType().getUserTypeId() == 3) {
            permissionValidator.validateHasPermissionAdminOrInstructor(currentUser);
            courseValidator.validateInstructorHasPermissionWithCourse(currentUser, course.getCourseId());
        }
        // Nếu là Student
        else if (currentUser.getUserType().getUserTypeId() == 2) {
            Student student = studentValidator.validateIsExitsStudent(currentUser.getUserId());
            boolean isEnrolled = enrollmentRepository.existsByStudentAndCourse(student, course);
            if (!isEnrolled) {
                throw new IllegalArgumentException("Bạn chưa tham gia khóa học này");
            }
        } else {
            throw new IllegalArgumentException("Bạn không có quyền xem bài tập của bài học này");
        }

        // Map từ Entity sang DTO
        return assignmentRepository.findByLessonId(lesson)
                .stream()
                .map(AssignmentDto::new)
                .toList();

    }



    public void uploadAssignment(UploadAssignmentDto assignmentRequest) {
        Users currentUser = JwtService.getCurrentUser();

        Course course = courseValidator.validateIsExitsCourseWithId(assignmentRequest.getCourseId());
        Lesson lesson = lessonRepository.findById(assignmentRequest.getLessonId()).orElseThrow();
        Student student = studentValidator.validateIsExitsStudent(currentUser.getUserId());

        userValidator.validateUserAuthenticate(currentUser);

        boolean isExist = enrollmentRepository.existsByStudentAndCourse(student, course);
        if (!isExist) {
            throw new IllegalArgumentException("Bạn chưa tham gia khóa học");
        }

        List<Submission> submissions = submissionRepository.findByStudentId(student);
        for (Submission s : submissions) {
            if (s.getAssignmentId().getAssignmentId() == assignmentRequest.getAssignmentId()) {
                throw new IllegalArgumentException("Bạn đã nộp bài này trước đó");
            }
        }

        Assignment assignment = assignmentRepository.findById(assignmentRequest.getAssignmentId())
                .orElseGet(() -> {
                    Assignment newAssignment = new Assignment();
                    newAssignment.setAssignmentId(assignmentRequest.getAssignmentId());
                    newAssignment.setDescription(assignmentRequest.getAssignmentDescription());
                    newAssignment.setCourseId(course);
                    newAssignment.setLessonId(lesson);
                    newAssignment.setDueDate(new Date());
                    newAssignment.setTitle(assignmentRequest.getAssignmentTitle());
                    return assignmentRepository.save(newAssignment);
                });

        Submission submission = new Submission();
        submission.setAssignmentId(assignment);
        submission.setStudentId(student);
        submission.setSubmittedAt(new Date());

        if (assignmentRequest.getSubmissionText() == null && assignmentRequest.getFilePath() == null) {
            throw new IllegalArgumentException("Nội dung nộp bài không được trống");
        }

        if (assignmentRequest.getSubmissionText() != null) {
            submission.setSubmissionText(assignmentRequest.getSubmissionText());
        }

        if (assignmentRequest.getFilePath() != null && !assignmentRequest.getFilePath().isEmpty()) {
            try {
                // Upload file dạng raw để hỗ trợ pdf, docx, txt, ...
                String fileUrl = cloudinaryService.uploadFile(
                        assignmentRequest.getFilePath(),
                        "assignments",   // folder lưu trữ, bạn có thể đổi tùy ý
                        "raw"            // resource_type = raw
                );
                submission.setFilePath(fileUrl);
            } catch (IOException e) {
                throw new RuntimeException("Upload file thất bại", e);
            }
        }

        submissionRepository.save(submission);
    }




    // Giáo viên chấm điểm bài tập
    public void gradeAssignment(int studentId, int assignmentId, float grade) {
        Users currentUser = JwtService.getCurrentUser();
        userValidator.validateUserAuthenticate(currentUser);

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(()-> new IllegalArgumentException("Không tìm thấy bài tập"));

        Course course = courseValidator.validateIsExitsCourseWithId(assignment.getCourseId().getCourseId());

        permissionValidator.validateHasPermissionAdminOrInstructor(currentUser);
        courseValidator.validateInstructorHasPermissionWithCourse(currentUser, course.getCourseId());
        Student student = studentValidator.validateIsExitsStudent(studentId);

        List<Submission> submission = submissionRepository.findByStudentId(student);
        if (submission.isEmpty()) {
            throw new IllegalArgumentException("Học sinh không có bài nộp nào");
        }

        for (Submission s : submission) {
            if (s.getAssignmentId().getAssignmentId() == assignment.getAssignmentId()) {
                s.setGrade(grade);
                submissionRepository.save(s);
                return;
            }
        }
        throw new IllegalArgumentException("Học sinh không nộp bài tập này");
    }

    public void saveAssignmentFeedback(int studentId, int assignmentId, String feedback) {
        Users currentUser = JwtService.getCurrentUser();
        userValidator.validateUserAuthenticate(currentUser);

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(()-> new IllegalArgumentException("Không tìm thấy bài tập"));

        Course course = courseValidator.validateIsExitsCourseWithId(assignment.getCourseId().getCourseId());

        permissionValidator.validateHasPermissionAdminOrInstructor(currentUser);
        courseValidator.validateInstructorHasPermissionWithCourse(currentUser, course.getCourseId());
        Student student = studentValidator.validateIsExitsStudent(studentId);

        List<Submission> submission = submissionRepository.findByStudentId(student);
        if (submission.isEmpty()) {
            throw new IllegalArgumentException("Học sinh không có bài nộp nào");
        }

        for (Submission s : submission) {
            if (s.getAssignmentId().getAssignmentId() == assignment.getAssignmentId()) {
                s.setFeedback(feedback);
                submissionRepository.save(s);
                return;
            }
        }

        throw new IllegalArgumentException("Học sinh không nộp bài tập này");
    }

    public String getFeedback(int assignmentId) {
        Users currentUser = JwtService.getCurrentUser();
        userValidator.validateUserAuthenticate(currentUser);

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(()-> new IllegalArgumentException("Không tìm thấy bài tập"));

        Student student = studentValidator.validateIsExitsStudent(currentUser.getUserId());

        boolean isExist = enrollmentRepository.existsByStudentAndCourse(student, assignment.getCourseId());
        if (!isExist) {
            throw new IllegalArgumentException("Bạn chưa tham gia khóa học");
        }

        List<Submission> submission = submissionRepository.findByStudentId(student);
        if (submission.isEmpty()) {
            throw new IllegalArgumentException("Học sinh không có bài nộp nào");
        }

        String feedback = "";
        boolean isHasFeedback = false;
        for (Submission s : submission) {
            if (s.getAssignmentId().getAssignmentId() == assignment.getAssignmentId()) {
                if (s.getFeedback() == null) {
                    feedback = "Chưa có phản hồi nào";
                    break;

                } else {
                    feedback = s.getFeedback();
                    isHasFeedback = true;
                    break;
                }
            }
        }
        if (!isHasFeedback){
            throw new IllegalArgumentException("Học sinh chưa nộp bài tập này");
        }
        return feedback;
    }

    public List<AssignmentSubmissionDTO> assignmentSubmissions(int assignmentId) {
        if (!assignmentRepository.existsById(assignmentId)) {
            throw new IllegalArgumentException("Không tìm thấy bài tập với Id: " + assignmentId);
        }

        Assignment assignment = assignmentRepository.findById(assignmentId).get();
        List<Submission> submissions = submissionRepository.findAllByAssignmentId(assignment);

        Users currentUser = JwtService.getCurrentUser();
        userValidator.validateUserAuthenticate(currentUser);
        permissionValidator.validateHasPermissionAdminOrInstructor(currentUser);
        courseValidator.validateInstructorHasPermissionWithCourse(currentUser, assignment.getCourseId().getCourseId());

        List<AssignmentSubmissionDTO> result = new ArrayList<>();

        for (Submission submission : submissions) {
            Student student = submission.getStudentId();

            // Map Student -> StudentDto
            StudentDto studentDto = new StudentDto();
            studentDto.setUserAccountId(student.getUserAccountId());
            studentDto.setFirstName(student.getFirstName());
            studentDto.setLastName(student.getLastName());
            studentDto.setEmail(student.getUserId().getEmail());
            // nếu StudentDto có các trường khác, map thêm

            AssignmentSubmissionDTO dto = new AssignmentSubmissionDTO();
            dto.setSubmissionId(submission.getSubmissionId());
            dto.setFeedback(submission.getFeedback());
            dto.setFilePath(submission.getFilePath());
            dto.setGrade(submission.getGrade());
            dto.setSubmissionText(submission.getSubmissionText());
            dto.setSubmitAt(submission.getSubmittedAt());
            dto.setAssignmentId(assignment.getAssignmentId());
            dto.setStudentDto(studentDto);

            result.add(dto);
        }

        return result;
    }

}
