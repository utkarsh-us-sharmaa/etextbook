package com.etextbook.service;
import com.etextbook.model.ParticipationSummary; 
import com.etextbook.dao.ParticipationPointsDAO;
import com.etextbook.dao.StudentActivityDAO;
import com.etextbook.model.ParticipationPoints;
import com.etextbook.model.StudentActivity;
import com.etextbook.service.exception.ServiceException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ParticipationService {
    private final ParticipationPointsDAO participationPointsDAO;
    private final StudentActivityDAO studentActivityDAO;
    private final NotificationService notificationService;

    public ParticipationService(ParticipationPointsDAO participationPointsDAO,
                               StudentActivityDAO studentActivityDAO,
                               NotificationService notificationService) {
        this.participationPointsDAO = participationPointsDAO;
        this.studentActivityDAO = studentActivityDAO;
        this.notificationService = notificationService;
    }

    // Points Management
    public void addPoints(String studentId, String courseId, int points) {
        if (points < 0) {
            throw new ServiceException("Points cannot be negative");
        }

        try {
            participationPointsDAO.findByStudentAndCourse(studentId, courseId)
                .ifPresentOrElse(
                    existingPoints -> updateExistingPoints(studentId, courseId, points),
                    () -> createNewParticipationPoints(studentId, courseId, points)
                );

            // Notify student of points earned
            notifyPointsEarned(studentId, courseId, points);
        } catch (Exception e) {
            throw new ServiceException("Error adding participation points", e);
        }
    }

    public void resetPoints(String studentId, String courseId) {
        try {
            participationPointsDAO.resetPoints(studentId, courseId);
            notificationService.createNotification(studentId, 
                "Your participation points for the course have been reset.");
        } catch (Exception e) {
            throw new ServiceException("Error resetting participation points", e);
        }
    }

    // Points Retrieval
    public ParticipationPoints getStudentPoints(String studentId, String courseId) {
        return participationPointsDAO.findByStudentAndCourse(studentId, courseId)
            .orElseGet(() -> createNewParticipationPoints(studentId, courseId, 0));
    }

    public List<ParticipationPoints> getCourseLeaderboard(String courseId) {
        return participationPointsDAO.findByCourse(courseId)
            .stream()
            .sorted((p1, p2) -> p2.getTotalPoints() - p1.getTotalPoints())
            .collect(Collectors.toList());
    }

    public Map<String, Integer> getStudentRankings(String courseId) {
        List<ParticipationPoints> allPoints = participationPointsDAO.findByCourse(courseId);
        
        // Sort by points in descending order and assign ranks
        return allPoints.stream()
            .sorted((p1, p2) -> p2.getTotalPoints() - p1.getTotalPoints())
            .collect(Collectors.toMap(
                ParticipationPoints::getStudentId,
                points -> allPoints.indexOf(points) + 1
            ));
    }

    // Activity Performance Analysis
    public double getStudentAverageScore(String studentId, String courseId) {
        List<StudentActivity> activities = studentActivityDAO.findByStudentAndCourse(studentId, courseId);
        if (activities.isEmpty()) {
            return 0.0;
        }
        return activities.stream()
            .mapToInt(StudentActivity::getScore)
            .average()
            .orElse(0.0);
    }

    public ParticipationSummary getParticipationSummary(String studentId, String courseId) {
        ParticipationPoints points = getStudentPoints(studentId, courseId);
        List<StudentActivity> activities = studentActivityDAO.findByStudentAndCourse(studentId, courseId);
        
        ParticipationSummary summary = new ParticipationSummary();
        summary.setTotalPoints(points.getTotalPoints());
        summary.setMaxPoints(points.getMaxPoints());
        summary.setActivitiesCompleted(activities.size());
        summary.setPerfectScores(countPerfectScores(activities));
        summary.setAverageScore(calculateAverageScore(activities));
        summary.setRank(getStudentRank(studentId, courseId));
        
        return summary;
    }

    // Helper Methods
    private void updateExistingPoints(String studentId, String courseId, int points) {
        participationPointsDAO.updatePoints(studentId, courseId, points);
    }

    private ParticipationPoints createNewParticipationPoints(String studentId, String courseId, int points) {
        ParticipationPoints newPoints = new ParticipationPoints();
        newPoints.setStudentId(studentId);
        newPoints.setCourseId(courseId);
        newPoints.setTotalPoints(points);
        newPoints.setMaxPoints(points);
        participationPointsDAO.save(newPoints);
        return newPoints;
    }

    private void notifyPointsEarned(String studentId, String courseId, int points) {
        String message = String.format("You earned %d participation points!", points);
        notificationService.createNotification(studentId, message);
    }

    private int countPerfectScores(List<StudentActivity> activities) {
        return (int) activities.stream()
            .filter(a -> a.getScore() == 3)
            .count();
    }

    private double calculateAverageScore(List<StudentActivity> activities) {
        if (activities.isEmpty()) {
            return 0.0;
        }
        return activities.stream()
            .mapToInt(StudentActivity::getScore)
            .average()
            .orElse(0.0);
    }

    private int getStudentRank(String studentId, String courseId) {
        Map<String, Integer> rankings = getStudentRankings(courseId);
        return rankings.getOrDefault(studentId, 0);
    }
}
