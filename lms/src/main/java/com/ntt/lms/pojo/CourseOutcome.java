package com.ntt.lms.pojo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "course_outcome")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseOutcome {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
}
