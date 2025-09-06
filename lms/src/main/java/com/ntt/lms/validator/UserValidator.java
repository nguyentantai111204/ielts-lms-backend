package com.ntt.lms.validator;

import com.ntt.lms.dto.UserSignUpRequest;
import com.ntt.lms.pojo.Users;
import com.ntt.lms.pojo.UsersType;
import com.ntt.lms.repository.UsersRepository;
import com.ntt.lms.repository.UsersTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UsersRepository usersRepository;
    private final UsersTypeRepository usersTypeRepository;

    //Kiểm tra xác thực người dùng
    public void validateUserAuthenticate(Users currentUser) {
        if (currentUser == null) {
            throw new IllegalArgumentException("Người dùng chưa xác thực.");
        }
    }

    // Kiểm tra tồn tại email người dùng
    public void validateUserEmail(UserSignUpRequest userSignUpRequest){
        if (usersRepository.findByEmail(userSignUpRequest.getEmail()) != null) {
            throw new IllegalArgumentException("Email đã tồn tại. Vui lòng chọn email khác!");
        }
    }

    // Kiểm tra tồn tại role người dùng
    public UsersType returnUserTypeAndvalidateUserRole(UserSignUpRequest userSignUpRequest){
        return usersTypeRepository.findById(userSignUpRequest.getUsersTypeId()).orElseThrow(()->new IllegalArgumentException("Role người dùng không hợp lệ"));
    }


}
