package com.etextbook.dao.impl;

import com.etextbook.dao.ChapterDAO;
import com.etextbook.model.Chapter;
import com.etextbook.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChapterDAOImpl implements ChapterDAO {

    @Override
    public Optional<Chapter> findById(String chapterId) {
        String sql = "SELECT * FROM Chapter WHERE ChapterID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, chapterId);

            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToChapter(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding chapter by ID", e);
        }
    }

    @Override
    public List<Chapter> findAll() {
        String sql = "SELECT * FROM Chapter";
        List<Chapter> chapters = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                chapters.add(mapResultSetToChapter(rs));
            }
            return chapters;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all chapters", e);
        }
    }

    @Override
    public void save(Chapter chapter) {
        String sql = "INSERT INTO Chapter (ChapterId, Title, TextbookID) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, chapter.getChapterId());
            stmt.setString(2, chapter.getTitle());
            stmt.setInt(3, chapter.getTextbookId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating chapter failed, no rows affected.");
            }

            // try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            //     if (generatedKeys.next()) {
            //         chapter.setChapterId(generatedKeys.getString(1));
            //     } else {
            //         throw new SQLException("Creating chapter failed, no ID obtained.");
            //     }
            // }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving chapter", e);
        }
    }

    @Override
    public void update(Chapter chapter) {
        String sql = "UPDATE Chapter SET ChapterNumber = ?, Title = ?, TextbookID = ? WHERE ChapterID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, chapter.getChapterNumber());
            stmt.setString(2, chapter.getTitle());
            stmt.setInt(3, chapter.getTextbookId());
            stmt.setString(4, chapter.getChapterId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating chapter", e);
        }
    }

    @Override
    public boolean delete(String chapterId) {
        String sql = "DELETE FROM Chapter WHERE ChapterID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, chapterId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting chapter", e);
        }
    }

    @Override
    public List<Chapter> findByTextbook(Integer textbookId) {
        String sql = "SELECT * FROM Chapter WHERE TextbookID = ? ORDER BY ChapterNumber";
        List<Chapter> chapters = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, textbookId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                chapters.add(mapResultSetToChapter(rs));
            }
            return chapters;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding chapters by textbook", e);
        }
    }

    @Override
    public Optional<Chapter> findByChapterNumber(String chapterNumber, Integer textbookId) {
        String sql = "SELECT * FROM Chapter WHERE ChapterNumber = ? AND TextbookID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, chapterNumber);
            stmt.setInt(2, textbookId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToChapter(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding chapter by number", e);
        }
    }

    @Override
    public List<Chapter> findChaptersInOrder(Integer textbookId) {
        String sql = "SELECT c.* FROM Chapter c " +
                    "LEFT JOIN CourseContentVersioning ccv ON c.ChapterID = ccv.ChapterID " +
                    "WHERE c.TextbookID = ? " +
                    "ORDER BY COALESCE(ccv.DisplayOrder, c.ChapterNumber)";
        List<Chapter> chapters = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, textbookId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                chapters.add(mapResultSetToChapter(rs));
            }
            return chapters;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding chapters in order", e);
        }
    }

    private Chapter mapResultSetToChapter(ResultSet rs) throws SQLException {
        Chapter chapter = new Chapter();
        chapter.setChapterId(rs.getString("ChapterID"));
        chapter.setChapterNumber(rs.getString("ChapterNumber"));
        chapter.setTitle(rs.getString("Title"));
        chapter.setTextbookId(rs.getInt("TextbookID"));
        return chapter;
    }
}