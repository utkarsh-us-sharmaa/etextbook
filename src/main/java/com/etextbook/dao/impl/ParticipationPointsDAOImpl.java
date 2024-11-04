package com.etextbook.dao.impl;

import com.etextbook.dao.ParticipationPointsDAO;
import com.etextbook.model.ParticipationPoints;
import com.etextbook.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParticipationPointsDAOImpl implements ParticipationPointsDAO {

    @Override
    public Optional<ParticipationPoints> findById(Integer participationId) {
        String sql = "SELECT * FROM ParticipationPoints WHERE ParticipationID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, participationId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToParticipationPoints(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding participation points by ID", e);
        }
    }

    @Override
    public List<ParticipationPoints> findAll() {
        String sql = "SELECT * FROM ParticipationPoints";
        List<ParticipationPoints> pointsList = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                pointsList.add(mapResultSetToParticipationPoints(rs));
            }
            return pointsList;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all participation points", e);
        }
    }

    @Override
    public void save(ParticipationPoints points) {
        String sql = "INSERT INTO ParticipationPoints (StudentID, CourseID, TotalPoints, MaxPoints) " +
                    "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            setParticipationPointsParameters(stmt, points);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating participation points failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    points.setParticipationId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating participation points failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving participation points", e);
        }
    }

    @Override
    public void update(ParticipationPoints points) {
        String sql = "UPDATE ParticipationPoints SET StudentID = ?, CourseID = ?, " +
                    "TotalPoints = ?, MaxPoints = ? WHERE ParticipationID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setParticipationPointsParameters(stmt, points);
            stmt.setInt(5, points.getParticipationId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating participation points", e);
        }
    }

    @Override
    public boolean delete(Integer participationId) {
        String sql = "DELETE FROM ParticipationPoints WHERE ParticipationID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, participationId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting participation points", e);
        }
    }

    @Override
    public List<ParticipationPoints> findByStudent(String studentId) {
        String sql = "SELECT * FROM ParticipationPoints WHERE StudentID = ?";
        List<ParticipationPoints> pointsList = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                pointsList.add(mapResultSetToParticipationPoints(rs));
            }
            return pointsList;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding participation points by student", e);
        }
    }

    @Override
    public List<ParticipationPoints> findByCourse(String courseId) {
        String sql = "SELECT * FROM ParticipationPoints WHERE CourseID = ? ORDER BY TotalPoints DESC";
        List<ParticipationPoints> pointsList = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, courseId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                pointsList.add(mapResultSetToParticipationPoints(rs));
            }
            return pointsList;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding participation points by course", e);
        }
    }

    @Override
    public Optional<ParticipationPoints> findByStudentAndCourse(String studentId, String courseId) {
        String sql = "SELECT * FROM ParticipationPoints WHERE StudentID = ? AND CourseID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, studentId);
            stmt.setString(2, courseId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToParticipationPoints(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding participation points by student and course", e);
        }
    }

    @Override
    public void updatePoints(String studentId, String courseId, int pointsToAdd) {
        String sql = "UPDATE ParticipationPoints SET TotalPoints = TotalPoints + ?, " +
                    "MaxPoints = GREATEST(MaxPoints, TotalPoints + ?) " +
                    "WHERE StudentID = ? AND CourseID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, pointsToAdd);
            stmt.setInt(2, pointsToAdd);
            stmt.setString(3, studentId);
            stmt.setString(4, courseId);
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating participation points", e);
        }
    }

    @Override
    public void resetPoints(String studentId, String courseId) {
        String sql = "UPDATE ParticipationPoints SET TotalPoints = 0 " +
                    "WHERE StudentID = ? AND CourseID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, studentId);
            stmt.setString(2, courseId);
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error resetting participation points", e);
        }
    }

    @Override
    public int getTotalPoints(String studentId, String courseId) {
        String sql = "SELECT TotalPoints FROM ParticipationPoints WHERE StudentID = ? AND CourseID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, studentId);
            stmt.setString(2, courseId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("TotalPoints");
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting total points", e);
        }
    }

    private void setParticipationPointsParameters(PreparedStatement stmt, ParticipationPoints points) 
            throws SQLException {
        stmt.setString(1, points.getStudentId());
        stmt.setString(2, points.getCourseId());
        stmt.setInt(3, points.getTotalPoints());
        stmt.setInt(4, points.getMaxPoints());
    }

    private ParticipationPoints mapResultSetToParticipationPoints(ResultSet rs) throws SQLException {
        ParticipationPoints points = new ParticipationPoints();
        points.setParticipationId(rs.getInt("ParticipationID"));
        points.setStudentId(rs.getString("StudentID"));
        points.setCourseId(rs.getString("CourseID"));
        points.setTotalPoints(rs.getInt("TotalPoints"));
        points.setMaxPoints(rs.getInt("MaxPoints"));
        return points;
    }
}