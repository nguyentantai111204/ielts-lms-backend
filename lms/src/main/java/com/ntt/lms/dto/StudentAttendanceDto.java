package com.ntt.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StudentAttendanceDto {
    private int userAccountId;
    private String firstName;
    private String lastName;
    private String email;
    private boolean attended;

    public StudentAttendanceDto(int id, String firstName, String lastName, String email, boolean attended) {
        this.userAccountId = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.attended = attended;
    }
}
