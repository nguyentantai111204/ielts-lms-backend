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
@Table(name = "lesson_attendance")
public class LessonAttendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private int attendanceId;

    @ManyToOne
    @JoinColumn(name = "lesson_id",referencedColumnName = "lesson_id")
    private Lesson lessonId;

    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "user_account_id")
    private Student studentId;
}
