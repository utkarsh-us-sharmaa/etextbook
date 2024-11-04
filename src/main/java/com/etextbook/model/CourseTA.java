package com.etextbook.model;

public class CourseTA {
    private Integer courseTAId;
    private String courseId;
    private String taId;
    private User ta;
    private Course course;

    // Constructors
    public CourseTA() {}

    public CourseTA(Integer courseTAId, String courseId, String taId) {
        this.courseTAId = courseTAId;
        this.courseId = courseId;
        this.taId = taId;
    }

    // Getters and Setters
    public Integer getCourseTAId() { return courseTAId; }
    public void setCourseTAId(Integer courseTAId) { this.courseTAId = courseTAId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getTaId() { return taId; }
    public void setTaId(String taId) { this.taId = taId; }

    public User getTa() { return ta; }
    public void setTa(User ta) { this.ta = ta; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    @Override
    public String toString() {
        return "CourseTA{" +
                "courseTAId=" + courseTAId +
                ", courseId='" + courseId + '\'' +
                ", taId='" + taId + '\'' +
                '}';
    }
}