// ui/AdminMenu.java
package com.etextbook.ui;

import com.etextbook.model.User;
import com.etextbook.service.CourseService;
import com.etextbook.service.ContentService;

public class AdminMenu extends BaseMenu {
    private final CourseService courseService;
    private final ContentService contentService;

    public AdminMenu(User user, String sessionId, CourseService courseService, 
                    ContentService contentService) {
        super(user, sessionId);
        this.courseService = courseService;
        this.contentService = contentService;
    }

    @Override
    protected void displayMenu() {
        System.out.println("\n=== Admin Menu ===");
        System.out.println("1. Create Faculty Account");
        System.out.println("2. Create TA Account");
        System.out.println("3. Manage Users");
        System.out.println("4. View System Statistics");
        System.out.println("5. Manage E-Textbooks");
        System.out.println("6. View Audit Logs");
        System.out.println("7. Change Password");
        System.out.println("8. Logout");
        System.out.print("Enter your choice: ");
    }

    @Override
    protected boolean handleMenuChoice(int choice) {
        switch (choice) {
            case 1:
                createFacultyAccount();
                return true;
            case 2:
                createTAAccount();
                return true;
            case 3:
                manageUsers();
                return true;
            case 4:
                viewSystemStats();
                return true;
            case 5:
                manageTextbooks();
                return true;
            case 6:
                viewAuditLogs();
                return true;
            case 7:
                changePassword();
                return true;
            case 8:
                System.out.println("Logging out...");
                return false;
            default:
                System.out.println("Invalid choice. Please try again.");
                return true;
        }
    }

    private void createFacultyAccount() {
        System.out.println("\n=== Create Faculty Account ===");
        // Implementation for creating faculty account
    }

    private void createTAAccount() {
        System.out.println("\n=== Create TA Account ===");
        // Implementation for creating TA account
    }

    private void manageUsers() {
        System.out.println("\n=== Manage Users ===");
        // Implementation for user management
    }

    private void viewSystemStats() {
        System.out.println("\n=== System Statistics ===");
        // Implementation for viewing system statistics
    }

    private void manageTextbooks() {
        System.out.println("\n=== Manage E-Textbooks ===");
        // Implementation for textbook management
    }

    private void viewAuditLogs() {
        System.out.println("\n=== Audit Logs ===");
        // Implementation for viewing audit logs
    }

    private void changePassword() {
        System.out.println("\n=== Change Password ===");
        // Implementation for password change
    }
}