package com.etextbook.dao.impl;

import com.etextbook.dao.ETextbookDAO;
import com.etextbook.model.ETextbook;
import com.etextbook.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ETextbookDAOImpl implements ETextbookDAO {

    @Override
    public Optional<ETextbook> findById(Integer textbookId) {
        String sql = "SELECT * FROM ETextbook WHERE TextbookID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, textbookId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToETextbook(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding textbook by ID", e);
        }
    }

    @Override
    public List<ETextbook> findAll() {
        String sql = "SELECT * FROM ETextbook";
        List<ETextbook> textbooks = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                textbooks.add(mapResultSetToETextbook(rs));
            }
            return textbooks;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all textbooks", e);
        }
    }

    @Override
    public void save(ETextbook textbook) {
        String sql = "INSERT INTO ETextbook (Title, TextContent, ImageURL) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, textbook.getTitle());
            stmt.setString(2, textbook.getTextContent());
            stmt.setString(3, textbook.getImageUrl());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating textbook failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    textbook.setTextbookId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating textbook failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving textbook", e);
        }
    }

    @Override
    public void update(ETextbook textbook) {
        String sql = "UPDATE ETextbook SET Title = ?, TextContent = ?, ImageURL = ? WHERE TextbookID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, textbook.getTitle());
            stmt.setString(2, textbook.getTextContent());
            stmt.setString(3, textbook.getImageUrl());
            stmt.setInt(4, textbook.getTextbookId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating textbook", e);
        }
    }

    @Override
    public boolean delete(Integer textbookId) {
        String sql = "DELETE FROM ETextbook WHERE TextbookID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, textbookId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting textbook", e);
        }
    }

    @Override
    public List<ETextbook> findByTitle(String title) {
        String sql = "SELECT * FROM ETextbook WHERE Title LIKE ?";
        List<ETextbook> textbooks = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + title + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                textbooks.add(mapResultSetToETextbook(rs));
            }
            return textbooks;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding textbooks by title", e);
        }
    }

    @Override
    public List<ETextbook> findByFaculty(String facultyId) {
        String sql = "SELECT DISTINCT e.* FROM ETextbook e " +
                    "JOIN Course c ON e.TextbookID = c.TextbookID " +
                    "WHERE c.FacultyID = ?";
        List<ETextbook> textbooks = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, facultyId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                textbooks.add(mapResultSetToETextbook(rs));
            }
            return textbooks;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding textbooks by faculty", e);
        }
    }

    private ETextbook mapResultSetToETextbook(ResultSet rs) throws SQLException {
        ETextbook textbook = new ETextbook();
        textbook.setTextbookId(rs.getInt("TextbookID"));
        textbook.setTitle(rs.getString("Title"));
        textbook.setTextContent(rs.getString("TextContent"));
        textbook.setImageUrl(rs.getString("ImageURL"));
        return textbook;
    }
}