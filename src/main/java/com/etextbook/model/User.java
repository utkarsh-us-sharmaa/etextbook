
package com.etextbook.model;

import java.time.LocalDate;

public class User {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;
    private LocalDate accountCreationDate;

    // Constructors
    public User() {}

    public User(String userId, String firstName, String lastName, String email, 
                String password, String role, LocalDate accountCreationDate) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.accountCreationDate = accountCreationDate;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDate getAccountCreationDate() { return accountCreationDate; }
    public void setAccountCreationDate(LocalDate accountCreationDate) { 
        this.accountCreationDate = accountCreationDate; 
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", accountCreationDate=" + accountCreationDate +
                '}';
    }
}
    

