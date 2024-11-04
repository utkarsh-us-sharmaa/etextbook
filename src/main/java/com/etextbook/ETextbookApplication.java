// ETextbookApplication.java
package com.etextbook;
import com.etextbook.dao.*;
import com.etextbook.dao.impl.*;
import com.etextbook.service.*;
import com.etextbook.util.*;
import com.etextbook.ui.LoginUI;



public class ETextbookApplication {
    public static void main(String[] args) {
        try {
            // Initialize all DAOs
            UserDAO userDAO = new UserDAOImpl();
            ETextbookDAO textbookDAO = new ETextbookDAOImpl();
            ChapterDAO chapterDAO = new ChapterDAOImpl();
            SectionDAO sectionDAO = new SectionDAOImpl();
            ContentBlockDAO contentBlockDAO = new ContentBlockDAOImpl();
            ActivityDAO activityDAO = new ActivityDAOImpl();
            CourseDAO courseDAO = new CourseDAOImpl();
            EnrollmentDAO enrollmentDAO = new EnrollmentDAOImpl();
            NotificationDAO notificationDAO = new NotificationDAOImpl();
            CourseTADAO courseTADAO = new CourseTADAOImpl();
            CourseCustomizationDAO customizationDAO = new CourseCustomizationDAOImpl();
            StudentActivityDAO studentActivityDAO = new StudentActivityDAOImpl();
            ParticipationPointsDAO participationPointsDAO = new ParticipationPointsDAOImpl();
            CourseContentVersioningDAO versioningDAO = new CourseContentVersioningDAOImpl();

            // Initialize utilities
            PasswordEncoder passwordEncoder = new PasswordEncoder();
            
            // Initialize services
            NotificationService notificationService = new NotificationService(notificationDAO);
            
            AuthenticationService authService = new AuthenticationService(
                userDAO,
                passwordEncoder,
                notificationService);

            ContentService contentService = new ContentService(
                textbookDAO,
                chapterDAO,
                sectionDAO,
                contentBlockDAO,
                versioningDAO,
                notificationService);

            CourseService courseService = new CourseService(
                courseDAO,
                enrollmentDAO,
                notificationService);

            ActivityService activityService = new ActivityService(
                activityDAO,
                studentActivityDAO,
                participationPointsDAO,
                customizationDAO,
                notificationService);

            ParticipationService participationService = new ParticipationService(
                participationPointsDAO,
                studentActivityDAO,
                notificationService);

            CustomizationService customizationService = new CustomizationService(
                customizationDAO,
                versioningDAO,
                notificationService);

            // Test database connection
            System.out.println("Testing database connection...");
            if (DBConnection.testConnection()) {
                System.out.println("Database connection successful!");
                
                // Start notification scheduler
                NotificationScheduler scheduler = new NotificationScheduler(notificationService);
                scheduler.startScheduledTasks();

                // Initialize and start the login UI
                LoginUI loginUI = new LoginUI(
                    authService,
                    courseService,
                    contentService,
                    activityService,
                    participationService,
                    customizationService);

                System.out.println("\nStarting E-Textbook Platform...");
                loginUI.start();

                // Cleanup
                scheduler.shutdown();
            } else {
                System.err.println("Failed to connect to database. Please check your configuration.");
            }
            
        } catch (Exception e) {
            System.err.println("Application failed to start: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printBanner() {
        System.out.println("****************************************");
        System.out.println("*        E-Textbook Platform          *");
        System.out.println("****************************************");
        System.out.println("*           Version 1.0               *");
        System.out.println("****************************************\n");
    }
}
// SessionManager sessionManager=new SessionManager();
//             DBConnection dbConnection=new DBConnection();