package com.ntt.lms.pojo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@NotNull
public class Notifications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private int notificationsId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private Users userId;

    private String message;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Column(name = "created_at")
    private Date createdTime;

    @Column(name = "is_read")
    private boolean read;

    @Override
    public String toString() {
        return "Notifications{" +
                "notificationsId=" + notificationsId +
                ", userId=" + userId +
                ", message='" + message + '\'' +
                ", createdTime=" + createdTime +
                '}';
    }

    @PrePersist
    protected void onCreate() {
        if (createdTime == null) {
            createdTime = new Date( );
        }
    }

}
