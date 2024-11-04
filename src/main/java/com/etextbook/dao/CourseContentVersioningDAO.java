// dao/CourseContentVersioningDAO.java
package com.etextbook.dao;

import com.etextbook.model.CourseContentVersioning;
import java.util.List;
// import java.util.Optional;

public interface CourseContentVersioningDAO extends BaseDAO<CourseContentVersioning, Integer> {
    List<CourseContentVersioning> findByCourse(String courseId);
    List<CourseContentVersioning> findByChapter(String chapterId);
    List<CourseContentVersioning> findBySection(String sectionId);
    void updateDisplayOrder(Integer versionId, Integer newOrder);
    void reorderContent(String courseId, List<CourseContentVersioning> newOrder);
}