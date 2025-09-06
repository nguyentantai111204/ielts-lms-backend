package com.ntt.lms.validator;

import com.ntt.lms.pojo.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PermissionValidator {

    public void validateHasPermissionAdminOrInstructor(Users users) {
        int roleId = users.getUserType().getUserTypeId();
        if (roleId != 1 && roleId != 3) {
            throw new IllegalArgumentException("Bạn không có quyền thực hiện hành động này");
        }
    }
}
