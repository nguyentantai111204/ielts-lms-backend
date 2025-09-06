package com.ntt.lms.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "lesson")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="lesson_id")
    private int lessonId;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "course_id", referencedColumnName = "course_id")
    private Course courseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id")
    private Skill skill;

    private String lessonName;
    @Column(name = "lesson_description", length = 2000)
    private String lessonDescription;

    @Column(name = "lesson_order")
    private int lessonOrder;

    @JsonProperty("OTP")
    private String OTP;

    private String content;

    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date creationTime;

    @PrePersist
    protected void onCreate() {
        if (creationTime == null) {
            creationTime = new Date();
        }
    }
}




