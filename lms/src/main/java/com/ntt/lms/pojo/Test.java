package com.ntt.lms.pojo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "test")
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer testId;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;
    
    private String description;

    @NotNull(message = "Test type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "test_type", nullable = false)
    private TestType type;

    @Positive(message = "Duration must be positive")
    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("orderNumber ASC")
    private List<TestSection> sections = new ArrayList<>();


}
