package com.ntt.lms.pojo;

import jakarta.persistence.*;
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
@Table(name = "test_part")
public class TestPart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer partId;

    @NotNull(message = "Part number is required")
    @Positive(message = "Part number must be positive")
    @Column(name = "part_number", nullable = false)
    private Integer partNumber; // Part 1..4 (Listening) or Passage 1..3 (Reading)

    @NotNull(message = "Order number is required")
    @Positive(message = "Order number must be positive")
    @Column(name = "order_number", nullable = false)
    private Integer orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private TestSection section;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;


    @Column(name = "audio_url")
    private String audioUrl; // for Listening

    @Column(name = "passage", columnDefinition = "TEXT")
    private String passage; // for Reading

    @OneToMany(mappedBy = "part", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("orderNumber ASC")
    private List<TestQuestionGroup> groups = new ArrayList<>();

}








