package com.etextbook.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Course {
    private String courseId;
    private String title;
    private String courseType;
    private String token;
    private Integer capacity;
    private LocalDate startDate;
    private LocalDate endDate;
    private String facultyId;
    private Integer textbookId;
    private List<User> enrolledStudents;
    private List<User> teachingAssistants;

    // Constructors
    public Course() {
        this.enrolledStudents = new ArrayList<>();
        this.teachingAssistants = new ArrayList<>();
    }

    public Course(String courseId, String title, String courseType, String token, 
                 Integer capacity, LocalDate startDate, LocalDate endDate, 
                 String facultyId, Integer textbookId) {
        this.courseId = courseId;
        this.title = title;
        this.courseType = courseType;
        this.token = token;
        this.capacity = capacity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.facultyId = facultyId;
        this.textbookId = textbookId;
        this.enrolledStudents = new ArrayList<>();
        this.teachingAssistants = new ArrayList<>();
    }

    // Getters and Setters
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCourseType() { return courseType; }
    public void setCourseType(String courseType) { this.courseType = courseType; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getFacultyId() { return facultyId; }
    public void setFacultyId(String facultyId) { this.facultyId = facultyId; }

    public Integer getTextbookId() { return textbookId; }
    public void setTextbookId(Integer textbookId) { this.textbookId = textbookId; }

    public List<User> getEnrolledStudents() { return enrolledStudents; }
    public void setEnrolledStudents(List<User> enrolledStudents) { 
        this.enrolledStudents = enrolledStudents; 
    }

    public List<User> getTeachingAssistants() { return teachingAssistants; }
    public void setTeachingAssistants(List<User> teachingAssistants) { 
        this.teachingAssistants = teachingAssistants; 
    }

    // Helper methods
    public void addStudent(User student) {
        if (student.getRole().equals("Student")) {
            this.enrolledStudents.add(student);
        }
    }

    public void addTA(User ta) {
        if (ta.getRole().equals("TA")) {
            this.teachingAssistants.add(ta);
        }
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseId='" + courseId + '\'' +
                ", title='" + title + '\'' +
                ", courseType='" + courseType + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", enrolledStudents=" + enrolledStudents.size() +
                ", teachingAssistants=" + teachingAssistants.size() +
                '}';
    }
}