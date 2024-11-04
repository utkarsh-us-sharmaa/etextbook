package com.etextbook.service;

import com.etextbook.dao.NotificationDAO;
import com.etextbook.model.*;
// import com.etextbook.service.exception.ServiceException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationService {
    private final NotificationDAO notificationDAO;

    public NotificationService(NotificationDAO notificationDAO) {
        this.notificationDAO = notificationDAO;
    }

    // User-related notifications
    public void sendWelcomeNotification(User user) {
        String message = String.format("Welcome to E-Textbook Platform, %s! Your account has been created successfully.", 
                                     user.getFirstName());
        createNotification(user.getUserId(), message);
    }

    public void sendPasswordChangeNotification(User user) {
        String message = "Your password has been changed successfully. If you didn't make this change, please contact support.";
        createNotification(user.getUserId(), message);
    }

    // Course-related notifications
    public void sendCourseCreationNotification(User faculty, Course course) {
        String message = String.format("Course '%s' has been created successfully. Course ID: %s", 
                                     course.getTitle(), course.getCourseId());
        createNotification(faculty.getUserId(), message);
    }

    public void sendEnrollmentRequestNotification(Course course, User student) {
        // Notify student
        String studentMessage = String.format("Your enrollment request for course '%s' has been submitted.", 
                                            course.getTitle());
        createNotification(student.getUserId(), studentMessage);

        // Notify faculty
        String facultyMessage = String.format("New enrollment request from %s %s for course '%s'.", 
                                            student.getFirstName(), student.getLastName(), course.getTitle());
        createNotification(course.getFacultyId(), facultyMessage);
    }

    public void sendEnrollmentApprovalNotification(Enrollment enrollment) {
        String message = String.format("Your enrollment in course ID %s has been approved.", 
                                     enrollment.getCourseId());
        createNotification(enrollment.getStudentId(), message);
    }

    // Activity-related notifications
    public void sendPerfectScoreNotification(String studentId, Integer activityId) {
        String message = "Congratulations! You achieved a perfect score on the activity!";
        createNotification(studentId, message);
    }

    public void sendNewActivityNotification(Course course, Activity activity) {
        String message = String.format("New activity added to course '%s'", course.getTitle());
        
        // Notify all enrolled students
        getEnrolledStudents(course.getCourseId()).forEach(studentId -> 
            createNotification(studentId, message)
        );
    }

    // Content-related notifications
    public void sendContentUpdateNotification(Course course, String contentType) {
        String message = String.format("New %s content has been added to course '%s'", 
                                     contentType, course.getTitle());
        
        // Notify all enrolled students
        getEnrolledStudents(course.getCourseId()).forEach(studentId -> 
            createNotification(studentId, message)
        );
    }

    public void sendChapterCreationNotification(User createdBy, Chapter chapter) {
        String message = String.format("Chapter '%s' has been created successfully. Chapter ID: %s", 
                                       chapter.getTitle(), chapter.getChapterId());
        createNotification(createdBy.getUserId(), message);
    }

    public void sendSectionCreationNotification(User createdBy, Section section) {
        String message = String.format("Section '%s' has been created successfully. Section ID: %s", 
                                       section.getTitle(), section.getSectionId());
        createNotification(createdBy.getUserId(), message);
    }
    
    public void sendSectionUpdateNotification(User updatedBy, Section section) {
        String message = String.format("Section '%s' has been updated successfully. Section ID: %s", 
                                       section.getTitle(), section.getSectionId());
        createNotification(updatedBy.getUserId(), message);
    }
    
    

    // Notification management methods
    public List<Notification> getUnreadNotifications(String userId) {
        return notificationDAO.findUnreadByUser(userId);
    }

    public List<Notification> getAllNotifications(String userId) {
        return notificationDAO.findByUser(userId);
    }

    public void markAsRead(Integer notificationId) {
        notificationDAO.markAsRead(notificationId);
    }

    public void markAllAsRead(String userId) {
        notificationDAO.markAllAsRead(userId);
    }

    public void deleteOldNotifications(int daysOld) {
        notificationDAO.deleteOldNotifications(daysOld);
    }

    public void sendTextbookCreationNotification(User creator, ETextbook textbook) {
        String message = String.format("New textbook '%s' has been created successfully.", 
                                     textbook.getTitle());
        createNotification(creator.getUserId(), message);
    }

    // Helper methods
    public void createNotification(String userId, String message) {
        try {
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setMessage(message);
            notification.setNotificationDate(LocalDateTime.now());
            notification.setIsRead(false);
            
            notificationDAO.save(notification);
        } catch (Exception e) {
            // Log error but don't throw exception to prevent disrupting main operations
            System.err.println("Error creating notification: " + e.getMessage());
        }
    }

    private List<String> getEnrolledStudents(String courseId) {
        // This would typically be injected as a dependency
        // For demonstration, returning empty list
        return new ArrayList<>();
    }
}
