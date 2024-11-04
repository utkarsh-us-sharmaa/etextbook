// dao/impl/EnrollmentDAOImpl.java
package com.etextbook.dao.impl;

import com.etextbook.dao.EnrollmentDAO;
import com.etextbook.model.Enrollment;
import com.etextbook.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnrollmentDAOImpl implements EnrollmentDAO {

    @Override
    public Optional<Enrollment> findById(Integer enrollmentId) {
        String sql = "SELECT * FROM Enrollment WHERE EnrollmentID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, enrollmentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToEnrollment(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding enrollment by ID", e);
        }
    }

    @Override
    public List<Enrollment> findAll() {
        String sql = "SELECT * FROM Enrollment ORDER BY RequestDate DESC";
        List<Enrollment> enrollments = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                enrollments.add(mapResultSetToEnrollment(rs));
            }
            return enrollments;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all enrollments", e);
        }
    }

    @Override
    public void save(Enrollment enrollment) {
        String sql = "INSERT INTO Enrollment (StudentID, CourseID, Status, RequestDate, ApprovalDate) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            setEnrollmentParameters(stmt, enrollment);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating enrollment failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    enrollment.setEnrollmentId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating enrollment failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving enrollment", e);
        }
    }

    @Override
    public void update(Enrollment enrollment) {
        String sql = "UPDATE Enrollment SET StudentID = ?, CourseID = ?, Status = ?, " +
                    "RequestDate = ?, ApprovalDate = ? WHERE EnrollmentID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setEnrollmentParameters(stmt, enrollment);
            stmt.setInt(6, enrollment.getEnrollmentId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating enrollment", e);
        }
    }

    @Override
    public boolean delete(Integer enrollmentId) {
        String sql = "DELETE FROM Enrollment WHERE EnrollmentID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, enrollmentId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting enrollment", e);
        }
    }

    @Override
    public List<Enrollment> findByStudent(String studentId) {
        String sql = "SELECT * FROM Enrollment WHERE StudentID = ? ORDER BY RequestDate DESC";
        List<Enrollment> enrollments = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                enrollments.add(mapResultSetToEnrollment(rs));
            }
            return enrollments;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding enrollments by student", e);
        }
    }

    @Override
    public List<Enrollment> findByCourse(String courseId) {
        String sql = "SELECT * FROM Enrollment WHERE CourseID = ? ORDER BY RequestDate DESC";
        List<Enrollment> enrollments = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, courseId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                enrollments.add(mapResultSetToEnrollment(rs));
            }
            return enrollments;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding enrollments by course", e);
        }
    }

    @Override
    public List<Enrollment> findByStatus(String status) {
        String sql = "SELECT * FROM Enrollment WHERE Status = ? ORDER BY RequestDate DESC";
        List<Enrollment> enrollments = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                enrollments.add(mapResultSetToEnrollment(rs));
            }
            return enrollments;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding enrollments by status", e);
        }
    }

    @Override
    public Optional<Enrollment> findByStudentAndCourse(String studentId, String courseId) {
        String sql = "SELECT * FROM Enrollment WHERE StudentID = ? AND CourseID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, studentId);
            stmt.setString(2, courseId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToEnrollment(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding enrollment by student and course", e);
        }
    }

    @Override
    public boolean updateStatus(Integer enrollmentId, String status) {
        String sql = "UPDATE Enrollment SET Status = ?, ApprovalDate = ? WHERE EnrollmentID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setDate(2, status.equals("Approved") ? Date.valueOf(java.time.LocalDate.now()) : null);
            stmt.setInt(3, enrollmentId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating enrollment status", e);
        }
    }

    private void setEnrollmentParameters(PreparedStatement stmt, Enrollment enrollment) throws SQLException {
        stmt.setString(1, enrollment.getStudentId());
        stmt.setString(2, enrollment.getCourseId());
        stmt.setString(3, enrollment.getStatus());
        stmt.setDate(4, Date.valueOf(enrollment.getRequestDate()));
        stmt.setDate(5, enrollment.getApprovalDate() != null ? 
                    Date.valueOf(enrollment.getApprovalDate()) : null);
    }

    private Enrollment mapResultSetToEnrollment(ResultSet rs) throws SQLException {
        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(rs.getInt("EnrollmentID"));
        enrollment.setStudentId(rs.getString("StudentID"));
        enrollment.setCourseId(rs.getString("CourseID"));
        enrollment.setStatus(rs.getString("Status"));
        enrollment.setRequestDate(rs.getDate("RequestDate").toLocalDate());
        Date approvalDate = rs.getDate("ApprovalDate");
        if (approvalDate != null) {
            enrollment.setApprovalDate(approvalDate.toLocalDate());
        }
        return enrollment;
    }
}