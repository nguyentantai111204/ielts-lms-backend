package com.ntt.lms.service;

import com.ntt.lms.dto.NotificationDTO;
import com.ntt.lms.pojo.Notifications;
import com.ntt.lms.pojo.Users;
import com.ntt.lms.repository.NotificationRepository;
import com.ntt.lms.repository.UsersRepository;
import com.ntt.lms.utils.JwtService;
import com.ntt.lms.validator.UserValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class NotificationsService {

    private final NotificationRepository notificationRepository;
    private final UsersRepository usersRepository;

    private UserValidator userValidator;


    public List<NotificationDTO> getAllNotifications(int userId) {
        Users currentUser = JwtService.getCurrentUser();
        userValidator.validateUserAuthenticate(currentUser);

        List<Notifications> notificationsList = this.notificationRepository.findAll();

        List<NotificationDTO> dtos = new ArrayList<>();
        for (Notifications notification : notificationsList) {
            if (notification.getUserId().getUserId() == userId) {
                NotificationDTO dto = new NotificationDTO(
                        notification.getNotificationsId(),
                        notification.getCreatedTime(),
                        notification.getMessage(),
                        notification.isRead(),
                        notification.getUserId().getUserId()
                );
                dtos.add(dto);
            }
        }
        return dtos;
    }

    public List<NotificationDTO> getAllUnreadNotifications(int userId) {
        Users currentUser = JwtService.getCurrentUser();
        userValidator.validateUserAuthenticate(currentUser);

        List<Notifications> notificationsList = this.notificationRepository.findAll();

        List<NotificationDTO> dtos = new ArrayList<>();
        for (Notifications notification : notificationsList) {
            if (notification.getUserId().getUserId() == userId && !notification.isRead()) {
                NotificationDTO dto = new NotificationDTO(
                        notification.getNotificationsId(),
                        notification.getCreatedTime(),
                        notification.getMessage(),
                        notification.isRead(),
                        notification.getUserId().getUserId()
                );
                dtos.add(dto);
            }
        }
        return dtos;
    }




    public void markAsRead(int notificationId) {
        Notifications notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông báo ID: " + notificationId));
        notification.setRead(true);
        notificationRepository.save(notification);
    }


    public void sendNotification(String message, int studentId) {
        Users user = usersRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng ID: " + studentId));
        Notifications enrollmentNotification = new Notifications();
        enrollmentNotification.setUserId(user);
        enrollmentNotification.setRead(false);
        enrollmentNotification.setCreatedTime(new Date());
        enrollmentNotification.setMessage(message);
        notificationRepository.save(enrollmentNotification);
    }



}
