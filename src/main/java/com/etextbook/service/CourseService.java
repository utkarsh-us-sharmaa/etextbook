package com.etextbook.service;
import com.etextbook.service.exception.ServiceException;
import com.etextbook.util.DBConnection;
import com.etextbook.dao.CourseDAO;
import com.etextbook.dao.EnrollmentDAO;
import com.etextbook.model.Course;
import com.etextbook.model.Enrollment;
import com.etextbook.model.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;
// import com.etextbook.service.exception.ServiceException;
// import com.etextbook.service.NotificationService;
import java.time.LocalDate;
import org.apache.commons.lang3.RandomStringUtils;
import java.util.List;

public class CourseService {
    private final CourseDAO courseDAO;
    private final EnrollmentDAO enrollmentDAO;
    private final NotificationService notificationService;

    public CourseService(CourseDAO courseDAO, EnrollmentDAO enrollmentDAO, 
                        NotificationService notificationService) {
        this.courseDAO = courseDAO;
        this.enrollmentDAO = enrollmentDAO;
        this.notificationService = notificationService;
    }

    public Course createCourse(Course course, User faculty) {
        validateCourseData(course);
        
        if (!"Faculty".equals(faculty.getRole())) {
            throw new ServiceException("Only faculty can create courses");
        }

        course.setFacultyId(faculty.getUserId());
        if ("Active".equals(course.getCourseType())) {
            course.setToken(generateCourseToken());
        }

        courseDAO.save(course);
        notificationService.sendCourseCreationNotification(faculty, course);
        return course;
    }

    public void enrollStudent(String courseId, User student, String token) {
        Course course = courseDAO.findById(courseId)
            .orElseThrow(() -> new ServiceException("Course not found"));

        if (!"Student".equals(student.getRole())) {
            throw new ServiceException("Only students can enroll in courses");
        }

        if ("Active".equals(course.getCourseType()) && !course.getToken().equals(token)) {
            throw new ServiceException("Invalid course token");
        }

        if (isEnrollmentLimitReached(course)) {
            throw new ServiceException("Course capacity reached");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(student.getUserId());
        enrollment.setCourseId(courseId);
        enrollment.setStatus("Pending");
        enrollment.setRequestDate(LocalDate.now());

        enrollmentDAO.save(enrollment);
        notificationService.sendEnrollmentRequestNotification(course, student);
    }

    public void approveEnrollment(Integer enrollmentId, User approver) {
        if (!List.of("Faculty", "TA").contains(approver.getRole())) {
            throw new ServiceException("Unauthorized to approve enrollments");
        }

        Enrollment enrollment = enrollmentDAO.findById(enrollmentId)
            .orElseThrow(() -> new ServiceException("Enrollment not found"));

        enrollment.setStatus("Approved");
        enrollment.setApprovalDate(LocalDate.now());
        enrollmentDAO.update(enrollment);

        notificationService.sendEnrollmentApprovalNotification(enrollment);
    }

    public List<Course> findActiveCourses() {
        return courseDAO.findByType("Active");
    }

    public List<Course> findCoursesByFaculty(String facultyId) {
        return courseDAO.findByFaculty(facultyId);
    }

    private void validateCourseData(Course course) {
        if (course.getStartDate().isAfter(course.getEndDate())) {
            throw new ServiceException("Start date must be before end date");
        }
        if (course.getStartDate().isBefore(LocalDate.now())) {
            throw new ServiceException("Start date cannot be in the past");
        }
    }

    private String generateCourseToken() {
        // Generate a random 7-character alphanumeric token
        return RandomStringUtils.randomAlphanumeric(7).toUpperCase();
    }

    private boolean isEnrollmentLimitReached(Course course) {
        if ("Evaluation".equals(course.getCourseType())) {
            return false;
        }
        long currentEnrollments = enrollmentDAO.findByCourse(course.getCourseId())
            .stream()
            .filter(e -> "Approved".equals(e.getStatus()))
            .count();
        return currentEnrollments >= course.getCapacity();  // This will work as Capacity is Integer
    }

    public boolean isAssignedTA(String taId, String courseId) {
        try {
            // Query the CourseTA table to check if there's a matching record
            String query = "SELECT COUNT(*) FROM CourseTA WHERE TAID = ? AND CourseID = ?";
            
            try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, taId);
                stmt.setString(2, courseId);
                
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        } catch (SQLException e) {
            throw new ServiceException("Error checking TA assignment: " + e.getMessage(), e);
        }
    }

    public Course getCourseById(String courseId) {
        try {
            return courseDAO.findById(courseId)
                .orElseThrow(() -> new ServiceException("Course not found"));
        } catch (Exception e) {
            throw new ServiceException("Error retrieving course: " + e.getMessage(), e);
        }
    }

    public List<Course> findCoursesByTA(String taId) {
        try {
            // First, verify that the user is a TA
            String query = "SELECT c.* FROM Course c " +
                          "JOIN CourseTA ct ON c.CourseID = ct.CourseID " +
                          "JOIN User u ON ct.TAID = u.UserID " +
                          "WHERE u.UserID = ? AND u.Role = 'TA'";
            
            List<Course> courses = new ArrayList<>();
            
            try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, taId);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    Course course = new Course();
                    course.setCourseId(rs.getString("CourseID"));
                    course.setTitle(rs.getString("Title"));
                    course.setCourseType(rs.getString("CourseType"));
                    course.setToken(rs.getString("Token"));
                    course.setCapacity(rs.getInt("Capacity"));
                    course.setStartDate(rs.getDate("StartDate").toLocalDate());
                    course.setEndDate(rs.getDate("EndDate").toLocalDate());
                    course.setFacultyId(rs.getString("FacultyID"));
                    course.setTextbookId(rs.getInt("TextbookID"));
                    courses.add(course);
                }
                return courses;
            }
        } catch (SQLException e) {
            throw new ServiceException("Error finding courses for TA: " + e.getMessage(), e);
        }
    }

    public List<User> findEnrolledStudents(String courseId) {
        try {
            String query = "SELECT u.* FROM User u " +
                          "JOIN Enrollment e ON u.UserID = e.StudentID " +
                          "WHERE e.CourseID = ? " +
                          "AND e.Status = 'Approved' " +
                          "ORDER BY u.LastName, u.FirstName";
            
            List<User> students = new ArrayList<>();
            
            try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, courseId);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    User student = new User();
                    student.setUserId(rs.getString("UserID"));
                    student.setFirstName(rs.getString("FirstName"));
                    student.setLastName(rs.getString("LastName"));
                    student.setEmail(rs.getString("Email"));
                    student.setRole(rs.getString("Role"));
                    students.add(student);
                }
                return students;
            }
        } catch (SQLException e) {
            throw new ServiceException("Error finding enrolled students: " + e.getMessage(), e);
        }
    }



}