package com.etextbook.model;

public class ParticipationPoints {
    private Integer participationId;
    private String studentId;
    private String courseId;
    private Integer totalPoints;
    private Integer maxPoints;
    private User student;
    private Course course;

    // Constructors
    public ParticipationPoints() {
        this.totalPoints = 0;
        this.maxPoints = 0;
    }

    public ParticipationPoints(Integer participationId, String studentId, 
                             String courseId) {
        this.participationId = participationId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.totalPoints = 0;
        this.maxPoints = 0;
    }

    // Getters and Setters
    public Integer getParticipationId() { return participationId; }
    public void setParticipationId(Integer participationId) { this.participationId = participationId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public Integer getTotalPoints() { return totalPoints; }
    public void setTotalPoints(Integer totalPoints) { this.totalPoints = totalPoints; }

    public Integer getMaxPoints() { return maxPoints; }
    public void setMaxPoints(Integer maxPoints) { this.maxPoints = maxPoints; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    // Helper methods
    public void addPoints(int points) {
        this.totalPoints += points;
        if (this.totalPoints > this.maxPoints) {
            this.maxPoints = this.totalPoints;
        }
    }

    @Override
    public String toString() {
        return "ParticipationPoints{" +
                "participationId=" + participationId +
                ", studentId='" + studentId + '\'' +
                ", courseId='" + courseId + '\'' +
                ", totalPoints=" + totalPoints +
                ", maxPoints=" + maxPoints +
                ", student=" + student +
                ", course=" + course +
                '}';
    }
}
