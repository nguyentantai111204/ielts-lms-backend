package com.ntt.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private int notificationId;
    private Date createAt;
    private String message;
    private boolean isRead;
    private int userId;
}
