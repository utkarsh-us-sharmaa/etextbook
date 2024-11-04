// dao/CourseCustomizationDAO.java
package com.etextbook.dao;

import com.etextbook.model.CourseCustomization;
import java.util.List;
// import java.util.Optional;

public interface CourseCustomizationDAO extends BaseDAO<CourseCustomization, Integer> {
    List<CourseCustomization> findByCourse(String courseId);
    List<CourseCustomization> findByCreator(String userId);
    List<CourseCustomization> findByRole(String courseId, String role);
    void updateDisplayOrder(Integer customizationId, Integer newOrder);
    void toggleVisibility(Integer customizationId);
    void reorderContent(String courseId, List<CourseCustomization> newOrder);
}