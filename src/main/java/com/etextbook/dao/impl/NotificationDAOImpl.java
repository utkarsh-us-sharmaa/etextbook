package com.etextbook.dao.impl;

import com.etextbook.dao.NotificationDAO;
import com.etextbook.model.Notification;
import com.etextbook.util.DBConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NotificationDAOImpl implements NotificationDAO {

    @Override
    public Optional<Notification> findById(Integer notificationId) {
        String sql = "SELECT * FROM Notification WHERE NotificationID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, notificationId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToNotification(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding notification by ID", e);
        }
    }

    @Override
    public List<Notification> findAll() {
        String sql = "SELECT * FROM Notification ORDER BY NotificationDate DESC";
        List<Notification> notifications = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                notifications.add(mapResultSetToNotification(rs));
            }
            return notifications;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all notifications", e);
        }
    }

    @Override
    public void save(Notification notification) {
        String sql = "INSERT INTO Notification (UserID, Message, NotificationDate, IsRead) " +
                    "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, notification.getUserId());
            stmt.setString(2, notification.getMessage());
            stmt.setTimestamp(3, Timestamp.valueOf(notification.getNotificationDate()));
            stmt.setBoolean(4, notification.getIsRead());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating notification failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    notification.setNotificationId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating notification failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving notification", e);
        }
    }

    @Override
    public void update(Notification notification) {
        String sql = "UPDATE Notification SET UserID = ?, Message = ?, " +
                    "NotificationDate = ?, IsRead = ? WHERE NotificationID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, notification.getUserId());
            stmt.setString(2, notification.getMessage());
            stmt.setTimestamp(3, Timestamp.valueOf(notification.getNotificationDate()));
            stmt.setBoolean(4, notification.getIsRead());
            stmt.setInt(5, notification.getNotificationId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating notification", e);
        }
    }

    @Override
    public boolean delete(Integer notificationId) {
        String sql = "DELETE FROM Notification WHERE NotificationID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, notificationId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting notification", e);
        }
    }

    @Override
    public List<Notification> findByUser(String userId) {
        String sql = "SELECT * FROM Notification WHERE UserID = ? ORDER BY NotificationDate DESC";
        List<Notification> notifications = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                notifications.add(mapResultSetToNotification(rs));
            }
            return notifications;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding notifications by user", e);
        }
    }

    @Override
    public List<Notification> findUnreadByUser(String userId) {
        String sql = "SELECT * FROM Notification WHERE UserID = ? AND IsRead = FALSE " +
                    "ORDER BY NotificationDate DESC";
        List<Notification> notifications = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                notifications.add(mapResultSetToNotification(rs));
            }
            return notifications;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding unread notifications", e);
        }
    }

    @Override
    public boolean markAsRead(Integer notificationId) {
        String sql = "UPDATE Notification SET IsRead = TRUE WHERE NotificationID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, notificationId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error marking notification as read", e);
        }
    }

    @Override
    public void markAllAsRead(String userId) {
        String sql = "UPDATE Notification SET IsRead = TRUE WHERE UserID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error marking all notifications as read", e);
        }
    }

    @Override
    public void deleteOldNotifications(int daysOld) {
        String sql = "DELETE FROM Notification WHERE NotificationDate < ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
            stmt.setTimestamp(1, Timestamp.valueOf(cutoffDate));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting old notifications", e);
        }
    }

    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setNotificationId(rs.getInt("NotificationID"));
        notification.setUserId(rs.getString("UserID"));
        notification.setMessage(rs.getString("Message"));
        notification.setNotificationDate(rs.getTimestamp("NotificationDate").toLocalDateTime());
        notification.setIsRead(rs.getBoolean("IsRead"));
        return notification;
    }
}