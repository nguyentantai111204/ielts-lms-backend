package com.ntt.lms.controller;

import com.ntt.lms.dto.*;
import com.ntt.lms.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quiz")
public class QuizController {

    private final QuizService quizService;

    @PostMapping("/add_quiz")
    public ResponseEntity<String> addQuiz(@RequestBody QuizRequestDto quizDto){
        try {
            int quiz_id = quizService.addQuiz(quizDto);
            return ResponseEntity.ok("Đã tạo bài kiểm tra thành công. Sử dụng id: "+quiz_id+" để vào bài kiểm tra");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/quiz_id/{id}")
    public ResponseEntity<?> getQuizById(@PathVariable int id) {
        try {
            QuizResponseDto quizDTO = quizService.getQuizByID(id);
            return ResponseEntity.ok(quizDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // lấy các bài được kích hoạt
    @GetMapping("/active_quiz/{course_id}")
    public ResponseEntity<?> getActiveQuiz(@PathVariable int course_id) {
        try {
            String quiz_id = quizService.getActiveQuiz(course_id);
            return ResponseEntity.ok(quiz_id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/active_quiz_by_lesson/{lessonId}")
    public ResponseEntity<?> getActiveQuizByLesson(@PathVariable int lessonId) {
        try {
            List<QuizResponseDto> quizzes = quizService.getActiveQuizByLesson(lessonId);
            return ResponseEntity.ok(quizzes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/add_questions_bank")
    public ResponseEntity<?> addQuestionsBank(@RequestBody QuizRequestDto quizDto)
    {
        try {
            quizService.createQuestionBank(quizDto.getCourse_id(),quizDto.getQuestionList());
            return ResponseEntity.ok("Thêm các câu hỏi mới thành công vào khóa học id: "+quizDto.getCourse_id());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/add_questions")
    public ResponseEntity<?> addQuestions(@RequestBody QuestionDto questionDto)
    {
        try {
            quizService.addQuestion(questionDto);
            return ResponseEntity.ok("Câu hỏi thêm thành công vào khóa học id: "+questionDto.getCourse_id());
        } catch (Exception  e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get_question_bank/{id}")
    public ResponseEntity<?> getQuestionBank(@PathVariable int id)
    {
        try {
            QuizResponseDto quizDto = quizService.getQuestionBank(id);
            return ResponseEntity.ok(quizDto.getQuestionList());
        } catch (Exception  e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //Chấm điểm
    @PostMapping("/grade_quiz")
    public ResponseEntity<?> gradeQuiz(@RequestBody GradingDto gradingDto)
    {
        try {
            quizService.gradeQuiz(gradingDto);
            return ResponseEntity.ok("Quiz has been graded for the student");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Lấy điểm sinh viên với id bài quiz
    @GetMapping("/get_quiz_grade/{quiz_id}/student/{student_id}")
    public ResponseEntity<?> getQuizGradeByStudent(@PathVariable int quiz_id,@PathVariable int student_id)
    {
        try {
            int grade=quizService.quizFeedback(quiz_id,student_id);
            return ResponseEntity.ok(grade);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Lấy các câu hỏi trong quiz
    @GetMapping("/get_quiz_questions/{id}")
    public ResponseEntity<?> getQuizQuestions(@PathVariable int id)
    {
        try {
            return ResponseEntity.ok(quizService.getQuizQuestions(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/grades/{quizId}")
    public ResponseEntity<List<QuizGradingDTO>> trackQuizGrades(@PathVariable int quizId) {
        try {
            List<QuizGradingDTO> grades = quizService.quizGrades(quizId);
            return ResponseEntity.ok(grades);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }





}
