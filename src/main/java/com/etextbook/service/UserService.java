package com.etextbook.service;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.etextbook.dao.UserDAO;
import com.etextbook.model.User;
import com.etextbook.service.exception.ServiceException;
import java.time.LocalDate;
import java.util.List;

public class UserService {
    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    public UserService(UserDAO userDAO, PasswordEncoder passwordEncoder, 
                      NotificationService notificationService) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
    }

    public User authenticate(String userId, String password, String role) {
        return userDAO.findById(userId)
            .filter(user -> passwordEncoder.matches(password, user.getPassword()))
            .filter(user -> user.getRole().equals(role))
            .orElseThrow(() -> new ServiceException("Invalid credentials"));
    }

    public User registerUser(User user) {
        validateUserData(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAccountCreationDate(LocalDate.now());
        
        try {
            userDAO.save(user);
            notificationService.sendWelcomeNotification(user);
            return user;
        } catch (Exception e) {
            throw new ServiceException("Error registering user", e);
        }
    }

    public void updatePassword(String userId, String oldPassword, String newPassword) {
        User user = userDAO.findById(userId)
            .orElseThrow(() -> new ServiceException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new ServiceException("Invalid old password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userDAO.update(user);
        notificationService.sendPasswordChangeNotification(user);
    }

    public List<User> findUsersByRole(String role) {
        return userDAO.findByRole(role);
    }

    private void validateUserData(User user) {
        if (!isValidEmail(user.getEmail())) {
            throw new ServiceException("Invalid email format");
        }
        if (!isValidPassword(user.getPassword())) {
            throw new ServiceException("Password does not meet requirements");
        }
        if (userDAO.findByEmail(user.getEmail()).isPresent()) {
            throw new ServiceException("Email already registered");
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 8 &&
               password.matches(".*[A-Z].*") && // At least one uppercase letter
               password.matches(".*[a-z].*") && // At least one lowercase letter
               password.matches(".*\\d.*");     // At least one digit
    }
}
