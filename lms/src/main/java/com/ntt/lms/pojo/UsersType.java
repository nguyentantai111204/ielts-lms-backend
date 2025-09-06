package com.ntt.lms.pojo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users_type")
public class UsersType implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_type_id")
    private int userTypeId;

    @Column(nullable = false, unique = true)
    private String userTypeName;

    @OneToMany(mappedBy = "userType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Users> users;

    @Override
    public String getAuthority() {
        return "ROLE_" + userTypeName.toUpperCase();
    }

    @Override
    public String toString() {
        return "UsersType{" +
                "userTypeId=" + userTypeId +
                ", userTypeName='" + userTypeName + '\'' +
                '}';
    }
}

