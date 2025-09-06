package com.ntt.lms.validator;

import com.ntt.lms.pojo.Instructor;
import com.ntt.lms.pojo.Users;
import com.ntt.lms.repository.InstructorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InstructorValidator {

    private final InstructorRepository instructorRepository;

    public Instructor validateIsExitsInstructor(Users user){
        return this.instructorRepository.findById(user.getUserId()).orElseThrow(()-> new IllegalArgumentException("Không tìm thấy tài khoản với Id: "+user.getUserId()));
    }

    public void validateHasPermissionInstructor(Users users){
        if(users.getUserType().getUserTypeId() != 3){
            throw new IllegalArgumentException("Chỉ giáo viên có quyền thực hiện hành động này");
        }
    }
}
