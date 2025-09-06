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
@Table(name = "test_section")
public class TestSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sectionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "section_type", nullable = false)
    private TestSectionType sectionType;

    @NotNull(message = "Order number is required")
    @Positive(message = "Order number must be positive")
    @Column(name = "order_number", nullable = false)
    private Integer orderNumber;

    @Positive(message = "Duration must be positive")
    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("orderNumber ASC")
    private List<TestPart> parts = new ArrayList<>();

}
