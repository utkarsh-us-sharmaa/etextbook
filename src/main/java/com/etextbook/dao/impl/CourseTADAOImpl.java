package com.etextbook.dao.impl;

import com.etextbook.dao.CourseTADAO;
import com.etextbook.model.CourseTA;
import com.etextbook.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseTADAOImpl implements CourseTADAO {

    @Override
    public Optional<CourseTA> findById(Integer courseTAId) {
        String sql = "SELECT * FROM CourseTA WHERE CourseTAID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, courseTAId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToCourseTA(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding CourseTA by ID", e);
        }
    }

    @Override
    public List<CourseTA> findAll() {
        String sql = "SELECT * FROM CourseTA";
        List<CourseTA> courseTAs = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                courseTAs.add(mapResultSetToCourseTA(rs));
            }
            return courseTAs;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all CourseTA assignments", e);
        }
    }

    @Override
    public void save(CourseTA courseTA) {
        String sql = "INSERT INTO CourseTA (CourseID, TAID) VALUES (?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Check if TA is already assigned to this course
            if (isAssignedTA(courseTA.getCourseId(), courseTA.getTaId())) {
                throw new RuntimeException("TA is already assigned to this course");
            }
            
            stmt.setString(1, courseTA.getCourseId());
            stmt.setString(2, courseTA.getTaId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating CourseTA failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    courseTA.setCourseTAId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating CourseTA failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving CourseTA assignment", e);
        }
    }

    @Override
    public void update(CourseTA courseTA) {
        String sql = "UPDATE CourseTA SET CourseID = ?, TAID = ? WHERE CourseTAID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, courseTA.getCourseId());
            stmt.setString(2, courseTA.getTaId());
            stmt.setInt(3, courseTA.getCourseTAId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating CourseTA assignment", e);
        }
    }

    @Override
    public boolean delete(Integer courseTAId) {
        String sql = "DELETE FROM CourseTA WHERE CourseTAID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, courseTAId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting CourseTA assignment", e);
        }
    }

    @Override
    public List<CourseTA> findByCourse(String courseId) {
        String sql = "SELECT ct.*, u.FirstName, u.LastName, u.Email " +
                    "FROM CourseTA ct " +
                    "JOIN User u ON ct.TAID = u.UserID " +
                    "WHERE ct.CourseID = ?";
        List<CourseTA> courseTAs = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, courseId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                courseTAs.add(mapResultSetToCourseTA(rs));
            }
            return courseTAs;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding CourseTA assignments by course", e);
        }
    }

    @Override
    public List<CourseTA> findByTA(String taId) {
        String sql = "SELECT ct.*, c.Title as CourseTitle " +
                    "FROM CourseTA ct " +
                    "JOIN Course c ON ct.CourseID = c.CourseID " +
                    "WHERE ct.TAID = ?";
        List<CourseTA> courseTAs = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, taId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                courseTAs.add(mapResultSetToCourseTA(rs));
            }
            return courseTAs;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding CourseTA assignments by TA", e);
        }
    }

    @Override
    public boolean removeTA(String courseId, String taId) {
        String sql = "DELETE FROM CourseTA WHERE CourseID = ? AND TAID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, courseId);
            stmt.setString(2, taId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error removing TA from course", e);
        }
    }

    @Override
    public boolean isAssignedTA(String courseId, String taId) {
        String sql = "SELECT COUNT(*) FROM CourseTA WHERE CourseID = ? AND TAID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, courseId);
            stmt.setString(2, taId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking TA assignment", e);
        }
    }

    private CourseTA mapResultSetToCourseTA(ResultSet rs) throws SQLException {
        CourseTA courseTA = new CourseTA();
        courseTA.setCourseTAId(rs.getInt("CourseTAID"));
        courseTA.setCourseId(rs.getString("CourseID"));
        courseTA.setTaId(rs.getString("TAID"));
        
        // // Map additional fields if they exist in the result set
        // try {
        //     courseTA.setCourseName(rs.getString("CourseTitle"));
        // } catch (SQLException e) {
        //     // Ignore if the column doesn't exist
        // }
        
        return courseTA;
    }
}