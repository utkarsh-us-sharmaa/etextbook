package com.etextbook.dao;

import com.etextbook.model.ParticipationPoints;
import java.util.List;
import java.util.Optional;

public interface ParticipationPointsDAO extends BaseDAO<ParticipationPoints, Integer> {
    List<ParticipationPoints> findByStudent(String studentId);
    List<ParticipationPoints> findByCourse(String courseId);
    Optional<ParticipationPoints> findByStudentAndCourse(String studentId, String courseId);
    void updatePoints(String studentId, String courseId, int pointsToAdd);
    void resetPoints(String studentId, String courseId);
    int getTotalPoints(String studentId, String courseId);
}