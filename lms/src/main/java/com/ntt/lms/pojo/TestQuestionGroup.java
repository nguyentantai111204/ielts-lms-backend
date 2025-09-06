package com.ntt.lms.pojo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "test_question_group")
public class TestQuestionGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer groupId;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    private String description;

    // orderNumber remains for ordering groups within a part

    @NotNull(message = "Order number is required")
    @Positive(message = "Order number must be positive")
    @Column(name = "order_number", nullable = false)
    private Integer orderNumber;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id")
    private TestPart part;

    @Column(columnDefinition = "TEXT")
    private String questionGroupText;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("questionNumber ASC")   
    private List<TestQuestion> questions = new ArrayList<>();

}
