package com.etextbook.dao.impl;

import com.etextbook.dao.SectionDAO;
import com.etextbook.model.Section;
import com.etextbook.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SectionDAOImpl implements SectionDAO {

    @Override
    public Optional<Section> findById(String sectionId) {
        String sql = "SELECT * FROM Section WHERE SectionID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, sectionId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToSection(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding section by ID", e);
        }
    }

    @Override
    public List<Section> findAll() {
        String sql = "SELECT * FROM Section";
        List<Section> sections = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                sections.add(mapResultSetToSection(rs));
            }
            return sections;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all sections", e);
        }
    }

    @Override
    public  void save(Section section) {
        String sql = "INSERT INTO Section (SectionID, SectionNumber, Title, ChapterID, TextbookID) VALUES (?, ?, ?, ?, ?)";
        System.out.println(section.getSectionId());
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, section.getSectionId());    
            stmt.setString(2, section.getSectionNumber());
            stmt.setString(3, section.getTitle());
            stmt.setString(4, section.getChapterId());
            stmt.setInt(5, section.getTextbookId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating section failed, no rows affected.");
            }

            // try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            //     if (generatedKeys.next()) {
            //         section.setSectionId(generatedKeys.getInt(1));
            //     } else {
            //         throw new SQLException("Creating section failed, no ID obtained.");
            //     }
            // }
            
        } catch (SQLException e) {
            throw new RuntimeException("Error saving section", e);
        }
    }

    @Override
    public void update(Section section) {
        String sql = "UPDATE Section SET SectionNumber = ?, Title = ?, ChapterID = ? WHERE SectionID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, section.getSectionNumber());
            stmt.setString(2, section.getTitle());
            stmt.setString(3, section.getChapterId());
            stmt.setString(4, section.getSectionId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating section", e);
        }
    }

    @Override
    public boolean delete(String sectionId) {
        String sql = "DELETE FROM Section WHERE SectionID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, sectionId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting section", e);
        }
    }

    @Override
    public List<Section> findByChapter(String chapterId) {
        String sql = "SELECT * FROM Section WHERE ChapterID = ? ORDER BY SectionNumber";
        List<Section> sections = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, chapterId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                sections.add(mapResultSetToSection(rs));
            }
            return sections;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding sections by chapter", e);
        }
    }

    @Override
    public Optional<Section> findBySectionNumber(String sectionNumber, String chapterId) {
        String sql = "SELECT * FROM Section WHERE SectionNumber = ? AND ChapterID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, sectionNumber);
            stmt.setString(2, chapterId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToSection(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding section by number", e);
        }
    }

    @Override
    public List<Section> findSectionsInOrder(String chapterId) {
        String sql = "SELECT s.* FROM Section s " +
                    "LEFT JOIN CourseContentVersioning ccv ON s.SectionID = ccv.SectionID " +
                    "WHERE s.ChapterID = ? " +
                    "ORDER BY COALESCE(ccv.DisplayOrder, s.SectionNumber)";
        List<Section> sections = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, chapterId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                sections.add(mapResultSetToSection(rs));
            }
            return sections;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding sections in order", e);
        }
    }

    private Section mapResultSetToSection(ResultSet rs) throws SQLException {
        Section section = new Section();
        section.setSectionId(rs.getString("SectionID"));
        section.setSectionNumber(rs.getString("SectionNumber"));
        section.setTitle(rs.getString("Title"));
        section.setChapterId(rs.getString("ChapterID"));
        return section;
    }
}