package com.ntt.lms.pojo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sub_lesson")
public class SubLesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_lesson_id")
    private int subLessonId;

    @Column(name = "sub_lesson_name")
    private String subName;

    private int time;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "lesson_id", referencedColumnName = "lesson_id", nullable = false)
    private Lesson lessonId;
}

