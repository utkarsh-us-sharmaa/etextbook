package com.etextbook.model;

public class CourseCustomization {
    private Integer customizationId;
    private String courseId;
    private Integer contentBlockId;
    private Integer activityId;
    private Boolean isHidden;
    private String addedByRole;
    private Boolean isOriginalContent;
    private Integer displayOrder;
    private String createdByUserId;
    private ContentBlock contentBlock;
    private Activity activity;

    // Constructors
    public CourseCustomization() {
        this.isHidden = false;
        this.isOriginalContent = true;
    }

    public CourseCustomization(Integer customizationId, String courseId, 
                             Integer displayOrder, String createdByUserId) {
        this.customizationId = customizationId;
        this.courseId = courseId;
        this.displayOrder = displayOrder;
        this.createdByUserId = createdByUserId;
        this.isHidden = false;
        this.isOriginalContent = true;
    }

    // Getters and Setters
    public Integer getCustomizationId() { return customizationId; }
    public void setCustomizationId(Integer customizationId) { this.customizationId = customizationId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public Integer getContentBlockId() { return contentBlockId; }
    public void setContentBlockId(Integer contentBlockId) { this.contentBlockId = contentBlockId; }

    public Integer getActivityId() { return activityId; }
    public void setActivityId(Integer activityId) { this.activityId = activityId; }

    public Boolean getIsHidden() { return isHidden; }
    public void setIsHidden(Boolean isHidden) { this.isHidden = isHidden; }

    public String getAddedByRole() { return addedByRole; }
    public void setAddedByRole(String addedByRole) { this.addedByRole = addedByRole; }

    public Boolean getIsOriginalContent() { return isOriginalContent; }
    public void setIsOriginalContent(Boolean isOriginalContent) { this.isOriginalContent = isOriginalContent; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public String getCreatedByUserId() { return createdByUserId; }
    public void setCreatedByUserId(String createdByUserId) { this.createdByUserId = createdByUserId; }

    public ContentBlock getContentBlock() { return contentBlock; }
    public void setContentBlock(ContentBlock contentBlock) { this.contentBlock = contentBlock; }

    public Activity getActivity() { return activity; }
    public void setActivity(Activity activity) { this.activity = activity; }

    @Override
    public String toString() {
        return "CourseCustomization{" +
                "customizationId=" + customizationId +
                ", courseId='" + courseId + '\'' +
                ", contentBlockId=" + contentBlockId +
                ", activityId=" + activityId +
                ", isHidden=" + isHidden +
                ", addedByRole='" + addedByRole + '\'' +
                ", isOriginalContent=" + isOriginalContent +
                ", displayOrder=" + displayOrder +
                ", createdByUserId='" + createdByUserId + '\'' +
                ", contentBlock=" + contentBlock +
                ", activity=" + activity +
                '}';
    }
}
