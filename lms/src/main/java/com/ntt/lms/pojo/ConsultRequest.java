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
@Table(name = "consult_request")
public class ConsultRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "consult_request_id")
    private int consultRequestId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "require_note")
    private String require;

    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "course_id", nullable = true)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(name = "student_type", nullable = false, length = 50)
    private StudentType studentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusEnum status;

    @OneToOne(mappedBy = "consultRequest", cascade = CascadeType.ALL)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "location_id", referencedColumnName = "location_id", unique = false)
    private Location location;


    public enum StatusEnum {
        ACTIVE,      // Đã tư vấn và đồng ý học
        PENDING,     // Mới gửi yêu cầu, chưa xử lý
        FAILED       // Không thể liên hệ hoặc tư vấn không thành công
    }
}
