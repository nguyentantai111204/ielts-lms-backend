package com.ntt.lms.service;

import com.ntt.lms.dto.UserSignUpRequest;
import com.ntt.lms.pojo.*;
import com.ntt.lms.repository.*;
import com.ntt.lms.utils.CloudinaryService;
import com.ntt.lms.utils.JwtService;
import com.ntt.lms.validator.AdminValidator;
import com.ntt.lms.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository usersRepository;
    private final AdminRepository adminRepository;
    private final LocationRepository locationRepository;
    private final InstructorRepository instructorRepository;

    private final PasswordEncoder passwordEncoder;

    private final StudentRepository studentRepository;
    private final UserValidator userValidator;
    private final AdminValidator adminValidator;

    private final CloudinaryService cloudinaryService;

    public void createUser(UserSignUpRequest userSignUpRequest) {
        Users currentUser = JwtService.getCurrentUser();

        userValidator.validateUserAuthenticate(currentUser);
        adminValidator.validateHasAdminPermistion(currentUser);
        userValidator.validateUserEmail(userSignUpRequest);

        UsersType usersType = userValidator.returnUserTypeAndvalidateUserRole(userSignUpRequest);

        Users newUser = new Users();
        newUser.setEmail(userSignUpRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(userSignUpRequest.getPassword()));
        newUser.setUserType(usersType);
        newUser.setRegistrationDate(new Date());

        Location location = locationRepository.findById(userSignUpRequest.getLocationId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Location với ID: " + userSignUpRequest.getLocationId()));


        if (userSignUpRequest.getAvatarFile() != null && !userSignUpRequest.getAvatarFile().isEmpty()) {
            try {
                System.out.println("Đang upload avatar...");
                String avatarUrl = cloudinaryService.uploadFile(userSignUpRequest.getAvatarFile());
                System.out.println("Upload thành công: " + avatarUrl);
                newUser.setAvatar(avatarUrl);
            } catch (IOException e) {
                System.err.println("Upload thất bại");
                e.printStackTrace();
                throw new RuntimeException("Lỗi khi upload avatar", e);
            }
        }

        usersRepository.save(newUser);

        switch (usersType.getUserTypeId()) {
            case 1 -> {
                Admin admin = new Admin();
                admin.setUserId(newUser);
                admin.setFirstName(userSignUpRequest.getFirstName());
                admin.setLastName(userSignUpRequest.getLastName());
                admin.setLocation(location);
                adminRepository.save(admin);
            }
            case 3 -> {
                Instructor instructor = new Instructor();
                instructor.setUserId(newUser);
                instructor.setFirstName(userSignUpRequest.getFirstName());
                instructor.setLastName(userSignUpRequest.getLastName());
                instructor.setLocation(location);
                instructorRepository.save(instructor);
            }
            case 2 -> {
                Student student = new Student();
                student.setUserId(newUser);
                student.setFirstName(userSignUpRequest.getFirstName());
                student.setLastName(userSignUpRequest.getLastName());
                student.setLocation(location);
                studentRepository.save(student);
            }
            default -> throw new IllegalArgumentException("Role không hợp lệ");
        }
    }

    public Users findByEmail(String email) {
        return this.usersRepository.findByEmail(email);
    }

    public boolean validatePassword(String password, String encodedPassword) {
        return passwordEncoder.matches(password, encodedPassword);
    }
}
