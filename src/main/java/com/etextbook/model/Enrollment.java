package com.etextbook.model;

import java.time.LocalDate;

public class Enrollment {
    private Integer enrollmentId;
    private String studentId;
    private String courseId;
    private String status;
    private LocalDate requestDate;
    private LocalDate approvalDate;

    // Constructors
    public Enrollment() {}

    public Enrollment(Integer enrollmentId, String studentId, String courseId,
                     String status, LocalDate requestDate) {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.status = status;
        this.requestDate = requestDate;
    }

    // Getters and Setters
    public Integer getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(Integer enrollmentId) { 
        this.enrollmentId = enrollmentId; 
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDate requestDate) { 
        this.requestDate = requestDate; 
    }

    public LocalDate getApprovalDate() { return approvalDate; }
    public void setApprovalDate(LocalDate approvalDate) { 
        this.approvalDate = approvalDate; 
    }

    @Override
    public String toString() {
        return "Enrollment{" +
                "enrollmentId=" + enrollmentId +
                ", studentId='" + studentId + '\'' +
                ", courseId='" + courseId + '\'' +
                ", status='" + status + '\'' +
                ", requestDate=" + requestDate +
                ", approvalDate=" + approvalDate +
                '}';
    }
}

