// ui/FacultyMenu.java
package com.etextbook.ui;

import com.etextbook.model.User;
import com.etextbook.service.*;

public class FacultyMenu extends BaseMenu {
    private final CourseService courseService;
    private final ContentService contentService;
    private final ActivityService activityService;
    private final CustomizationService customizationService;

    public FacultyMenu(User user, String sessionId, CourseService courseService,
                      ContentService contentService, ActivityService activityService,
                      CustomizationService customizationService) {
        super(user, sessionId);
        this.courseService = courseService;
        this.contentService = contentService;
        this.activityService = activityService;
        this.customizationService = customizationService;
    }

    @Override
    protected void displayMenu() {
        System.out.println("\n=== Faculty Menu ===");
        System.out.println("1. Create Course");
        System.out.println("2. Manage Courses");
        System.out.println("3. Manage Content");
        System.out.println("4. Create Activities");
        System.out.println("5. View Student Progress");
        System.out.println("6. Manage TAs");
        System.out.println("7. Approve Enrollments");
        System.out.println("8. Change Password");
        System.out.println("9. Logout");
        System.out.print("Enter your choice: ");
    }

    @Override
    protected boolean handleMenuChoice(int choice) {
        switch (choice) {
            case 1:
                // createCourse();
                return true;
            case 2:
                // manageCourses();
                return true;
            case 3:
                // manageContent();
                return true;
            case 4:
                // createActivities();
                return true;
            case 5:
                // viewStudentProgress();
                return true;
            case 6:
                // manageTAs();
                return true;
            case 7:
                // approveEnrollments();
                return true;
            case 8:
                // changePassword();
                return true;
            case 9:
                System.out.println("Logging out...");
                return false;
            default:
                System.out.println("Invalid choice. Please try again.");
                return true;
        }
    }

    // Implement other menu methods...
}