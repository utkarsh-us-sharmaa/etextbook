package com.etextbook.dao;

import com.etextbook.model.Course;
import java.util.List;
import java.util.Optional;

public interface CourseDAO extends BaseDAO<Course, String> {
    List<Course> findByFaculty(String facultyId);
    List<Course> findByType(String courseType);
    Optional<Course> findByToken(String token);
    List<Course> findActiveCoursesForStudent(String studentId);
    List<Course> findCoursesByTA(String taId);
}