package com.ntt.lms.repository;

import com.ntt.lms.pojo.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Integer> {
    Users findByEmail(String email);

    @Query("SELECT MONTH(u.registrationDate), COUNT(u) " +
            "FROM Users u " +
            "WHERE YEAR(u.registrationDate) = :year " +
            "AND MONTH(u.registrationDate) BETWEEN :startMonth AND :endMonth " +
            "AND u.userType.userTypeName = 'STUDENT' " +
            "GROUP BY MONTH(u.registrationDate) " +
            "ORDER BY MONTH(u.registrationDate)")
    List<Object[]> countStudentRegistrationsByMonth(@Param("year") int year,
                                                    @Param("startMonth") int startMonth,
                                                    @Param("endMonth") int endMonth);
    
}
