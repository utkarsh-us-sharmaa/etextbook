// service/AuthenticationService.java
package com.etextbook.service;
import java.time.LocalDate;
import com.etextbook.model.User;
import com.etextbook.dao.UserDAO;
import com.etextbook.util.PasswordEncoder;
import com.etextbook.exception.AuthenticationException;

public class AuthenticationService {
    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    public AuthenticationService(UserDAO userDAO, PasswordEncoder passwordEncoder, 
                               NotificationService notificationService) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
    }

    public User login(String userId, String password, String role) {
        User user = userDAO.findById(userId)
            .orElseThrow(() -> new AuthenticationException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthenticationException("Invalid credentials");
        }

        if (!user.getRole().equals(role)) {
            throw new AuthenticationException("Unauthorized role access");
        }

        notificationService.createNotification(userId, "New login detected");
        return user;
    }

    public void changePassword(String userId, String oldPassword, String newPassword) {
        User user = userDAO.findById(userId)
            .orElseThrow(() -> new AuthenticationException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new AuthenticationException("Invalid old password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userDAO.update(user);
        notificationService.createNotification(userId, "Password changed successfully");
    }

    public void resetPassword(String userId, String email) {
        User user = userDAO.findByEmail(email)
            .orElseThrow(() -> new AuthenticationException("User not found"));

        if (!user.getUserId().equals(userId)) {
            throw new AuthenticationException("User ID and email do not match");
        }

        String tempPassword = generateTemporaryPassword();
        user.setPassword(passwordEncoder.encode(tempPassword));
        userDAO.update(user);
        
        // In real application, send this via email
        notificationService.createNotification(userId, 
            "Your temporary password is: " + tempPassword);
    }
    public void registerUser(String userId, String firstName, String lastName, 
                        String email, String password, String role) {
    // Check if user already exists
    if (userDAO.findById(userId).isPresent()) {
        throw new AuthenticationException("User ID already exists");
    }
    if (userDAO.findByEmail(email).isPresent()) {
        throw new AuthenticationException("Email already registered");
    }
    
    // Create new user
    User newUser = new User();
    newUser.setUserId(userId);
    newUser.setFirstName(firstName);
    newUser.setLastName(lastName);
    newUser.setEmail(email);
    newUser.setPassword(passwordEncoder.encode(password));
    newUser.setRole(role);
    newUser.setAccountCreationDate(LocalDate.now());
    
    // Save user to database
    userDAO.save(newUser);
    
    // Send notification
    notificationService.createNotification(userId, "Welcome! Account created successfully");
}

    private String generateTemporaryPassword() {
        return "temp" + System.currentTimeMillis() % 10000;
    }
}