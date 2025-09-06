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
@Table(name = "question_level")
public class QuestionLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "level_id")
    private int levelId;

    @Enumerated(EnumType.STRING)
    @Column(name = "level_name", nullable = false)
    private QuestionLevel.QuestionLevelEnum levelName;

    public enum QuestionLevelEnum {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED,
        EXPERT
    }

}
