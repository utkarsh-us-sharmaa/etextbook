package com.etextbook.dao.impl;

import com.etextbook.dao.ActivityDAO;
import com.etextbook.model.Activity;
import com.etextbook.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ActivityDAOImpl implements ActivityDAO {

    @Override
    public Optional<Activity> findById(Integer activityId) {
        String sql = "SELECT * FROM Activity WHERE ActivityID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, activityId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToActivity(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding activity by ID", e);
        }
    }

    @Override
    public List<Activity> findAll() {
        String sql = "SELECT * FROM Activity";
        List<Activity> activities = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                activities.add(mapResultSetToActivity(rs));
            }
            return activities;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all activities", e);
        }
    }

    @Override
    public void save(Activity activity) {
        String sql = "INSERT INTO Activity (Question, CorrectAnswer, IncorrectAnswer1, " +
                    "IncorrectAnswer2, IncorrectAnswer3, ExplanationCorrect, " +
                    "ExplanationIncorrect1, ExplanationIncorrect2, ExplanationIncorrect3, " +
                    "ContentBlockID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            setActivityParameters(stmt, activity);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating activity failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    activity.setActivityId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating activity failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving activity", e);
        }
    }

    @Override
    public void update(Activity activity) {
        String sql = "UPDATE Activity SET Question = ?, CorrectAnswer = ?, " +
                    "IncorrectAnswer1 = ?, IncorrectAnswer2 = ?, IncorrectAnswer3 = ?, " +
                    "ExplanationCorrect = ?, ExplanationIncorrect1 = ?, " +
                    "ExplanationIncorrect2 = ?, ExplanationIncorrect3 = ?, " +
                    "ContentBlockID = ? WHERE ActivityID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setActivityParameters(stmt, activity);
            stmt.setInt(11, activity.getActivityId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating activity", e);
        }
    }

    @Override
    public boolean delete(Integer activityId) {
        String sql = "DELETE FROM Activity WHERE ActivityID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, activityId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting activity", e);
        }
    }

    @Override
    public List<Activity> findByContentBlock(Integer contentBlockId) {
        String sql = "SELECT * FROM Activity WHERE ContentBlockID = ?";
        List<Activity> activities = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, contentBlockId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                activities.add(mapResultSetToActivity(rs));
            }
            return activities;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding activities by content block", e);
        }
    }

    @Override
    public List<Activity> findByCourse(String courseId) {
        String sql = "SELECT DISTINCT a.* FROM Activity a " +
                    "JOIN ContentBlock cb ON a.ContentBlockID = cb.ContentBlockID " +
                    "JOIN Section s ON cb.SectionID = s.SectionID " +
                    "JOIN Chapter ch ON s.ChapterID = ch.ChapterID " +
                    "JOIN Course c ON ch.TextbookID = c.TextbookID " +
                    "WHERE c.CourseID = ?";
        List<Activity> activities = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, courseId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                activities.add(mapResultSetToActivity(rs));
            }
            return activities;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding activities by course", e);
        }
    }

    @Override
    public List<Activity> findByStudent(String studentId) {
        String sql = "SELECT DISTINCT a.* FROM Activity a " +
                    "JOIN StudentActivity sa ON a.ActivityID = sa.ActivityID " +
                    "WHERE sa.StudentID = ?";
        List<Activity> activities = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                activities.add(mapResultSetToActivity(rs));
            }
            return activities;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding activities by student", e);
        }
    }

    private void setActivityParameters(PreparedStatement stmt, Activity activity) throws SQLException {
        stmt.setString(1, activity.getQuestion());
        stmt.setString(2, activity.getCorrectAnswer());
        stmt.setString(3, activity.getIncorrectAnswer1());
        stmt.setString(4, activity.getIncorrectAnswer2());
        stmt.setString(5, activity.getIncorrectAnswer3());
        stmt.setString(6, activity.getExplanationCorrect());
        stmt.setString(7, activity.getExplanationIncorrect1());
        stmt.setString(8, activity.getExplanationIncorrect2());
        stmt.setString(9, activity.getExplanationIncorrect3());
        stmt.setInt(10, activity.getContentBlockId());
    }

    private Activity mapResultSetToActivity(ResultSet rs) throws SQLException {
        Activity activity = new Activity();
        activity.setActivityId(rs.getInt("ActivityID"));
        activity.setQuestion(rs.getString("Question"));
        activity.setCorrectAnswer(rs.getString("CorrectAnswer"));
        activity.setIncorrectAnswer1(rs.getString("IncorrectAnswer1"));
        activity.setIncorrectAnswer2(rs.getString("IncorrectAnswer2"));
        activity.setIncorrectAnswer3(rs.getString("IncorrectAnswer3"));
        activity.setExplanationCorrect(rs.getString("ExplanationCorrect"));
        activity.setExplanationIncorrect1(rs.getString("ExplanationIncorrect1"));
        activity.setExplanationIncorrect2(rs.getString("ExplanationIncorrect2"));
        activity.setExplanationIncorrect3(rs.getString("ExplanationIncorrect3"));
        activity.setContentBlockId(rs.getInt("ContentBlockID"));
        return activity;
    }
}