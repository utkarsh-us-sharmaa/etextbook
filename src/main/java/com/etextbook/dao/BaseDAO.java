// dao/BaseDAO.java
package com.etextbook.dao;

import java.util.List;
import java.util.Optional;


public interface BaseDAO<T, ID> {
    Optional<T> findById(ID id);
    List<T> findAll();
    void save(T entity);
    void update(T entity);
    boolean delete(ID id);
}