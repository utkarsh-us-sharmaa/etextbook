// ui/TAMenu.java
package com.etextbook.ui;

import com.etextbook.model.Chapter;
import com.etextbook.model.Course;
import com.etextbook.model.Section;
import com.etextbook.model.User;
import com.etextbook.service.*;
import java.util.Scanner;
import java.util.List;
import com.etextbook.service.exception.ServiceException;


public class TAMenu extends BaseMenu {
    private final Scanner scanner;
    private final CourseService courseService;
    private final ContentService contentService;
    private final ActivityService activityService;
    private final AuthenticationService authService;
    private final User currentUser;
    private final LoginUI loginUI;
    

    public TAMenu(String sessionId, CourseService courseService, 
                  ContentService contentService, ActivityService activityService, 
                  AuthenticationService authService, User currentUser, LoginUI loginUI) {
        super(currentUser, sessionId); // Ensure user is passed correctly to the BaseMenu
        this.courseService = courseService;
        this.contentService = contentService;
        this.activityService = activityService; // Added to initialize the activityService
        this.authService = authService; // Added to initialize the authenticationService
        this.currentUser = currentUser; // Added to initialize the currentUser
        this.scanner = new Scanner(System.in); // Initialize scanner for user input
        this.loginUI = loginUI;
    }

    @Override
    public void displayMenu() {
        while (true) {
            System.out.println("\n=== TA Menu ===");
            System.out.println("1. Go to Active Course");
            System.out.println("2. View Courses");
            System.out.println("3. Change Password");
            System.out.println("4. Logout");
            System.out.print("Enter your choice (1-4): ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    goToActiveCourse();
                    break;
                case 2:
                    viewCourses();
                    break;
                case 3:
                    changePassword();
                    break;
                case 4:
                    System.out.println("Logging out...");
                    loginUI.displayMainMenu();
                    return;
                    
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void goToActiveCourse() {
        System.out.print("Enter Course ID: ");
        String courseId = scanner.nextLine();
        
        try {
            // Verify if TA is assigned to this course
            if (!courseService.isAssignedTA(currentUser.getUserId(), courseId)) {
                System.out.println("You are not assigned to this course.");
                return;
            }

            while (true) {
                System.out.println("\n=== Course Menu ===");
                System.out.println("1. View Students");
                System.out.println("2. Add New Chapter");
                System.out.println("3. Modify Chapters");
                System.out.println("4. Go Back");
                System.out.print("Enter your choice (1-4): ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        viewStudents(courseId);
                        break;
                    case 2:
                        addNewChapter(courseId);
                        break;
                    case 3:
                        modifyChapters(courseId);
                        break;
                    case 4:
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewCourses() {
        try {
            List<Course> courses = courseService.findCoursesByTA(currentUser.getUserId());
            if (courses.isEmpty()) {
                System.out.println("You are not assigned to any courses.");
                return;
            }

            System.out.println("\n=== Assigned Courses ===");
            for (Course course : courses) {
                System.out.printf("Course ID: %s, Title: %s\n", 
                    course.getCourseId(), course.getTitle());
            }

            System.out.println("\n1. Go Back");
            System.out.print("Enter your choice (1): ");
            scanner.nextInt();
            scanner.nextLine(); // Consume newline
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    private void changePassword() {
        try {
            System.out.print("Enter current password: ");
            String currentPassword = scanner.nextLine();
            
            System.out.print("Enter new password: ");
            String newPassword = scanner.nextLine();
            
            System.out.print("Confirm new password: ");
            String confirmPassword = scanner.nextLine();

            if (!newPassword.equals(confirmPassword)) {
                System.out.println("New passwords do not match!");
                return;
            }

            System.out.println("\n1. Update");
            System.out.println("2. Go Back");
            System.out.print("Enter your choice (1-2): ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (choice == 1) {
                authService.changePassword(
                    currentUser.getUserId(), 
                    currentPassword, 
                    newPassword
                );
                System.out.println("Password updated successfully!");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewStudents(String courseId) {
        try {
            List<User> students = courseService.findEnrolledStudents(courseId);
            
            if (students.isEmpty()) {
                System.out.println("\nNo students enrolled in this course.");
                return;
            }
    
            System.out.println("\n=== Enrolled Students ===");
            System.out.printf("%-10s %-20s %-30s%n", 
                "ID", "Name", "Email");
            System.out.println("------------------------------------------------");
            
            for (User student : students) {
                System.out.printf("%-10s %-20s %-30s%n",
                    student.getUserId(),
                    student.getFirstName() + " " + student.getLastName(),
                    student.getEmail());
            }
    
            System.out.println("\n1. Go Back");
            System.out.print("Enter your choice (1): ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            if (choice != 1) {
                System.out.println("Invalid choice. Going back to previous menu.");
            }
        } catch (ServiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void addNewChapter(String courseId) {
        try {
            // First verify if the course exists and TA has access
            if (!courseService.isAssignedTA(currentUser.getUserId(), courseId)) {
                System.out.println("You are not authorized to add chapters to this course.");
                return;
            }
    
            // Get course details to get textbook ID
            Course course = courseService.getCourseById(courseId);
            if (course == null || course.getTextbookId() == null) {
                System.out.println("Invalid course or no textbook assigned.");
                return;
            }
    
            while (true) {
                System.out.println("\n=== Add New Chapter ===");
                
                // Get chapter details
                System.out.print("Enter Chapter ID (format: chap_X, where X is a number): ");
                String chapterId = scanner.nextLine();
                
                System.out.print("Enter Chapter Title: ");
                String chapterTitle = scanner.nextLine().trim();
    
                // Display menu
                System.out.println("\n1. Add New Section");
                System.out.println("2. Go Back");
                System.out.print("Enter your choice (1-2): ");
    
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
    
                switch (choice) {
                    case 1:
                        try {
                            // Create the chapter
                            Chapter chapter = contentService.createChapter(
                                chapterId,
                                chapterTitle,
                                course.getTextbookId(),
                                currentUser
                            );
                            System.out.println("Chapter created successfully!");
                            
                            // Go to add section
                            addNewSection(chapter.getChapterId());
                        } catch (ServiceException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;
                        
                    case 2:
                        return;
                        
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void addNewSection(String chapterId) {
        try {
            while (true) {
                System.out.println("\n=== Add New Section ===");
                
                System.out.print("Enter Section Number: ");
                String sectionNumber = scanner.nextLine().trim();
                
                System.out.print("Enter Section Title: ");
                String sectionTitle = scanner.nextLine().trim();
    
                System.out.println("\n1. Add New Content Block");
                System.out.println("2. Go Back");
                System.out.print("Enter your choice (1-2): ");
    
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
    
                switch (choice) {
                    case 1:
                        try {
                            Section section = contentService.createSection(
                                sectionNumber,
                                sectionTitle,
                                chapterId,
                                currentUser
                            );
                            System.out.println("Section created successfully!");
                            addNewContentBlock(section.getSectionId());
                        } catch (ServiceException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;
                        
                    case 2:
                        return;
                        
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void modifyChapters(String courseId) {
        try {
            System.out.println("\n=== Modify Chapter ===");
            
            // Get chapter ID
            System.out.print("Enter Chapter ID: ");
            String chapterId = scanner.nextLine().trim();
            
            // Verify chapter exists and belongs to the course's textbook
            Course course = courseService.getCourseById(courseId);
            List<Chapter> chapters = contentService.getChaptersByTextbook(course.getTextbookId());
            
            boolean chapterFound = false;
            for (Chapter chapter : chapters) {
                if (chapter.getChapterId().equals(chapterId)) {
                    chapterFound = true;
                    break;
                }
            }
            
            if (!chapterFound) {
                System.out.println("Chapter not found in this course.");
                return;
            }
    
            while (true) {
                System.out.println("\n1. Add New Section");
                System.out.println("2. Modify Section");
                System.out.println("3. Go Back");
                System.out.print("Enter your choice (1-3): ");
    
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
    
                switch (choice) {
                    case 1:
                        addNewSection(chapterId);
                        break;
                    case 2:
                        modifySection(chapterId);
                        break;
                    case 3:
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (ServiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void modifySection(String chapterId) {
        try {
            System.out.println("\n=== Modify Section ===");
            
            System.out.print("Enter Section Number: ");
            String sectionNumber = scanner.nextLine().trim();
            
            // Verify section exists
            List<Section> sections = contentService.getSectionsByChapter(chapterId);
            Section sectionToModify = null;
            
            for (Section section : sections) {
                if (section.getSectionNumber().equals(sectionNumber)) {
                    sectionToModify = section;
                    break;
                }
            }
            
            if (sectionToModify == null) {
                System.out.println("Section not found in this chapter.");
                return;
            }
    
            System.out.print("Enter New Section Title (press Enter to keep current): ");
            String newTitle = scanner.nextLine().trim();
            
            if (!newTitle.isEmpty()) {
                sectionToModify.setTitle(newTitle);
                contentService.updateSection(sectionToModify, currentUser);
                System.out.println("Section updated successfully!");
            }
            
        } catch (ServiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void addNewContentBlock(String sectionId) {
        try {
            System.out.println("\n=== Add New Content Block ===");
            
            System.out.print("Enter Content Block ID: ");
            String contentBlockId = scanner.nextLine().trim();
    
            while (true) {
                System.out.println("\n1. Add Text");
                System.out.println("2. Add Picture");
                System.out.println("3. Add Activity");
                System.out.println("4. Hide Activity");
                System.out.println("5. Go Back");
                System.out.print("Enter your choice (1-5): ");
    
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
    
                switch (choice) {
                    case 1:
                        // addText(contentBlockId, sectionId);
                        break;
                        
                    case 2:
                        // addPicture(contentBlockId, sectionId);
                        break;
                        
                    case 3:
                        // addActivity(contentBlockId, sectionId);
                        break;
                        
                    case 4:
                        // hideActivity(contentBlockId, sectionId);
                        break;
                        
                    case 5:
                        return;
                        
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    

    


    


    @Override
    protected boolean handleMenuChoice(int choice) {
        switch (choice) {
            case 1:
                // viewAssignedCourses();
                return true;
            case 2:
                // createActivities();
                return true;
            case 3:
                // viewStudentProgress();
                return true;
            case 4:
                // manageContent();
                return true;
            case 5:
                // approveEnrollments();
                return true;
            case 6:
                //changePassword();
                return true;
            case 7:
                System.out.println("Logging out...");
                return false;
            default:
                System.out.println("Invalid choice. Please try again.");
                return true;
        }
    }

    // Implement other menu methods...
}