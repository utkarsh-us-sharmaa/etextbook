// dao/CourseTADAO.java
package com.etextbook.dao;

import com.etextbook.model.CourseTA;
import java.util.List;
// import java.util.Optional;

public interface CourseTADAO extends BaseDAO<CourseTA, Integer> {
    List<CourseTA> findByCourse(String courseId);
    List<CourseTA> findByTA(String taId);
    boolean removeTA(String courseId, String taId);
    boolean isAssignedTA(String courseId, String taId);
}