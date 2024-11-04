// dao/NotificationDAO.java
package com.etextbook.dao;

import com.etextbook.model.Notification;
import java.util.List;


public interface NotificationDAO extends BaseDAO<Notification, Integer> {
    List<Notification> findByUser(String userId);
    List<Notification> findUnreadByUser(String userId);
    boolean markAsRead(Integer notificationId);
    void markAllAsRead(String userId);
    void deleteOldNotifications(int daysOld);
}
