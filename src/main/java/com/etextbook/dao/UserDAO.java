// dao/UserDAO.java
package com.etextbook.dao;

import com.etextbook.model.User;
import java.util.List;
import java.util.Optional;

public interface UserDAO extends BaseDAO<User, String> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(String role);
    boolean authenticate(String userId, String password, String role);
    void updatePassword(String userId, String newPassword);
}

