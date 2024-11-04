// dao/impl/StudentActivityDAOImpl.java
package com.etextbook.dao.impl;

import com.etextbook.dao.StudentActivityDAO;
import com.etextbook.model.StudentActivity;
import com.etextbook.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentActivityDAOImpl implements StudentActivityDAO {

    @Override
    public Optional<StudentActivity> findById(Integer studentActivityId) {
        String sql = "SELECT * FROM StudentActivity WHERE StudentActivityID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentActivityId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToStudentActivity(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding student activity by ID", e);
        }
    }

    @Override
    public List<StudentActivity> findAll() {
        String sql = "SELECT * FROM StudentActivity ORDER BY AttemptDate DESC";
        List<StudentActivity> activities = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                activities.add(mapResultSetToStudentActivity(rs));
            }
            return activities;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all student activities", e);
        }
    }

    @Override
    public void save(StudentActivity activity) {
        String sql = "INSERT INTO StudentActivity (StudentID, ActivityID, CourseID, AttemptDate, Score) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            setStudentActivityParameters(stmt, activity);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating student activity failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    activity.setStudentActivityId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating student activity failed, no ID obtained.");
                }
            }
            
            // Update participation points
            updateParticipationPoints(conn, activity.getStudentId(), activity.getCourseId(), activity.getScore());
            
        } catch (SQLException e) {
            throw new RuntimeException("Error saving student activity", e);
        }
    }

    @Override
    public void update(StudentActivity activity) {
        String sql = "UPDATE StudentActivity SET StudentID = ?, ActivityID = ?, CourseID = ?, " +
                    "AttemptDate = ?, Score = ? WHERE StudentActivityID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setStudentActivityParameters(stmt, activity);
            stmt.setInt(6, activity.getStudentActivityId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating student activity", e);
        }
    }

    @Override
    public boolean delete(Integer studentActivityId) {
        String sql = "DELETE FROM StudentActivity WHERE StudentActivityID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentActivityId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting student activity", e);
        }
    }

    @Override
    public List<StudentActivity> findByStudent(String studentId) {
        String sql = "SELECT * FROM StudentActivity WHERE StudentID = ? ORDER BY AttemptDate DESC";
        List<StudentActivity> activities = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                activities.add(mapResultSetToStudentActivity(rs));
            }
            return activities;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding activities by student", e);
        }
    }

    @Override
    public List<StudentActivity> findByCourse(String courseId) {
        String sql = "SELECT * FROM StudentActivity WHERE CourseID = ? ORDER BY AttemptDate DESC";
        List<StudentActivity> activities = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, courseId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                activities.add(mapResultSetToStudentActivity(rs));
            }
            return activities;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding activities by course", e);
        }
    }

    @Override
    public List<StudentActivity> findByActivity(Integer activityId) {
        String sql = "SELECT * FROM StudentActivity WHERE ActivityID = ? ORDER BY AttemptDate DESC";
        List<StudentActivity> activities = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, activityId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                activities.add(mapResultSetToStudentActivity(rs));
            }
            return activities;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding activities by activity ID", e);
        }
    }

    @Override
    public Optional<StudentActivity> findByStudentAndActivity(String studentId, Integer activityId) {
        String sql = "SELECT * FROM StudentActivity WHERE StudentID = ? AND ActivityID = ? " +
                    "ORDER BY AttemptDate DESC LIMIT 1";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, studentId);
            stmt.setInt(2, activityId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToStudentActivity(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding activity by student and activity", e);
        }
    }

    @Override
    public double calculateAverageScore(String studentId, String courseId) {
        String sql = "SELECT AVG(Score) as AverageScore FROM StudentActivity " +
                    "WHERE StudentID = ? AND CourseID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, studentId);
            stmt.setString(2, courseId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("AverageScore");
            }
            return 0.0;
        } catch (SQLException e) {
            throw new RuntimeException("Error calculating average score", e);
        }
    }

    @Override
    public int getTotalAttempts(String studentId, Integer activityId) {
        String sql = "SELECT COUNT(*) as TotalAttempts FROM StudentActivity " +
                    "WHERE StudentID = ? AND ActivityID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, studentId);
            stmt.setInt(2, activityId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("TotalAttempts");
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting total attempts", e);
        }
    }
    @Override
    public List<StudentActivity> findByStudentAndCourse(String studentId, String courseId) {
        String sql = "SELECT * FROM StudentActivity WHERE StudentID = ? AND CourseID = ? " +
                    "ORDER BY AttemptDate DESC";
        List<StudentActivity> activities = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, studentId);
            stmt.setString(2, courseId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                activities.add(mapResultSetToStudentActivity(rs));
            }
            return activities;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding student activities by student and course", e);
        }
    }

    private void setStudentActivityParameters(PreparedStatement stmt, StudentActivity activity) 
            throws SQLException {
        stmt.setString(1, activity.getStudentId());
        stmt.setInt(2, activity.getActivityId());
        stmt.setString(3, activity.getCourseId());
        stmt.setTimestamp(4, Timestamp.valueOf(activity.getAttemptDate()));
        stmt.setInt(5, activity.getScore());
    }

    private void updateParticipationPoints(Connection conn, String studentId, String courseId, int score) 
            throws SQLException {
        String sql = "INSERT INTO ParticipationPoints (StudentID, CourseID, TotalPoints, MaxPoints) " +
                    "VALUES (?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE TotalPoints = TotalPoints + ?, " +
                    "MaxPoints = GREATEST(MaxPoints, TotalPoints + ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            stmt.setString(2, courseId);
            stmt.setInt(3, score);
            stmt.setInt(4, score);
            stmt.setInt(5, score);
            stmt.setInt(6, score);
            stmt.executeUpdate();
        }
    }

    private StudentActivity mapResultSetToStudentActivity(ResultSet rs) throws SQLException {
        StudentActivity activity = new StudentActivity();
        activity.setStudentActivityId(rs.getInt("StudentActivityID"));
        activity.setStudentId(rs.getString("StudentID"));
        activity.setActivityId(rs.getInt("ActivityID"));
        activity.setCourseId(rs.getString("CourseID"));
        activity.setAttemptDate(rs.getTimestamp("AttemptDate").toLocalDateTime());
        activity.setScore(rs.getInt("Score"));
        return activity;
    }
}