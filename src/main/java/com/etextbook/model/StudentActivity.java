package com.etextbook.model;

import java.time.LocalDateTime;

public class StudentActivity {
    private Integer studentActivityId;
    private String studentId;
    private Integer activityId;
    private String courseId;
    private LocalDateTime attemptDate;
    private Integer score;

    // Constructors
    public StudentActivity() {}

    public StudentActivity(Integer studentActivityId, String studentId, 
                         Integer activityId, String courseId, Integer score) {
        this.studentActivityId = studentActivityId;
        this.studentId = studentId;
        this.activityId = activityId;
        this.courseId = courseId;
        this.attemptDate = LocalDateTime.now();
        this.score = score;
    }

    // Getters and Setters
    public Integer getStudentActivityId() { return studentActivityId; }
    public void setStudentActivityId(Integer studentActivityId) { this.studentActivityId = studentActivityId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public Integer getActivityId() { return activityId; }
    public void setActivityId(Integer activityId) { this.activityId = activityId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public LocalDateTime getAttemptDate() { return attemptDate; }
    public void setAttemptDate(LocalDateTime attemptDate) { this.attemptDate = attemptDate; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    @Override
    public String toString() {
        return "StudentActivity{" +
                "studentActivityId=" + studentActivityId +
                ", studentId='" + studentId + '\'' +
                ", activityId=" + activityId +
                ", courseId='" + courseId + '\'' +
                ", attemptDate=" + attemptDate +
                ", score=" + score +
                '}';
    }
}
