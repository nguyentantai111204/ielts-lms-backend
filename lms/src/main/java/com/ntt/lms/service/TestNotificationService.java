package com.ntt.lms.service;

import com.ntt.lms.pojo.*;
import com.ntt.lms.repository.NotificationRepository;
import com.ntt.lms.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TestNotificationService {

    private final NotificationRepository notificationRepository;
    private final UsersRepository usersRepository;

    // Gửi thông báo khi user bắt đầu làm bài test
    @Transactional
    public void sendTestStartedNotification(int userId, int testId, String testTitle) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));
        
        Notifications notification = new Notifications();
        notification.setUserId(user);
        notification.setMessage("Bạn đã bắt đầu làm bài test: " + testTitle);
        notification.setCreatedTime(new Date());
        notification.setRead(false);
        
        notificationRepository.save(notification);
    }

    // Gửi thông báo khi user hoàn thành bài test
    @Transactional
    public void sendTestCompletedNotification(int userId, int testId, String testTitle, Double score) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));
        
        String message = "Bạn đã hoàn thành bài test: " + testTitle;
        if (score != null) {
            message += " với điểm số: " + String.format("%.2f", score * 100) + "%";
        }
        
        Notifications notification = new Notifications();
        notification.setUserId(user);
        notification.setMessage(message);
        notification.setCreatedTime(new Date());
        notification.setRead(false);
        
        notificationRepository.save(notification);
    }

    // Gửi thông báo khi có bài test mới
    @Transactional
    public void sendNewTestNotification(List<Integer> userIds, int testId, String testTitle) {
        for (Integer userId : userIds) {
            Users user = usersRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));
            
            Notifications notification = new Notifications();
            notification.setUserId(user);
            notification.setMessage("Có bài test mới: " + testTitle);
            notification.setCreatedTime(new Date());
            notification.setRead(false);
            
            notificationRepository.save(notification);
        }
    }

    // Gửi thông báo nhắc nhở làm bài test
    @Transactional
    public void sendTestReminderNotification(int userId, int testId, String testTitle) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));
        
        Notifications notification = new Notifications();
        notification.setUserId(user);
        notification.setMessage("Nhắc nhở: Bạn chưa hoàn thành bài test: " + testTitle);
        notification.setCreatedTime(new Date());
        notification.setRead(false);
        
        notificationRepository.save(notification);
    }

    // Gửi thông báo khi có kết quả bài test
    @Transactional
    public void sendTestResultNotification(int userId, int testId, String testTitle, Double score) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));
        
        String message = "Kết quả bài test: " + testTitle;
        if (score != null) {
            if (score >= 0.8) {
                message += " - Xuất sắc! (" + String.format("%.2f", score * 100) + "%)";
            } else if (score >= 0.6) {
                message += " - Tốt! (" + String.format("%.2f", score * 100) + "%)";
            } else if (score >= 0.4) {
                message += " - Trung bình (" + String.format("%.2f", score * 100) + "%)";
            } else {
                message += " - Cần cải thiện (" + String.format("%.2f", score * 100) + "%)";
            }
        }
        
        Notifications notification = new Notifications();
        notification.setUserId(user);
        notification.setMessage(message);
        notification.setCreatedTime(new Date());
        notification.setRead(false);
        
        notificationRepository.save(notification);
    }

    // Gửi thông báo khi có bài test sắp hết hạn
    @Transactional
    public void sendTestExpiryWarningNotification(int userId, int testId, String testTitle, long remainingMinutes) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));
        
        String timeUnit = remainingMinutes < 60 ? "phút" : "giờ";
        long displayTime = remainingMinutes < 60 ? remainingMinutes : remainingMinutes / 60;
        
        Notifications notification = new Notifications();
        notification.setUserId(user);
        notification.setMessage("Cảnh báo: Bài test " + testTitle + " sẽ hết hạn sau " + displayTime + " " + timeUnit);
        notification.setCreatedTime(new Date());
        notification.setRead(false);
        
        notificationRepository.save(notification);
    }

    // Gửi thông báo khi user đạt thành tích mới
    @Transactional
    public void sendAchievementNotification(int userId, String achievement, String description) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));
        
        Notifications notification = new Notifications();
        notification.setUserId(user);
        notification.setMessage("🎉 Thành tích mới: " + achievement + " - " + description);
        notification.setCreatedTime(new Date());
        notification.setRead(false);
        
        notificationRepository.save(notification);
    }

    // Gửi thông báo tổng hợp hàng tuần
    @Transactional
    public void sendWeeklySummaryNotification(int userId, int testsCompleted, Double averageScore, int improvementRank) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));
        
        String message = "📊 Tóm tắt tuần: Bạn đã hoàn thành " + testsCompleted + " bài test";
        if (averageScore != null) {
            message += " với điểm trung bình " + String.format("%.2f", averageScore * 100) + "%";
        }
        if (improvementRank > 0) {
            message += ". Xếp hạng cải thiện: " + improvementRank;
        }
        
        Notifications notification = new Notifications();
        notification.setUserId(user);
        notification.setMessage(message);
        notification.setCreatedTime(new Date());
        notification.setRead(false);
        
        notificationRepository.save(notification);
    }
}



















