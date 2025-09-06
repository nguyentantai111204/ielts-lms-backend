package com.ntt.lms.service;

import com.ntt.lms.pojo.UsersType;
import com.ntt.lms.repository.UsersTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsersTypeService {
    private final UsersTypeRepository usersTypeRepository;

    public List<UsersType> getAll() {
        return usersTypeRepository.findAll();
    }
}
