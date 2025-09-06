package com.ntt.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstructorDTO {
    private int userAccountId;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String achievements;
    private String position;
    private String userTypeName;
    private int locationId;
    private String avatar;
}
