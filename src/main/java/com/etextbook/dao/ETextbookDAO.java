package com.etextbook.dao;

import com.etextbook.model.ETextbook;
import java.util.List;
// import java.util.Optional;

public interface ETextbookDAO extends BaseDAO<ETextbook, Integer> {
    List<ETextbook> findByTitle(String title);
    List<ETextbook> findByFaculty(String facultyId);
}