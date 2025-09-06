package com.ntt.lms.repository;

import com.ntt.lms.pojo.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Optional<Admin> findFirstByLocation_LocationId(int locationId);
}
