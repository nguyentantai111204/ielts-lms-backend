package com.ntt.lms.pojo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submission_id")

    private int submissionId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "assignment_id", referencedColumnName = "assignment_id")
    private Assignment assignmentId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "student_id", referencedColumnName = "user_account_id")
    private Student studentId;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "submission_text", length = 2000, nullable = true)
    private String submissionText;

    private Float grade;

    private String feedback;

    @Column(name = "submitted_at")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date submittedAt;
}
