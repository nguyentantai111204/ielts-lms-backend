package com.ntt.lms.service;

import com.ntt.lms.dto.InstructorDTO;
import com.ntt.lms.pojo.*;
import com.ntt.lms.repository.InstructorRepository;
import com.ntt.lms.repository.LocationRepository;
import com.ntt.lms.repository.UsersRepository;
import com.ntt.lms.repository.UsersTypeRepository;
import com.ntt.lms.utils.CloudinaryService;
import com.ntt.lms.utils.JwtService;
import com.ntt.lms.validator.AdminValidator;
import com.ntt.lms.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InstructorService {

    private final InstructorRepository instructorRepository;
    private final UsersRepository usersRepository;
    private final UsersTypeRepository usersTypeRepository;
    private final LocationRepository locationRepository;

    private final UserValidator userValidator;
    private final AdminValidator adminValidator;

    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder passwordEncoder;

    public Optional<Instructor> findById(int instructorId) {
        return instructorRepository.findById(instructorId);
    }

    public void updateProfile(int instructorId, InstructorDTO dto, MultipartFile file) throws IOException {
        // Lấy user hiện tại và kiểm tra quyền
        Users currentUser = JwtService.getCurrentUser();
        userValidator.validateUserAuthenticate(currentUser);
        adminValidator.validateHasAdminPermistion(currentUser);


        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giảng viên với id: " + instructorId));

        Users user = instructor.getUserId();


        if (dto.getPassword() != null && !dto.getPassword().isBlank() && !dto.getPassword().equals("********")) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }


        if (dto.getUserTypeName() != null && !dto.getUserTypeName().isBlank()) {
            UsersType userType = usersTypeRepository.findByUserTypeName(dto.getUserTypeName())
                    .orElseThrow(() -> new IllegalArgumentException("UserType không hợp lệ: " + dto.getUserTypeName()));
            user.setUserType(userType);
        }


        if (file != null && !file.isEmpty()) {
            String avatarUrl = cloudinaryService.uploadFile(file, "avatars", "image");
            user.setAvatar(avatarUrl);
        }

        usersRepository.save(user);


        if (dto.getFirstName() != null) instructor.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) instructor.setLastName(dto.getLastName());
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getAchievements() != null) instructor.setAchievements(dto.getAchievements());
        if (dto.getPosition() != null) instructor.setPosition(dto.getPosition());

        // Cập nhật location
        if (dto.getLocationId() > 0) {
            Location location = locationRepository.findById(dto.getLocationId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy location với id: " + dto.getLocationId()));
            instructor.setLocation(location);
        }

        instructorRepository.save(instructor);
    }


    public void deleteInstructor(int instructorId) {
        Users currentUser = JwtService.getCurrentUser();

        userValidator.validateUserAuthenticate(currentUser);
        adminValidator.validateHasAdminPermistion(currentUser);


        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy học sinh với id: " + instructorId));


        String avatar = instructor.getUserId().getAvatar();
        if (avatar != null && !avatar.isEmpty()) {
            try {
                cloudinaryService.deleteFile(avatar);
            } catch (IOException e) {
                System.err.println("Không xóa được avatar trên Cloudinary: " + e.getMessage());
            }
        }


        instructorRepository.delete(instructor);
    }

    public List<InstructorDTO> getAllInstructors() {
        List<Instructor> instructors = instructorRepository.findAll();
        return instructors.stream()
                .map(this::convertToDTO)
                .toList();
    }

    private InstructorDTO convertToDTO(Instructor instructor) {
        Users users = usersRepository.findById(instructor.getUserAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Instructor không tồn tại"));

        InstructorDTO dto = new InstructorDTO();
        dto.setUserAccountId(instructor.getUserAccountId());
        dto.setEmail(instructor.getUserId().getEmail());
        dto.setFirstName(instructor.getFirstName());
        dto.setLastName(instructor.getLastName());
        dto.setAchievements(instructor.getAchievements());
        dto.setPosition(instructor.getPosition());
        dto.setPassword(instructor.getUserId().getPassword());
        dto.setLocationId(instructor.getLocation() != null ? instructor.getLocation().getLocationId() : 0);
        dto.setAvatar(users.getAvatar());


        dto.setEmail(users.getEmail());
        dto.setUserTypeName(users.getUserType().getUserTypeName());

        return dto;
    }

}
