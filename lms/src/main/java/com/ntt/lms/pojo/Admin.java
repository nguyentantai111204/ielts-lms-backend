package com.ntt.lms.pojo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "admin")
public class Admin {
    @Id
    private int userAccountId;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "user_account_id", referencedColumnName = "user_id")
    @MapsId
    private Users userId;

    private String firstName;
    private String lastName;

    @ManyToOne
    @JoinColumn(name = "location_id", referencedColumnName = "location_id", nullable = false)
    private Location location;

    public Admin(Users users) {
        this.userId = users;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "userAccountId=" + userAccountId +
                ", userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }

}
