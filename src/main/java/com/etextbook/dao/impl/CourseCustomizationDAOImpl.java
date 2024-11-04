package com.etextbook.dao.impl;

import com.etextbook.dao.CourseCustomizationDAO;
import com.etextbook.model.CourseCustomization;
import com.etextbook.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseCustomizationDAOImpl implements CourseCustomizationDAO {

    @Override
    public Optional<CourseCustomization> findById(Integer customizationId) {
        String sql = "SELECT * FROM CourseCustomization WHERE CustomizationID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customizationId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToCustomization(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding course customization by ID", e);
        }
    }

    @Override
    public List<CourseCustomization> findAll() {
        String sql = "SELECT * FROM CourseCustomization ORDER BY CourseID, DisplayOrder";
        List<CourseCustomization> customizations = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                customizations.add(mapResultSetToCustomization(rs));
            }
            return customizations;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all course customizations", e);
        }
    }

    @Override
    public void save(CourseCustomization customization) {
        String sql = "INSERT INTO CourseCustomization (CourseID, ContentBlockID, ActivityID, " +
                    "IsHidden, AddedByRole, IsOriginalContent, DisplayOrder, CreatedByUserID) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            setCustomizationParameters(stmt, customization);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating course customization failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    customization.setCustomizationId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating course customization failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving course customization", e);
        }
    }

    @Override
    public void update(CourseCustomization customization) {
        String sql = "UPDATE CourseCustomization SET CourseID = ?, ContentBlockID = ?, " +
                    "ActivityID = ?, IsHidden = ?, AddedByRole = ?, IsOriginalContent = ?, " +
                    "DisplayOrder = ?, CreatedByUserID = ? WHERE CustomizationID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setCustomizationParameters(stmt, customization);
            stmt.setInt(9, customization.getCustomizationId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating course customization", e);
        }
    }

    @Override
    public boolean delete(Integer customizationId) {
        String sql = "DELETE FROM CourseCustomization WHERE CustomizationID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customizationId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting course customization", e);
        }
    }

    @Override
    public List<CourseCustomization> findByCourse(String courseId) {
        String sql = "SELECT * FROM CourseCustomization WHERE CourseID = ? ORDER BY DisplayOrder";
        List<CourseCustomization> customizations = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, courseId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                customizations.add(mapResultSetToCustomization(rs));
            }
            return customizations;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding customizations by course", e);
        }
    }

    @Override
    public List<CourseCustomization> findByCreator(String userId) {
        String sql = "SELECT * FROM CourseCustomization WHERE CreatedByUserID = ? " +
                    "ORDER BY CourseID, DisplayOrder";
        List<CourseCustomization> customizations = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                customizations.add(mapResultSetToCustomization(rs));
            }
            return customizations;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding customizations by creator", e);
        }
    }

    @Override
    public List<CourseCustomization> findByRole(String courseId, String role) {
        String sql = "SELECT * FROM CourseCustomization WHERE CourseID = ? AND AddedByRole = ? " +
                    "ORDER BY DisplayOrder";
        List<CourseCustomization> customizations = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, courseId);
            stmt.setString(2, role);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                customizations.add(mapResultSetToCustomization(rs));
            }
            return customizations;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding customizations by role", e);
        }
    }

    @Override
    public void updateDisplayOrder(Integer customizationId, Integer newOrder) {
        String sql = "UPDATE CourseCustomization SET DisplayOrder = ? WHERE CustomizationID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, newOrder);
            stmt.setInt(2, customizationId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating display order", e);
        }
    }

    @Override
    public void reorderContent(String courseId, List<CourseCustomization> newOrder) {
        String sql = "UPDATE CourseCustomization SET DisplayOrder = ? WHERE CustomizationID = ? AND CourseID = ?";
        
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < newOrder.size(); i++) {
                    CourseCustomization customization = newOrder.get(i);
                    stmt.setInt(1, i + 1);  // New display order (1-based)
                    stmt.setInt(2, customization.getCustomizationId());
                    stmt.setString(3, courseId);
                    stmt.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error reordering content", e);
        }
    }

    @Override
    public void toggleVisibility(Integer customizationId) {
        String sql = "UPDATE CourseCustomization SET IsHidden = NOT IsHidden " +
                    "WHERE CustomizationID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customizationId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error toggling visibility", e);
        }
    }

    private void setCustomizationParameters(PreparedStatement stmt, CourseCustomization customization) 
            throws SQLException {
        stmt.setString(1, customization.getCourseId());
        stmt.setObject(2, customization.getContentBlockId());
        stmt.setObject(3, customization.getActivityId());
        stmt.setBoolean(4, customization.getIsHidden());
        stmt.setString(5, customization.getAddedByRole());
        stmt.setBoolean(6, customization.getIsOriginalContent());
        stmt.setInt(7, customization.getDisplayOrder());
        stmt.setString(8, customization.getCreatedByUserId());
    }

    private CourseCustomization mapResultSetToCustomization(ResultSet rs) throws SQLException {
        CourseCustomization customization = new CourseCustomization();
        customization.setCustomizationId(rs.getInt("CustomizationID"));
        customization.setCourseId(rs.getString("CourseID"));
        customization.setContentBlockId(rs.getObject("ContentBlockID", Integer.class));
        customization.setActivityId(rs.getObject("ActivityID", Integer.class));
        customization.setIsHidden(rs.getBoolean("IsHidden"));
        customization.setAddedByRole(rs.getString("AddedByRole"));
        customization.setIsOriginalContent(rs.getBoolean("IsOriginalContent"));
        customization.setDisplayOrder(rs.getInt("DisplayOrder"));
        customization.setCreatedByUserId(rs.getString("CreatedByUserID"));
        return customization;
    }
}