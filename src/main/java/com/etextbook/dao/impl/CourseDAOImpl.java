package com.etextbook.dao.impl;

import com.etextbook.dao.CourseDAO;
import com.etextbook.model.Course;
import com.etextbook.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseDAOImpl implements CourseDAO {

    @Override
    public Optional<Course> findById(String courseId) {
        String sql = "SELECT * FROM Course WHERE CourseID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, courseId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToCourse(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding course by ID", e);
        }
    }

    @Override
    public List<Course> findAll() {
        String sql = "SELECT * FROM Course ORDER BY StartDate DESC";
        List<Course> courses = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }
            return courses;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all courses", e);
        }
    }

    @Override
    public void save(Course course) {
        String sql = "INSERT INTO Course (CourseID, Title, CourseType, Token, Capacity, " +
                    "StartDate, EndDate, FacultyID, TextbookID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setCourseParameters(stmt, course);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Error saving course", e);
        }
    }

    @Override
    public void update(Course course) {
        String sql = "UPDATE Course SET Title = ?, CourseType = ?, Token = ?, " +
                    "Capacity = ?, StartDate = ?, EndDate = ?, FacultyID = ?, " +
                    "TextbookID = ? WHERE CourseID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setCourseParameters(stmt, course);
            stmt.setString(9, course.getCourseId());
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Error updating course", e);
        }
    }

    @Override
    public boolean delete(String courseId) {
        String sql = "DELETE FROM Course WHERE CourseID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, courseId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting course", e);
        }
    }

    @Override
    public List<Course> findByFaculty(String facultyId) {
        String sql = "SELECT * FROM Course WHERE FacultyID = ? ORDER BY StartDate DESC";
        List<Course> courses = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, facultyId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }
            return courses;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding courses by faculty", e);
        }
    }

    @Override
    public List<Course> findByType(String courseType) {
        String sql = "SELECT * FROM Course WHERE CourseType = ? ORDER BY StartDate DESC";
        List<Course> courses = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, courseType);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }
            return courses;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding courses by type", e);
        }
    }

    @Override
    public Optional<Course> findByToken(String token) {
        String sql = "SELECT * FROM Course WHERE Token = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToCourse(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding course by token", e);
        }
    }

    @Override
    public List<Course> findActiveCoursesForStudent(String studentId) {
        String sql = "SELECT c.* FROM Course c " +
                    "JOIN Enrollment e ON c.CourseID = e.CourseID " +
                    "WHERE e.StudentID = ? AND e.Status = 'Approved' " +
                    "AND c.EndDate >= CURRENT_DATE " +
                    "ORDER BY c.StartDate DESC";
        List<Course> courses = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }
            return courses;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding active courses for student", e);
        }
    }

    @Override
    public List<Course> findCoursesByTA(String taId) {
        String sql = "SELECT c.* FROM Course c " +
                    "JOIN CourseTA ct ON c.CourseID = ct.CourseID " +
                    "WHERE ct.TAID = ? " +
                    "ORDER BY c.StartDate DESC";
        List<Course> courses = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, taId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }
            return courses;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding courses by TA", e);
        }
    }

    private void setCourseParameters(PreparedStatement stmt, Course course) throws SQLException {
        stmt.setString(1, course.getTitle());
        stmt.setString(2, course.getCourseType());
        stmt.setString(3, course.getToken());
        stmt.setObject(4, course.getCapacity());
        stmt.setDate(5, Date.valueOf(course.getStartDate()));
        stmt.setDate(6, Date.valueOf(course.getEndDate()));
        stmt.setString(7, course.getFacultyId());
        stmt.setObject(8, course.getTextbookId());
    }

    private Course mapResultSetToCourse(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setCourseId(rs.getString("CourseID"));
        course.setTitle(rs.getString("Title"));
        course.setCourseType(rs.getString("CourseType"));
        course.setToken(rs.getString("Token"));
        course.setCapacity(rs.getObject("Capacity", Integer.class));
        course.setStartDate(rs.getDate("StartDate").toLocalDate());
        course.setEndDate(rs.getDate("EndDate").toLocalDate());
        course.setFacultyId(rs.getString("FacultyID"));
        course.setTextbookId(rs.getObject("TextbookID", Integer.class));
        return course;
    }
}