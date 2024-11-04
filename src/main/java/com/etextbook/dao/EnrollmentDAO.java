package com.etextbook.dao;

import com.etextbook.model.Enrollment;
import java.util.List;
import java.util.Optional;

public interface EnrollmentDAO extends BaseDAO<Enrollment, Integer> {
    List<Enrollment> findByStudent(String studentId);
    List<Enrollment> findByCourse(String courseId);
    List<Enrollment> findByStatus(String status);
    Optional<Enrollment> findByStudentAndCourse(String studentId, String courseId);
    boolean updateStatus(Integer enrollmentId, String status);
}
