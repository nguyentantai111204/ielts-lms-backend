package com.ntt.lms.pojo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private int courseId;

    @Column(nullable = false)
    private String courseName;

    @ManyToOne
    @JoinColumn(name = "instructor_id", referencedColumnName = "user_account_id")
    private Instructor instructorId;

    private String description;

    @Column(name = "media", nullable = true, length = 255)
    private String media;

    private int duration;

    @Column(unique = true, nullable = true, length = 100)
    private String slug;


    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date creationDate;

    @OneToMany(mappedBy = "courseId")
    private List<Lesson> lessons;

    @PrePersist
    protected void onCreate() {
        if (creationDate == null) {
            creationDate = new Date();
        }
    }

}
