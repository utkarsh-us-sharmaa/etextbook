// dao/StudentActivityDAO.java
package com.etextbook.dao;

import com.etextbook.model.StudentActivity;
import java.util.List;
import java.util.Optional;

public interface StudentActivityDAO extends BaseDAO<StudentActivity, Integer> {
    List<StudentActivity> findByStudent(String studentId);
    List<StudentActivity> findByCourse(String courseId);
    List<StudentActivity> findByActivity(Integer activityId);
    Optional<StudentActivity> findByStudentAndActivity(String studentId, Integer activityId);
    double calculateAverageScore(String studentId, String courseId);
    int getTotalAttempts(String studentId, Integer activityId);
    List<StudentActivity> findByStudentAndCourse(String studentId, String courseId);
}
