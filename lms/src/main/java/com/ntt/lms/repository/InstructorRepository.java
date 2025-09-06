package com.ntt.lms.repository;

import com.ntt.lms.pojo.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstructorRepository extends JpaRepository<Instructor, Integer> {
}
