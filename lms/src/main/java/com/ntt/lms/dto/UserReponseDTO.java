package com.ntt.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserReponseDTO {
    private int userId;
    private String email;
    private String avatar;
    private int userTypeId;
    private String role;
}
