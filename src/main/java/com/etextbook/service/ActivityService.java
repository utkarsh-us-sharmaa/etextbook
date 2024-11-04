// service/ActivityService.java
package com.etextbook.service;
// import com.etextbook.service.NotificationService;
import com.etextbook.dao.*;
import com.etextbook.model.*;
import com.etextbook.service.exception.ServiceException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ActivityService {
    private final ActivityDAO activityDAO;
    private final StudentActivityDAO studentActivityDAO;
    private final ParticipationPointsDAO participationPointsDAO;
    private final CourseCustomizationDAO customizationDAO;
    private final NotificationService notificationService;

    public ActivityService(ActivityDAO activityDAO, 
                         StudentActivityDAO studentActivityDAO,
                         ParticipationPointsDAO participationPointsDAO,
                         CourseCustomizationDAO customizationDAO,
                         NotificationService notificationService) {
        this.activityDAO = activityDAO;
        this.studentActivityDAO = studentActivityDAO;
        this.participationPointsDAO = participationPointsDAO;
        this.customizationDAO = customizationDAO;
        this.notificationService = notificationService;
    }

    // Activity Creation and Management
    public Activity createActivity(Activity activity, User creator) {
        if (!canManageActivities(creator)) {
            throw new ServiceException("Unauthorized to create activities");
        }

        validateActivityData(activity);
        
        try {
            activityDAO.save(activity);
            
            // Create customization record if needed
            if (!"Faculty".equals(creator.getRole())) {
                createCustomizationRecord(activity, creator);
            }
            
            return activity;
        } catch (Exception e) {
            throw new ServiceException("Error creating activity", e);
        }
    }

    public void updateActivity(Activity activity, User updater) {
        if (!canManageActivities(updater)) {
            throw new ServiceException("Unauthorized to update activities");
        }

        validateActivityData(activity);
        activityDAO.update(activity);
    }

    // Student Activity Handling
    public StudentActivity submitActivityResponse(String studentId, Integer activityId, 
                                                String courseId, String answer) {
        Activity activity = activityDAO.findById(activityId)
            .orElseThrow(() -> new ServiceException("Activity not found"));

        // Calculate score based on answer
        int score = calculateScore(activity, answer);

        StudentActivity studentActivity = new StudentActivity();
        studentActivity.setStudentId(studentId);
        studentActivity.setActivityId(activityId);
        studentActivity.setCourseId(courseId);
        studentActivity.setAttemptDate(LocalDateTime.now());
        studentActivity.setScore(score);

        try {
            // Save student activity
            studentActivityDAO.save(studentActivity);

            // Update participation points
            updateParticipationPoints(studentId, courseId, score);

            // Send notification if perfect score
            if (score == 3) {
                notificationService.sendPerfectScoreNotification(studentId, activityId);
            }

            return studentActivity;
        } catch (Exception e) {
            throw new ServiceException("Error submitting activity response", e);
        }
    }

    // Activity Statistics and Reports
    public ActivityStatistics getActivityStatistics(Integer activityId, String courseId) {
        List<StudentActivity> attempts = studentActivityDAO.findByActivity(activityId);
        
        ActivityStatistics stats = new ActivityStatistics();
        stats.setTotalAttempts(attempts.size());
        stats.setAverageScore(calculateAverageScore(attempts));
        stats.setPerfectScores(countPerfectScores(attempts));
        
        return stats;
    }

    public List<StudentActivity> getStudentProgress(String studentId, String courseId) {
        return studentActivityDAO.findByStudentAndCourse(studentId, courseId);
    }

    // Helper Methods
    private void validateActivityData(Activity activity) {
        if (activity.getQuestion() == null || activity.getQuestion().trim().isEmpty()) {
            throw new ServiceException("Question is required");
        }
        if (activity.getCorrectAnswer() == null || activity.getCorrectAnswer().trim().isEmpty()) {
            throw new ServiceException("Correct answer is required");
        }
        if (activity.getIncorrectAnswer1() == null || activity.getIncorrectAnswer1().trim().isEmpty() ||
            activity.getIncorrectAnswer2() == null || activity.getIncorrectAnswer2().trim().isEmpty() ||
            activity.getIncorrectAnswer3() == null || activity.getIncorrectAnswer3().trim().isEmpty()) {
            throw new ServiceException("All incorrect answers are required");
        }
    }

    private boolean canManageActivities(User user) {
        return List.of("Faculty", "TA").contains(user.getRole());
    }

    private void createCustomizationRecord(Activity activity, User creator) {
        CourseCustomization customization = new CourseCustomization();
        customization.setActivityId(activity.getActivityId());
        customization.setAddedByRole(creator.getRole());
        customization.setCreatedByUserId(creator.getUserId());
        customization.setIsOriginalContent(false);
        
        customizationDAO.save(customization);
    }

    private int calculateScore(Activity activity, String studentAnswer) {
        if (activity.getCorrectAnswer().equalsIgnoreCase(studentAnswer)) {
            return 3; // Perfect score
        }
        return 0; // Incorrect answer
    }

    private void updateParticipationPoints(String studentId, String courseId, int score) {
        Optional<ParticipationPoints> existingPoints = 
            participationPointsDAO.findByStudentAndCourse(studentId, courseId);

        if (existingPoints.isPresent()) {
            participationPointsDAO.updatePoints(studentId, courseId, score);
        } else {
            ParticipationPoints newPoints = new ParticipationPoints();
            newPoints.setStudentId(studentId);
            newPoints.setCourseId(courseId);
            newPoints.setTotalPoints(score);
            newPoints.setMaxPoints(score);
            participationPointsDAO.save(newPoints);
        }
    }

    private double calculateAverageScore(List<StudentActivity> attempts) {
        if (attempts.isEmpty()) {
            return 0.0;
        }
        return attempts.stream()
                      .mapToInt(StudentActivity::getScore)
                      .average()
                      .orElse(0.0);
    }

    private int countPerfectScores(List<StudentActivity> attempts) {
        return (int) attempts.stream()
                           .filter(a -> a.getScore() == 3)
                           .count();
    }
}
