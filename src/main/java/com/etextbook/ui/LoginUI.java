// // ui/LoginUI.java
// package com.etextbook.ui;

// import com.etextbook.service.AuthenticationService;
// import com.etextbook.util.SessionManager;
// import com.etextbook.model.User;
// import java.util.Scanner;

// public class LoginUI {
//     private final AuthenticationService authService;
//     private final Scanner scanner;

//     public LoginUI(AuthenticationService authService) {
//         this.authService = authService;
//         this.scanner = new Scanner(System.in);
//     }

//     public void start() {
//         while (true) {
//             System.out.println("\nE-Textbook Platform Login");
//             System.out.println("1. Login");
//             System.out.println("2. Exit");
//             System.out.print("Choose an option: ");

//             int choice = scanner.nextInt();
//             scanner.nextLine(); // Clear buffer

//             if (choice == 1) {
//                 handleLogin();
//             } else if (choice == 2) {
//                 System.out.println("Goodbye!");
//                 break;
//             }
//         }
//     }

//     private void handleLogin() {
//         try {
//             System.out.print("Enter User ID: ");
//             String userId = scanner.nextLine();

//             System.out.print("Enter Password: ");
//             String password = scanner.nextLine();

//             System.out.println("Select Role:");
//             System.out.println("1. Admin");
//             System.out.println("2. Faculty");
//             System.out.println("3. TA");
//             System.out.println("4. Student");
//             System.out.print("Choose role: ");
            
//             int roleChoice = scanner.nextInt();
//             String role = getRoleFromChoice(roleChoice);

//             // Attempt login
//             User user = authService.login(userId, password, role);
//             String sessionId = SessionManager.createSession(user);

//             System.out.println("Login successful! Welcome " + user.getFirstName());
            
//             // Route to appropriate menu based on role
//             handleUserMenu(user, sessionId);

//         } catch (Exception e) {
//             System.out.println("Login failed: " + e.getMessage());
//         }
//     }

//     private String getRoleFromChoice(int choice) {
//         switch (choice) {
//             case 1: return "Admin";
//             case 2: return "Faculty";
//             case 3: return "TA";
//             case 4: return "Student";
//             default: throw new IllegalArgumentException("Invalid role choice");
//         }
//     }

//     private void handleUserMenu(User user, String sessionId) {
//         try {
//             switch (user.getRole()) {
//                 case "Admin":
//                     new AdminMenu(user, sessionId).show();
//                     break;
//                 case "Faculty":
//                     new FacultyMenu(user, sessionId).show();
//                     break;
//                 case "TA":
//                     new TAMenu(user, sessionId).show();
//                     break;
//                 case "Student":
//                     new StudentMenu(user, sessionId).show();
//                     break;
//             }
//         } finally {
//             // Always logout when menu is closed
//             SessionManager.invalidateSession(sessionId);
//         }
//     }
// }

// ui/LoginUI.java
package com.etextbook.ui;

import com.etextbook.model.User;
import com.etextbook.service.*;
import com.etextbook.util.SessionManager;
import com.etextbook.exception.AuthenticationException;
import java.util.Scanner;

public class LoginUI {
    private final AuthenticationService authService;
    private final CourseService courseService;
    private final ContentService contentService;
    private final ActivityService activityService;
    private final ParticipationService participationService;
    private final CustomizationService customizationService;
    private final Scanner scanner;

    public LoginUI(
            AuthenticationService authService,
            CourseService courseService,
            ContentService contentService,
            ActivityService activityService,
            ParticipationService participationService,
            CustomizationService customizationService) {
        this.authService = authService;
        this.courseService = courseService;
        this.contentService = contentService;
        this.activityService = activityService;
        this.participationService = participationService;
        this.customizationService = customizationService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        while (running) {
            displayMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleLogin("Admin");
                    break;
                case "2":
                    handleLogin("Faculty");
                    break;
                case "3":
                    handleLogin("TA");
                    break;
                case "4":
                    handleLogin("Student");
                    break;
                case "5":
                    System.out.println("\nThank you for using E-Textbook Platform!");
                    running = false;
                    break;
                case "6":
                    handleRegistration();
                    break;    
                default:
                    System.out.println("\nInvalid choice. Please enter a number between 1 and 5.");
            }
        }
    }
        

    public void displayMainMenu() {
        System.out.println("\n=== E-Textbook Platform - Home ===");
        System.out.println("1. Admin Login");
        System.out.println("2. Faculty Login");
        System.out.println("3. TA Login");
        System.out.println("4. Student Login");
        System.out.println("6. Register New User");  // Add this line
        System.out.print("\nEnter Choice (1-6): ");
    }
    private void handleRegistration() {
        System.out.println("\n=== New User Registration ===");
        
        // Get user input with validation
        String userId = getValidInput("User ID (3-10 characters)", 
            input -> InputValidator.isValidUserId(input));
        
        String firstName = getValidInput("First Name", 
            input -> !input.trim().isEmpty());
        
        String lastName = getValidInput("Last Name", 
            input -> !input.trim().isEmpty());
        
        String email = getValidInput("Email", 
            input -> InputValidator.isValidEmail(input));
        
        String password = getValidInput("Password (minimum 6 characters)", 
            input -> InputValidator.isValidPassword(input));
        
        System.out.println("\nSelect Role:");
        System.out.println("1. Faculty");
        System.out.println("2. TA");
        System.out.println("3. Student");
        System.out.print("Enter Choice (1-3): ");
        
        String role;
        while (true) {
            try {
                int roleChoice = Integer.parseInt(scanner.nextLine().trim());
                role = switch (roleChoice) {
                    case 1 -> "Faculty";
                    case 2 -> "TA";
                    case 3 -> "Student";
                    default -> throw new IllegalArgumentException("Invalid role choice");
                };
                break;
            } catch (Exception e) {
                System.out.println("Invalid choice. Please enter a number between 1 and 3.");
            }
        }
        
        try {
            authService.registerUser(userId, firstName, lastName, email, password, role);
            System.out.println("\nRegistration successful! Please login with your credentials.");
        } catch (AuthenticationException e) {
            System.out.println("\nRegistration failed: " + e.getMessage());
        }
    }
    
    // Helper method for input validation
    private String getValidInput(String prompt, java.util.function.Predicate<String> validator) {
        String input;
        do {
            System.out.print(prompt + ": ");
            input = scanner.nextLine().trim();
            if (!validator.test(input)) {
                System.out.println("Invalid input. Please try again.");
            }
        } while (!validator.test(input));
        return input;
    }

    private void handleLogin(String role) {
        System.out.println("\n=== " + role + " Login ===");
        
        String userId = "";
        while (userId.isEmpty()) {
            System.out.print("Enter User ID: ");
            userId = scanner.nextLine().trim();
            if (userId.isEmpty()) {
                System.out.println("User ID cannot be empty. Please try again.");
            }
        }

        String password = "";
        while (password.isEmpty()) {
            System.out.print("Enter Password: ");
            password = scanner.nextLine().trim();
            if (password.isEmpty()) {
                System.out.println("Password cannot be empty. Please try again.");
            }
        }

        try {
            User user = authService.login(userId, password, role);
            String sessionId = SessionManager.createSession(user);
            System.out.println("\nLogin successful! Welcome, " + user.getFirstName() + " " + user.getLastName());
            
            // Route to appropriate menu based on role
            switch (role) {
                case "Admin":
                    new AdminMenu(user, sessionId, courseService, contentService).show();
                    break;
                case "Faculty":
                    new FacultyMenu(user, sessionId, courseService, contentService, 
                                  activityService, customizationService).show();
                    break;
                case "TA":
                    new TAMenu(sessionId, courseService, contentService, activityService, authService, user, this).show();
                    break;
                case "Student":
                    new StudentMenu(user, sessionId, courseService, activityService, 
                                  participationService).show();
                    break;
            }

            // Cleanup session after menu closes
            SessionManager.invalidateSession(sessionId);

        } catch (AuthenticationException e) {
            System.out.println("\nLogin failed: " + e.getMessage());
        
        } catch (NumberFormatException e) {
            System.out.println("Invalid role selection.");
        }
    }

    private void handlePasswordReset() {
        System.out.println("\n=== Password Reset ===");
        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine();

        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        try {
            authService.resetPassword(userId, email);
            System.out.println("Password reset instructions have been sent to your email.");
        } catch (AuthenticationException e) {
            System.out.println("Password reset failed: " + e.getMessage());
        }
    }

    private String getRoleFromChoice(int choice) {
        switch (choice) {
            case 1: return "Admin";
            case 2: return "Faculty";
            case 3: return "TA";
            case 4: return "Student";
            default: throw new IllegalArgumentException("Invalid role choice");
        }
    }

    // Inner class for handling user input validation
    private static class InputValidator {
        public static boolean isValidUserId(String userId) {
            return userId != null && userId.matches("[a-zA-Z0-9]{3,10}");
        }

        public static boolean isValidPassword(String password) {
            return password != null && password.length() >= 6;
        }

        public static boolean isValidEmail(String email) {
            return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
        }
    }
}
