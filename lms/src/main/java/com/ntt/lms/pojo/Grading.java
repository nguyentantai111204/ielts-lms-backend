package com.ntt.lms.pojo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "grading")
public class Grading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grade_id")
    private int gradingId;
    @Column(name="grade")
    private int grade;
    @ManyToOne
    @JoinColumn(name = "quiz_id",referencedColumnName = "quiz_id")
    private Quiz quizId;
    @ManyToOne
    @JoinColumn(name="student_id",referencedColumnName = "user_account_id")
    private Student student_id;
}
