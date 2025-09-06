package com.ntt.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentInfoDTO {
    private int userAccountId;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String avatar;
    private String locationName;
    private Date registrationDate;
    private String userType;
}

