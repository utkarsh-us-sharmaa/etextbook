// dao/impl/CourseContentVersioningDAOImpl.java
package com.etextbook.dao.impl;

import com.etextbook.dao.CourseContentVersioningDAO;
import com.etextbook.model.CourseContentVersioning;
import com.etextbook.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseContentVersioningDAOImpl implements CourseContentVersioningDAO {

    @Override
    public Optional<CourseContentVersioning> findById(Integer versionId) {
        String sql = "SELECT * FROM CourseContentVersioning WHERE VersionID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, versionId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToVersioning(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding content versioning by ID", e);
        }
    }

    @Override
    public List<CourseContentVersioning> findAll() {
        String sql = "SELECT * FROM CourseContentVersioning ORDER BY CourseID, DisplayOrder";
        List<CourseContentVersioning> versionings = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                versionings.add(mapResultSetToVersioning(rs));
            }
            return versionings;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all content versionings", e);
        }
    }

    @Override
    public void save(CourseContentVersioning versioning) {
        String sql = "INSERT INTO CourseContentVersioning (CourseID, ChapterID, SectionID, DisplayOrder) " +
                    "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (versioning.getDisplayOrder() == null) {
                versioning.setDisplayOrder(1); // Default value if none provided
            }
            setVersioningParameters(stmt, versioning);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating content versioning failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    versioning.setVersionId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating content versioning failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving content versioning", e);
        }
    }

    @Override
    public void update(CourseContentVersioning versioning) {
        String sql = "UPDATE CourseContentVersioning SET CourseID = ?, ChapterID = ?, " +
                    "SectionID = ?, DisplayOrder = ? WHERE VersionID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setVersioningParameters(stmt, versioning);
            stmt.setInt(5, versioning.getVersionId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating content versioning", e);
        }
    }

    @Override
    public boolean delete(Integer versionId) {
        String sql = "DELETE FROM CourseContentVersioning WHERE VersionID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, versionId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting content versioning", e);
        }
    }

    @Override
    public List<CourseContentVersioning> findByCourse(String courseId) {
        String sql = "SELECT * FROM CourseContentVersioning WHERE CourseID = ? ORDER BY DisplayOrder";
        List<CourseContentVersioning> versionings = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, courseId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                versionings.add(mapResultSetToVersioning(rs));
            }
            return versionings;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding content versionings by course", e);
        }
    }

    @Override
    public List<CourseContentVersioning> findByChapter(String chapterId) {
        String sql = "SELECT * FROM CourseContentVersioning WHERE ChapterID = ? ORDER BY DisplayOrder";
        List<CourseContentVersioning> versionings = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, chapterId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                versionings.add(mapResultSetToVersioning(rs));
            }
            return versionings;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding content versionings by chapter", e);
        }
    }

    @Override
    public List<CourseContentVersioning> findBySection(String sectionId) {
        String sql = "SELECT * FROM CourseContentVersioning WHERE SectionID = ? ORDER BY DisplayOrder";
        List<CourseContentVersioning> versionings = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, sectionId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                versionings.add(mapResultSetToVersioning(rs));
            }
            return versionings;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding content versionings by section", e);
        }
    }

    @Override
    public void updateDisplayOrder(Integer versionId, Integer newOrder) {
        String sql = "UPDATE CourseContentVersioning SET DisplayOrder = ? WHERE VersionID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, newOrder);
            stmt.setInt(2, versionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating display order", e);
        }
    }

    @Override
    public void reorderContent(String courseId, List<CourseContentVersioning> newOrder) {
        String sql = "UPDATE CourseContentVersioning SET DisplayOrder = ? WHERE VersionID = ?";
        
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < newOrder.size(); i++) {
                    stmt.setInt(1, i + 1);
                    stmt.setInt(2, newOrder.get(i).getVersionId());
                    stmt.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error reordering content", e);
        }
    }

    private void setVersioningParameters(PreparedStatement stmt, CourseContentVersioning versioning) 
            throws SQLException {
        stmt.setString(1, versioning.getCourseId());
        stmt.setString(2, versioning.getChapterId());
        stmt.setObject(3, versioning.getSectionId());
        stmt.setInt(4, versioning.getDisplayOrder());
    }

    private CourseContentVersioning mapResultSetToVersioning(ResultSet rs) throws SQLException {
        CourseContentVersioning versioning = new CourseContentVersioning();
        versioning.setVersionId(rs.getInt("VersionID"));
        versioning.setCourseId(rs.getString("CourseID"));
        versioning.setChapterId(rs.getString("ChapterID"));
        versioning.setSectionId(rs.getObject("SectionID", String.class));
        versioning.setDisplayOrder(rs.getInt("DisplayOrder"));
        return versioning;
    }
}