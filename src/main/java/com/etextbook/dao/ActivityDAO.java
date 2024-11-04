package com.etextbook.dao;

import com.etextbook.model.Activity;
import java.util.List;
// import java.util.Optional;

public interface ActivityDAO extends BaseDAO<Activity, Integer> {
    List<Activity> findByContentBlock(Integer contentBlockId);
    List<Activity> findByCourse(String courseId);
    List<Activity> findByStudent(String studentId);
}
