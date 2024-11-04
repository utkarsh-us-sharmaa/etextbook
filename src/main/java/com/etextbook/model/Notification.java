// model/Notification.java
package com.etextbook.model;

import java.time.LocalDateTime;

public class Notification {
    private Integer notificationId;
    private String userId;
    private String message;
    private LocalDateTime notificationDate;
    private Boolean isRead;

    // Constructors
    public Notification() {
        this.notificationDate = LocalDateTime.now();
        this.isRead = false;
    }

    public Notification(Integer notificationId, String userId, String message) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.message = message;
        this.notificationDate = LocalDateTime.now();
        this.isRead = false;
    }

    // Getters and Setters
    public Integer getNotificationId() { return notificationId; }
    public void setNotificationId(Integer notificationId) { 
        this.notificationId = notificationId; 
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getNotificationDate() { return notificationDate; }
    public void setNotificationDate(LocalDateTime notificationDate) { 
        this.notificationDate = notificationDate; 
    }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" + notificationId +
                ", userId='" + userId + '\'' +
                ", message='" + message + '\'' +
                ", notificationDate=" + notificationDate +
                ", isRead=" + isRead +
                '}';
    }
}

