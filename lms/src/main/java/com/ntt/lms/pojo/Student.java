package com.ntt.lms.pojo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "student")
public class Student {
    @Id
    private int userAccountId;

    @OneToOne(cascade= {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_account_id", referencedColumnName = "user_id")
    @MapsId
    private Users userId;

    private String firstName;
    private String lastName;

    @OneToOne
    @JoinColumn(name = "consult_request_id", referencedColumnName = "consult_request_id")
    private ConsultRequest consultRequest;

    @ManyToOne
    @JoinColumn(name = "location_id", referencedColumnName = "location_id", nullable = false)
    private Location location;


    public Student(Users users) {
        this.userId = users;
    }

    @Override
    public String toString() {
        return "Student{" +
                "userAccountId=" + userAccountId +
                ", userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }


}
