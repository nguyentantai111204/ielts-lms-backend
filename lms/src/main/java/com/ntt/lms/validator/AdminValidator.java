package com.ntt.lms.validator;

import com.ntt.lms.pojo.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminValidator {

    //Kiểm tra người dùng có phải admin hay không
    public void validateHasAdminPermistion(Users users){
        if (users.getUserType().getUserTypeId() != 1) {
            throw new IllegalArgumentException("Chỉ có người quản trị mới được phép thực hiện hành động này");
        }
    }
}
