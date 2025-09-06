package com.ntt.lms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequest {
    @NotEmpty(message = "Email không được để trống")
    @Email(message = "Email chưa đúng định dạng")
    private String email;
    @NotEmpty(message = "Mật khẩu không được để trống")
    private String password;
}
