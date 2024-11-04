package com.etextbook.model;

public class CourseContentVersioning {
    private Integer versionId;
    private String courseId;
    private String chapterId;
    private String sectionId;
    private Integer displayOrder;
    private Course course;
    private Chapter chapter;
    private Section section;

    // Constructors
    public CourseContentVersioning() {}

    public CourseContentVersioning(Integer versionId, String courseId, 
                                 String chapterId, Integer displayOrder) {
        this.versionId = versionId;
        this.courseId = courseId;
        this.chapterId = chapterId;
        this.displayOrder = displayOrder;
    }

    // Getters and Setters
    public Integer getVersionId() { return versionId; }
    public void setVersionId(Integer versionId) { this.versionId = versionId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getChapterId() { return chapterId; }
    public void setChapterId(String chapterId) { this.chapterId = chapterId; }

    public String getSectionId() { return sectionId; }
    public void setSectionId(String sectionId) { this.sectionId = sectionId; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public Chapter getChapter() { return chapter; }
    public void setChapter(Chapter chapter) { this.chapter = chapter; }

    public Section getSection() { return section; }
    public void setSection(Section section) { this.section = section; }

    @Override
    public String toString() {
        return "CourseContentVersioning{" +
                "versionId=" + versionId +
                ", courseId='" + courseId + '\'' +
                ", chapterId=" + chapterId +
                ", sectionId=" + sectionId +
                ", displayOrder=" + displayOrder +
                ", course=" + course +
                ", chapter=" + chapter +
                ", section=" + section +
                '}';
    }
}
