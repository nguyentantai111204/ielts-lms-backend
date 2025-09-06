package com.ntt.lms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntt.lms.dto.*;
import com.ntt.lms.pojo.*;
import com.ntt.lms.repository.*;
import com.ntt.lms.utils.JwtService;
import com.ntt.lms.validator.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final EnrollmentService enrollmentService;
    private final NotificationsService notificationsService;

    private final ObjectMapper objectMapper;

    private final QuestionRepository questionRepository;
    private final QuestionTypeRepository questionTypeRepository;
    private final GradingRepository gradingRepository;
    private final LessonRepository lessonRepository;


    private final CourseValidator courseValidator;
    private final UserValidator userValidator;
    private final PermissionValidator permissionValidator;
    private final QuizValidator quizValidator;
    private final StudentValidator studentValidator;

    List<Question> quizQuestions = new ArrayList<>();
    List<Answer> quizAnswers = new ArrayList<>();
    List<Question>questionBank= new ArrayList<>();


    public int addQuiz(QuizRequestDto quizDto) throws Exception {
        Users currentUser = JwtService.getCurrentUser();

        Course course = courseValidator.validateIsExitsCourseWithId(quizDto.getCourse_id());
        Lesson lesson = lessonRepository.findById(quizDto.getLessonId()).orElseThrow();

        // Xác thực người dùng và kiểm tra quyền
        userValidator.validateUserAuthenticate(currentUser);
        courseValidator.validateInstructorHasPermissionWithCourse(currentUser, quizDto.getCourse_id());
        permissionValidator.validateHasPermissionAdminOrInstructor(currentUser);
        // Kiểm tra data request
        quizValidator.validateIsExitsQuizWithQuizTitleAndCourseId(quizDto.getTitle(), quizDto.getCourse_id());


        Quiz quiz = new Quiz();
        quiz.setTitle(quizDto.getTitle());
        quiz.setCourse(course);
        quiz.setLesson(lesson);
        quiz.setQuestionCount(quizDto.getQuestionCount());
        quiz.setRandomized(quizDto.isRandomized());
        quiz.setCreationDate(new Date());

        generateQuestions(quiz,quizDto.getTypeId(), course, quizDto.getQuestionCount());
        quizRepository.save(quiz);
        List<StudentDto> enrolledStudents = enrollmentService.viewEnrolledStudents(quizDto.getCourse_id());
        for(StudentDto student : enrolledStudents)
        {
            notificationsService.sendNotification("Một Quiz mới với Id: "+quiz.getQuizId()+" vừa được tải lên " +
                    "trong khóa: "+course.getCourseName(),student.getUserAccountId());
        }

        return quiz.getQuizId();
    }


    public void generateQuestions(Quiz quiz,int questionType, Course course_id, int questionCount) throws Exception {

        // Lấy tất cả các câu hỏi trong khóa học theo loại câu hỏi
        List<Question> allQuestions = questionRepository.findQuestionsByCourseIdAndQuestionType(course_id.getCourseId(),questionType);
        // Lấy tất cả các câu hỏi đã được gán cho khóa học nhưng chưa gán cho quiz nào
        List<Question> emptyQuestions = questionRepository.findEmptyQuestionsByCourseIdAndQuestionType(course_id.getCourseId(),questionType);

        // Điều kiện tạo quiz, trong khóa có chứa ít nhất 5 câu hỏi thuộc loại câu hỏi mà người dùng chọn
        if(allQuestions.size()< questionCount )
            throw new Exception("Khóa học chưa đủ câu hỏi để tạo quiz!\n");
        // Hoặc trong khóa có chứa ít nhất 5 câu hỏi thuộc loại câu hỏi mà người dùng chọn nhưng chưa gán cho bất kì quiz nào
        if(emptyQuestions.size() < questionCount )
            throw new Exception("Chưa đủ câu hỏi có sẵn để tạo quiz : "+emptyQuestions.size()+" type "+questionType+"\n");

        Random random = new Random();
        Set<Integer> selectedIndices = new HashSet<>();
        int count = 0;

        while (count < questionCount) {
            int randomIndex = random.nextInt(emptyQuestions.size());

            // nếu index chưa được chọn
            if (!selectedIndices.contains(randomIndex)) {
                selectedIndices.add(randomIndex);
                Question selectedQuestion = emptyQuestions.get(randomIndex); // câu câu hỏi theo index
                selectedQuestion.setQuiz(quiz); // gán vào quiz
                count++;
            }
        }
    }


    public QuizResponseDto getQuizByID(int id) {
        Users currentUser = JwtService.getCurrentUser();

        Quiz quiz = quizRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Khong tim thay quiz voi id"+id));

        Course course = courseValidator.validateIsExitsCourseWithId(quiz.getCourse().getCourseId());

        userValidator.validateUserAuthenticate(currentUser);

        if(currentUser.getUserType().getUserTypeId() == 3 || currentUser.getUserType().getUserTypeId()==1){
            courseValidator.validateInstructorHasPermissionWithCourse(currentUser, course.getCourseId());
            permissionValidator.validateHasPermissionAdminOrInstructor(currentUser);
        }
        // nếu là sinh viên
        else if(currentUser.getUserType().getUserTypeId() == 2){
            studentValidator.validateStudentEnrolledInCourse(currentUser, course);
        }
        else{
            throw new IllegalArgumentException("Bạn không có quyền xem khóa học này");
        }

        return new QuizResponseDto(
                quiz.getQuizId(),
                quiz.getTitle(),
                quiz.getCreationDate(),
                (quiz.getCourse().getCourseId()),
                quiz.getLesson().getLessonId()
        );
    }

    public String getActiveQuiz(int courseId) {
        Users currentUser = JwtService.getCurrentUser();

        userValidator.validateUserAuthenticate(currentUser);

        Course course =  courseValidator.validateIsExitsCourseWithId(courseId);
        // nếu là admin hoặc giáo viên
        if(currentUser.getUserType().getUserTypeId() == 3 || currentUser.getUserType().getUserTypeId()==1){
            courseValidator.validateInstructorHasPermissionWithCourse(currentUser, courseId);
            permissionValidator.validateHasPermissionAdminOrInstructor(currentUser);
        }
        // nếu là sinh viên
        else if(currentUser.getUserType().getUserTypeId() == 2){
            studentValidator.validateStudentEnrolledInCourse(currentUser, course);
        }
        else{
            throw new IllegalArgumentException("Bạn không có quyền xem khóa học này");
        }

        List<Quiz> quizzes = quizRepository.getQuizzesByCourseId(courseId);
        StringBuilder result = new StringBuilder();
        long currentTime = System.currentTimeMillis();
        long quizDurationMs = 100 * 60 * 1000;

        for (Quiz quiz : quizzes) {
            long creationTime = quiz.getCreationDate().getTime();
            long endTime = creationTime + quizDurationMs;

            if (endTime > currentTime) {
                long minutesLeft = (endTime - currentTime) / (60 * 1000);
                result.append("Quiz ID: ")
                        .append(quiz.getQuizId())
                        .append(" — Thời gian còn: ")
                        .append(minutesLeft)
                        .append(" phút\n");
            }
        }
        if (result.isEmpty()) {
            return "Không có quiz nào\nTổng quiz hiện tại: " + quizzes.size();
        }
        return result.toString();
    }


//    public List<QuizResponseDto> getActiveQuizByLesson(int lessonId) {
//        Users currentUser = JwtService.getCurrentUser();
//
//        userValidator.validateUserAuthenticate(currentUser);
//
//        Lesson lesson = lessonRepository.findById(lessonId)
//                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lesson với id: " + lessonId));
//
//        Course course = courseValidator.validateIsExitsCourseWithId(lesson.getCourseId().getCourseId());
//
//        // nếu là admin hoặc giáo viên
//        if (currentUser.getUserType().getUserTypeId() == 3 || currentUser.getUserType().getUserTypeId() == 1) {
//            courseValidator.validateInstructorHasPermissionWithCourse(currentUser, course.getCourseId());
//            permissionValidator.validateHasPermissionAdminOrInstructor(currentUser);
//        }
//        // nếu là sinh viên
//        else if (currentUser.getUserType().getUserTypeId() == 2) {
//            studentValidator.validateStudentEnrolledInCourse(currentUser, course);
//        } else {
//            throw new IllegalArgumentException("Bạn không có quyền xem khóa học này");
//        }
//
//        // Lấy quiz theo lesson
//        List<Quiz> quizzes = quizRepository.getQuizzesByLessonId(lessonId);
//
//        long currentTime = System.currentTimeMillis();
//        long quizDurationMs = 100 * 60 * 1000; // 100 phút
//
//        // Lọc quiz còn hiệu lực
//        return quizzes.stream()
//                .filter(quiz -> {
//                    long creationTime = quiz.getCreationDate().getTime();
//                    long endTime = creationTime + quizDurationMs;
//                    return endTime > currentTime;
//                })
//                .map(quiz -> new QuizResponseDto(
//                        quiz.getQuizId(),
//                        quiz.getTitle(),
//                        quiz.getCreationDate(),
//                        quiz.getLesson().getCourseId().getCourseId(),
//                        quiz.getLesson().getLessonId()
//                ))
//                .toList();
//    }

    public List<QuizResponseDto> getActiveQuizByLesson(int lessonId) {
        Users currentUser = JwtService.getCurrentUser();

        userValidator.validateUserAuthenticate(currentUser);

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lesson với id: " + lessonId));

        Course course = courseValidator.validateIsExitsCourseWithId(lesson.getCourseId().getCourseId());

        // nếu là admin hoặc giáo viên
        if (currentUser.getUserType().getUserTypeId() == 3 || currentUser.getUserType().getUserTypeId() == 1) {
            courseValidator.validateInstructorHasPermissionWithCourse(currentUser, course.getCourseId());
            permissionValidator.validateHasPermissionAdminOrInstructor(currentUser);
        }
        // nếu là sinh viên
        else if (currentUser.getUserType().getUserTypeId() == 2) {
            studentValidator.validateStudentEnrolledInCourse(currentUser, course);
        } else {
            throw new IllegalArgumentException("Bạn không có quyền xem khóa học này");
        }

        // Lấy quiz theo lesson
        List<Quiz> quizzes = quizRepository.getQuizzesByLessonId(lessonId);

        // Trả hết quiz mà không cần lọc thời gian
        return quizzes.stream()
                .map(quiz -> new QuizResponseDto(
                        quiz.getQuizId(),
                        quiz.getTitle(),
                        quiz.getCreationDate(),
                        quiz.getLesson().getCourseId().getCourseId(),
                        quiz.getLesson().getLessonId()
                ))
                .toList();
    }



    // Thêm các câu hỏi vào khóa
    public void createQuestionBank(int courseId, List<QuestionDto> questionList) {
        Users currentUser = JwtService.getCurrentUser();
        Course course = courseValidator.validateIsExitsCourseWithId(courseId);

        userValidator.validateUserAuthenticate(currentUser);
        permissionValidator.validateHasPermissionAdminOrInstructor(currentUser);
        courseValidator.validateInstructorHasPermissionWithCourse(currentUser,courseId);

        for (QuestionDto dto : questionList) {
            Question question = questionRepository.findById(dto.getQuestion_id())
                    .orElse(new Question());

            question.setQuestionText(dto.getQuestion_text());
            try {
                String optionsAsString = objectMapper.writeValueAsString(dto.getOptions());
                question.setOptions(optionsAsString);
            } catch (Exception e) {
                throw new RuntimeException("Chuyển sang JSON thất bại", e);
            }
            question.setCorrectAnswer(dto.getCorrect_answer());
            question.setCourseId(course);

            QuestionType questionType = questionTypeRepository.findById(dto.getType())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy loại câu hỏi"+dto.getType()));
            question.setQuestionType(questionType);

            questionRepository.save(question);
        }
    }

    public void addQuestion(QuestionDto questionDto) throws Exception {
        Users currentUser = JwtService.getCurrentUser();

        Course course = courseValidator.validateIsExitsCourseWithId(questionDto.getCourse_id());

        userValidator.validateUserAuthenticate(currentUser);
        permissionValidator.validateHasPermissionAdminOrInstructor(currentUser);
        courseValidator.validateInstructorHasPermissionWithCourse(currentUser,questionDto.getCourse_id());

        Optional<Question> optQuestion = questionRepository.findById(questionDto.getQuestion_id());
        if(optQuestion.isPresent()) throw new Exception("Cau hoi da ton tai");
        Question question = new Question();
        question.setQuestionText(questionDto.getQuestion_text()); // Set tiêu đề

        QuestionType questionType = questionTypeRepository.findById(questionDto.getType()).orElseThrow(() -> new EntityNotFoundException("No such QuestionType"+questionDto.getType()));
        question.setQuestionType(questionType); // set Loại câu hỏi

        try {
            // Chuyển String sang Json
            String optionsAsString = objectMapper.writeValueAsString(questionDto.getOptions());
            question.setOptions(optionsAsString);
        } catch (Exception e) {
            throw new RuntimeException("Chuyển sang JSON thất bại", e);
        }
        question.setCourseId(course);
        question.setCorrectAnswer(questionDto.getCorrect_answer());
        questionRepository.save(question);
    }

    public QuizResponseDto getQuestionBank(int courseId) throws Exception {
        Users currentUser = JwtService.getCurrentUser();

        Course course = courseValidator.validateIsExitsCourseWithId(courseId);

        userValidator.validateUserAuthenticate(currentUser);
        permissionValidator.validateHasPermissionAdminOrInstructor(currentUser);
        courseValidator.validateInstructorHasPermissionWithCourse(currentUser,courseId);

        QuizResponseDto quizResponseDto = new QuizResponseDto();
        questionBank = questionRepository.findQuestionByCourseId(courseId);
        if(questionBank.isEmpty()) throw new Exception("Khoa hoc chua co bat ki cau hoi nao");

        List<QuestionDto> questionDtos = new ArrayList<>();
        for (Question question : questionBank) {
            QuestionDto questionDto = new QuestionDto();
            questionDto.setQuestion_id(question.getQuestionId());
            questionDto.setCorrect_answer(question.getCorrectAnswer());
            questionDto.setQuestion_text(question.getQuestionText());
            questionDto.setType(question.getQuestionType().getTypeId());
            questionDto.setCourse_id(question.getCourseId().getCourseId());
            questionDto.setOptions(question.getOptions());
            questionDtos.add(questionDto);
        }
        quizResponseDto.setQuestionList(questionDtos);
        return quizResponseDto;
    }

    public Course findCourseByQuizId(int quizId){
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy quiz với id: "+ quizId));
        return courseValidator.validateIsExitsCourseWithId(quiz.getCourse().getCourseId());
    }

    // Nộp bài
    public void gradeQuiz(GradingDto gradingDto) throws Exception {
        Users currentUser = JwtService.getCurrentUser();
        userValidator.validateUserAuthenticate(currentUser);
        Quiz quiz = quizRepository.findById(gradingDto.getQuiz_id())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy quiz với id: " + gradingDto.getQuiz_id()));
        Course course = findCourseByQuizId(gradingDto.getQuiz_id());
        studentValidator.validateIsStudent(currentUser);
        studentValidator.validateStudentEnrolledInCourse(currentUser, course);

        if (gradingRepository.boolFindGradeByQuizAndStudentID(quiz.getQuizId(), currentUser.getUserId()).orElse(false)) {
            throw new Exception("Bạn đã nộp bài trước đó rồi!");
        }

        List<Question> gradedQuestions = questionRepository.findQuestionsByQuizId(gradingDto.getQuiz_id());
        List<String> answersList = gradingDto.getAnswers();
        int grade = 0;
        for (int i = 0; i < gradedQuestions.size(); i++) {
            if (Objects.equals(gradedQuestions.get(i).getCorrectAnswer(), answersList.get(i))) {
                grade++;
            }
        }

        Grading grading = new Grading();
        grading.setGrade(grade);
        grading.setQuizId(quiz);
        grading.setStudent_id(studentValidator.validateIsExitsStudent(currentUser.getUserId()));
        gradingRepository.save(grading);
        notificationsService.sendNotification("Quiz " + quiz.getQuizId() + " đã được nộp bởi", currentUser.getUserId());
    }


    public int quizFeedback(int quizId, int studentId) throws Exception {
        Users currentUser = JwtService.getCurrentUser();
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy quiz với id: "+ quizId));


        Course course = findCourseByQuizId(quizId);

        userValidator.validateUserAuthenticate(currentUser);
        if(currentUser.getUserType().getUserTypeId()==1 || currentUser.getUserType().getUserTypeId()==3){
            permissionValidator.validateHasPermissionAdminOrInstructor(currentUser);
            courseValidator.validateInstructorHasPermissionWithCourse(currentUser, course.getCourseId());
        }
        else if (currentUser.getUserType().getUserTypeId()==2){
            studentValidator.validateIsExitsStudent(currentUser.getUserId());
            studentValidator.validateStudentEnrolledInCourse(currentUser, course);
        }
        int grade = gradingRepository.findGradeByQuizAndStudentID(quizId, studentId);
        if(grade ==-1) throw new Exception("Bài tập chưa được chấm điểm");
        return grade;
    }

    public Map<String, Object> getQuizQuestions(int quizId) {
        Users currentUser = JwtService.getCurrentUser();
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy quiz với id: " + quizId));

        Course course = findCourseByQuizId(quizId);

        // Xác thực người dùng
        userValidator.validateUserAuthenticate(currentUser);

        int userTypeId = currentUser.getUserType().getUserTypeId();

        if (userTypeId == 1 || userTypeId == 3) { // Admin hoặc Instructor
            permissionValidator.validateHasPermissionAdminOrInstructor(currentUser);
            courseValidator.validateInstructorHasPermissionWithCourse(currentUser, course.getCourseId());
        }
        else if (userTypeId == 2) { // Student
            studentValidator.validateIsExitsStudent(currentUser.getUserId());
            studentValidator.validateStudentEnrolledInCourse(currentUser, course);

            // Kiểm tra đã nộp bài chưa
            boolean alreadySubmitted = gradingRepository
                    .boolFindGradeByQuizAndStudentID(quiz.getQuizId(), currentUser.getUserId())
                    .orElse(false);

            if (alreadySubmitted) {
                // Lấy điểm đã chấm
                int grade = gradingRepository.findGradeByQuizAndStudentID(
                        quiz.getQuizId(), currentUser.getUserId()
                );

                Map<String, Object> response = new HashMap<>();
                response.put("alreadySubmitted", true);
                response.put("grade", grade);
//                response.put("questions", Collections.emptyList());
                return response;
            }
        }

        // Nếu chưa nộp thì lấy danh sách câu hỏi
        List<Question> quizQuestions = questionRepository.findQuestionsByQuizId(quizId);

        List<QuestionDto> questions = new ArrayList<>();
        for (Question q : quizQuestions) {
            QuestionDto dto = new QuestionDto();
            dto.setOptions(q.getOptions());
            dto.setType(q.getQuestionType().getTypeId());
            dto.setQuestion_text(q.getQuestionText());
            dto.setCourse_id(q.getCourseId().getCourseId());
            dto.setQuestion_id(q.getQuestionId());

            // Chỉ Admin hoặc Instructor mới thấy đáp án đúng
            dto.setCorrect_answer((userTypeId == 1 || userTypeId == 3) ? q.getCorrectAnswer() : null);

            questions.add(dto);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("alreadySubmitted", false);
        response.put("questions", questions);

        return response;
    }



    public List<QuizGradingDTO> quizGrades(int quizId) {
        if (quizRepository.existsById(quizId)) {
            Users currentUser = JwtService.getCurrentUser();
            Quiz quiz = quizRepository.findById(quizId).get();
            List<Grading> quizGrades = gradingRepository.findAllByQuizId(quiz);

            Course course = findCourseByQuizId(quizId);

            // Validate quyền
            userValidator.validateUserAuthenticate(currentUser);
            courseValidator.validateInstructorHasPermissionWithCourse(currentUser, course.getCourseId());
            permissionValidator.validateHasPermissionAdminOrInstructor(currentUser);

            List<QuizGradingDTO> grades = new ArrayList<>();
            for (Grading grading : quizGrades) {
                Student student = grading.getStudent_id();
                StudentDto studentDto = new StudentDto(
                        student.getUserAccountId(),
                        student.getUserId().getEmail(),
                        student.getFirstName(),
                        student.getLastName()

                );

                QuizGradingDTO dto = new QuizGradingDTO(
                        grading.getGradingId(),
                        grading.getGrade(),
                        quiz.getQuizId(),
                        studentDto
                );
                grades.add(dto);
            }
            return grades;
        } else {
            throw new IllegalArgumentException("Quiz với ID " + quizId + " không tìm thấy.");
        }
    }



}
