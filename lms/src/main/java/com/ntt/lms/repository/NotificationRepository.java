package com.ntt.lms.repository;

import com.ntt.lms.pojo.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notifications, Integer> {
    List<Notifications> findByUserId_UserId(int userId);
    List<Notifications> findByUserId_UserIdAndReadFalse(int userId);

}
