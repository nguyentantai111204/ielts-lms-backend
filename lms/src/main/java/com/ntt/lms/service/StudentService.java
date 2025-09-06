package com.ntt.lms.service;

import com.ntt.lms.dto.ChangePasswordRequest;
import com.ntt.lms.dto.StudentInfoDTO;
import com.ntt.lms.dto.StudentProfileDTO;
import com.ntt.lms.pojo.Location;
import com.ntt.lms.pojo.Student;
import com.ntt.lms.pojo.Users;
import com.ntt.lms.pojo.UsersType;
import com.ntt.lms.repository.LocationRepository;
import com.ntt.lms.repository.StudentRepository;
import com.ntt.lms.repository.UsersRepository;
import com.ntt.lms.repository.UsersTypeRepository;
import com.ntt.lms.utils.CloudinaryService;
import com.ntt.lms.utils.JwtService;
import com.ntt.lms.validator.AdminValidator;
import com.ntt.lms.validator.StudentValidator;
import com.ntt.lms.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final UsersRepository usersRepository;
    private final LocationRepository locationRepository;
    private final UsersTypeRepository usersTypeRepository;

    private final CloudinaryService cloudinaryService;

    private final UserValidator userValidator;
    private final AdminValidator adminValidator;
    private final StudentValidator studentValidator;

    private final PasswordEncoder passwordEncoder;



    public StudentProfileDTO getStudentProfile(int studentId) {
        Users currentUser = JwtService.getCurrentUser();

        userValidator.validateUserAuthenticate(currentUser);
        studentValidator.validateStudentHasPermission(currentUser, studentId);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy học sinh với id: " + studentId));

        return new StudentProfileDTO(
                student.getUserAccountId(),
                student.getUserId().getEmail(),
                student.getFirstName(),
                student.getLastName(),
                student.getUserId().getAvatar(),
                student.getLocation() != null ? student.getLocation().getLocationName() : null,
                student.getUserId().getRegistrationDate(),
                student.getUserId().getUserType() != null ? student.getUserId().getUserType().getUserTypeName() : null
        );
    }

    public List<StudentInfoDTO> getAllStudents() {
        Users currentUser = JwtService.getCurrentUser();

        userValidator.validateUserAuthenticate(currentUser);
        adminValidator.validateHasAdminPermistion(currentUser);

        List<Student> students = studentRepository.findAll();

        return students.stream()
                .map(student -> new StudentInfoDTO(
                        student.getUserAccountId(),
                        student.getUserId().getEmail(),
                        student.getUserId().getPassword(),
                        student.getFirstName(),
                        student.getLastName(),
                        student.getUserId().getAvatar(),
                        student.getLocation() != null ? student.getLocation().getLocationName() : null,
                        student.getUserId().getRegistrationDate(),
                        student.getUserId().getUserType() != null ? student.getUserId().getUserType().getUserTypeName() : null
                ))
                .toList();
    }


    public void deleteStudent(int studentId) {
        Users currentUser = JwtService.getCurrentUser();

        userValidator.validateUserAuthenticate(currentUser);
        adminValidator.validateHasAdminPermistion(currentUser);


        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy học sinh với id: " + studentId));


        String avatar = student.getUserId().getAvatar();
        if (avatar != null && !avatar.isEmpty()) {
            try {
                cloudinaryService.deleteFile(avatar);
            } catch (IOException e) {
                System.err.println("Không xóa được avatar trên Cloudinary: " + e.getMessage());
            }
        }


        studentRepository.delete(student);
    }


    public void updateProfile(int studentId, StudentInfoDTO dto, MultipartFile file) throws IOException {
        Users currentUser = JwtService.getCurrentUser();
        userValidator.validateUserAuthenticate(currentUser);
        adminValidator.validateHasAdminPermistion(currentUser);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy học sinh với id: " + studentId));

        Users user = student.getUserId();

        if (dto.getPassword() != null && !dto.getPassword().isBlank() && !dto.getPassword().equals("********")) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getUserType() != null && !dto.getUserType().isBlank()) {
            UsersType userType = usersTypeRepository.findByUserTypeName(dto.getUserType())
                    .orElseThrow(() -> new IllegalArgumentException("UserType không hợp lệ: " + dto.getUserType()));
            user.setUserType(userType);
        }

        if (file != null && !file.isEmpty()) {
            String avatarUrl = cloudinaryService.uploadFile(file, "avatars", "image");
            user.setAvatar(avatarUrl);
        }

        usersRepository.save(user); // save Users trước

        // --- Cập nhật thông tin Student ---
        if (dto.getFirstName() != null) student.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) student.setLastName(dto.getLastName());
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getLocationName() != null && !dto.getLocationName().isBlank()) {
            Location location = locationRepository.findByLocationName(dto.getLocationName())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy location: " + dto.getLocationName()));
            student.setLocation(location);
        }

        studentRepository.save(student); // save Student sau
    }






    public String updateAvatar(int studentId, MultipartFile file) throws IOException {

        Users currentUser = JwtService.getCurrentUser();

        userValidator.validateUserAuthenticate(currentUser);
        if(currentUser.getUserType().getUserTypeId() != 1){
            studentValidator.validateStudentHasPermission(currentUser, studentId);
        }
        else {
            adminValidator.validateHasAdminPermistion(currentUser);
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy học sinh với id: " + studentId));

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File avatar không hợp lệ");
        }

        String avatarUrl = cloudinaryService.uploadFile(file, "avatars", "image");

        student.getUserId().setAvatar(avatarUrl);
        studentRepository.save(student);

        return avatarUrl;
    }


    public void changePassword(int studentId, ChangePasswordRequest request) {
        Users currentUser = JwtService.getCurrentUser();

        userValidator.validateUserAuthenticate(currentUser);
        studentValidator.validateStudentHasPermission(currentUser, studentId);

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Mật khẩu mới và xác nhận không khớp");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy học sinh với id: " + studentId));

        Users user = student.getUserId();


        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không đúng");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        studentRepository.save(student);
    }


    public long countStudent(){
        Users currentUser = JwtService.getCurrentUser();

        userValidator.validateUserAuthenticate(currentUser);
        adminValidator.validateHasAdminPermistion(currentUser);

        return studentRepository.count();
    }


    public List<Map<String, Object>> getStudentRegistrationStatsLast6Months() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int currentMonth = now.getMonthValue();

        int startMonth, endMonth;

        if (currentMonth >= 6) {
            startMonth = currentMonth - 5;
            endMonth = currentMonth;
        } else {
            startMonth = 1;
            endMonth = currentMonth;
        }

        List<Object[]> results = usersRepository.countStudentRegistrationsByMonth(year, startMonth, endMonth);


        Map<Integer, Long> stats = new LinkedHashMap<>();
        for (int m = startMonth; m <= endMonth; m++) {
            stats.put(m, 0L);
        }

        for (Object[] row : results) {
            int month = (int) row[0];
            long count = (long) row[1];
            stats.put(month, count);
        }


        List<Map<String, Object>> response = new ArrayList<>();
        for (Map.Entry<Integer, Long> entry : stats.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("month", getMonthName(entry.getKey()));
            item.put("users", entry.getValue());
            response.add(item);
        }

        return response;
    }

    private String getMonthName(int month) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return months[month - 1];
    }


}
