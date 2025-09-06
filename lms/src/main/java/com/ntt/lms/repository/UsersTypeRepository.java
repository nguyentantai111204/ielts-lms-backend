package com.ntt.lms.repository;

import com.ntt.lms.pojo.UsersType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersTypeRepository extends JpaRepository<UsersType, Integer> {
    Optional<UsersType> findByUserTypeName(String userTypeName);
}
