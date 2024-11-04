package com.etextbook.dao.impl;

import com.etextbook.dao.ContentBlockDAO;
import com.etextbook.model.ContentBlock;
import com.etextbook.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ContentBlockDAOImpl implements ContentBlockDAO {

    @Override
    public Optional<ContentBlock> findById(Integer contentBlockId) {
        String sql = "SELECT * FROM ContentBlock WHERE ContentBlockID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, contentBlockId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToContentBlock(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding content block by ID", e);
        }
    }

    @Override
    public List<ContentBlock> findAll() {
        String sql = "SELECT * FROM ContentBlock ORDER BY SectionID, SequenceNumber";
        List<ContentBlock> contentBlocks = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                contentBlocks.add(mapResultSetToContentBlock(rs));
            }
            return contentBlocks;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all content blocks", e);
        }
    }

    @Override
    public void save(ContentBlock contentBlock) {
        String sql = "INSERT INTO ContentBlock (ContentType, Content, SequenceNumber, SectionID) " +
                    "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            validateContentBlock(contentBlock);
            
            stmt.setString(1, contentBlock.getContentType());
            stmt.setString(2, contentBlock.getContent());
            stmt.setInt(3, contentBlock.getSequenceNumber());
            stmt.setString(4, contentBlock.getSectionId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating content block failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    contentBlock.setContentBlockId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating content block failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving content block", e);
        }
    }

    @Override
    public void update(ContentBlock contentBlock) {
        String sql = "UPDATE ContentBlock SET ContentType = ?, Content = ?, " +
                    "SequenceNumber = ?, SectionID = ? WHERE ContentBlockID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            validateContentBlock(contentBlock);
            
            stmt.setString(1, contentBlock.getContentType());
            stmt.setString(2, contentBlock.getContent());
            stmt.setInt(3, contentBlock.getSequenceNumber());
            stmt.setString(4, contentBlock.getSectionId());
            stmt.setInt(5, contentBlock.getContentBlockId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating content block", e);
        }
    }

    @Override
    public boolean delete(Integer contentBlockId) {
        String sql = "DELETE FROM ContentBlock WHERE ContentBlockID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, contentBlockId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting content block", e);
        }
    }

    @Override
    public List<ContentBlock> findBySection(String sectionId) {
        String sql = "SELECT * FROM ContentBlock WHERE SectionID = ? ORDER BY SequenceNumber";
        List<ContentBlock> contentBlocks = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, sectionId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                contentBlocks.add(mapResultSetToContentBlock(rs));
            }
            return contentBlocks;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding content blocks by section", e);
        }
    }

    @Override
    public List<ContentBlock> findByType(String contentType) {
        String sql = "SELECT * FROM ContentBlock WHERE ContentType = ?";
        List<ContentBlock> contentBlocks = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, contentType);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                contentBlocks.add(mapResultSetToContentBlock(rs));
            }
            return contentBlocks;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding content blocks by type", e);
        }
    }

    @Override
    public List<ContentBlock> findBySequenceOrder(String sectionId) {
        String sql = "SELECT cb.* FROM ContentBlock cb " +
                    "LEFT JOIN CourseCustomization cc ON cb.ContentBlockID = cc.ContentBlockID " +
                    "WHERE cb.SectionID = ? " +
                    "ORDER BY COALESCE(cc.DisplayOrder, cb.SequenceNumber)";
        List<ContentBlock> contentBlocks = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, sectionId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                contentBlocks.add(mapResultSetToContentBlock(rs));
            }
            return contentBlocks;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding content blocks in sequence order", e);
        }
    }

    @Override
    public Optional<ContentBlock> findBySequenceNumber(String sectionId, Integer sequenceNumber) {
        String sql = "SELECT * FROM ContentBlock WHERE SectionID = ? AND SequenceNumber = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, sectionId);
            stmt.setInt(2, sequenceNumber);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToContentBlock(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding content block by sequence number", e);
        }
    }

    private ContentBlock mapResultSetToContentBlock(ResultSet rs) throws SQLException {
        ContentBlock contentBlock = new ContentBlock();
        contentBlock.setContentBlockId(rs.getInt("ContentBlockID"));
        contentBlock.setContentType(rs.getString("ContentType"));
        contentBlock.setContent(rs.getString("Content"));
        contentBlock.setSequenceNumber(rs.getInt("SequenceNumber"));
        contentBlock.setSectionId(rs.getString("SectionID"));
        return contentBlock;
    }

    private void validateContentBlock(ContentBlock contentBlock) {
        if (contentBlock.getContent() == null || contentBlock.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be null or empty");
        }
        if (!contentBlock.getContentType().equals("Text") && !contentBlock.getContentType().equals("Image")) {
            throw new IllegalArgumentException("Content type must be either 'Text' or 'Image'");
        }
    }
}