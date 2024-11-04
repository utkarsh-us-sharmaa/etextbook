// ui/StudentMenu.java
package com.etextbook.ui;

import com.etextbook.model.User;
import com.etextbook.service.*;

public class StudentMenu extends BaseMenu {
    private final CourseService courseService;
    private final ActivityService activityService;
    private final ParticipationService participationService;

    public StudentMenu(User user, String sessionId, CourseService courseService,
                      ActivityService activityService, 
                      ParticipationService participationService) {
        super(user, sessionId);
        this.courseService = courseService;
        this.activityService = activityService;
        this.participationService = participationService;
    }

    @Override
    protected void displayMenu() {
        System.out.println("\n=== Student Menu ===");
        System.out.println("1. View Enrolled Courses");
        System.out.println("2. Enroll in Course");
        System.out.println("3. View Course Content");
        System.out.println("4. Complete Activities");
        System.out.println("5. View Progress");
        System.out.println("6. View Participation Points");
        System.out.println("7. Change Password");
        System.out.println("8. Logout");
        System.out.print("Enter your choice: ");
    }

    @Override
    protected boolean handleMenuChoice(int choice) {
        switch (choice) {
            case 1:
                // viewEnrolledCourses();
                return true;
            case 2:
                // enrollInCourse();
                return true;
            case 3:
                // viewCourseContent();
                return true;
            case 4:
                // completeActivities();
                return true;
            case 5:
                // viewProgress();
                return true;
            case 6:
                // viewParticipationPoints();
                return true;
            case 7:
                // changePassword();
                return true;
            case 8:
                System.out.println("Logging out...");
                return false;
            default:
                System.out.println("Invalid choice. Please try again.");
                return true;
        }
    }

    // Implement other menu methods...
}