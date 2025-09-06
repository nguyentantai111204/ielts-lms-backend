package com.ntt.lms.dto;

import com.ntt.lms.pojo.Location;
import com.ntt.lms.pojo.UsersType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class UserSignUpRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private MultipartFile avatarFile;
    private int usersTypeId;
    private int locationId;
}
