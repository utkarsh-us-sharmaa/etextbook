package com.etextbook.model;

public class ContentBlock {
    private Integer contentBlockId;
    private String contentType;
    private String content;
    private Integer sequenceNumber;
    private String sectionId;
    private Activity activity;

    // Constructors
    public ContentBlock() {}

    public ContentBlock(Integer contentBlockId, String contentType, String content, 
                       Integer sequenceNumber, String sectionId) {
        this.contentBlockId = contentBlockId;
        this.contentType = contentType;
        this.content = content;
        this.sequenceNumber = sequenceNumber;
        this.sectionId = sectionId;
    }

    // Getters and Setters
    public Integer getContentBlockId() { return contentBlockId; }
    public void setContentBlockId(Integer contentBlockId) { 
        this.contentBlockId = contentBlockId; 
    }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getSequenceNumber() { return sequenceNumber; }
    public void setSequenceNumber(Integer sequenceNumber) { 
        this.sequenceNumber = sequenceNumber; 
    }

    public String getSectionId() { return sectionId; }
    public void setSectionId(String sectionId) { this.sectionId = sectionId; }

    public Activity getActivity() { return activity; }
    public void setActivity(Activity activity) { this.activity = activity; }

    @Override
    public String toString() {
        return "ContentBlock{" +
                "contentBlockId=" + contentBlockId +
                ", contentType='" + contentType + '\'' +
                ", sequenceNumber=" + sequenceNumber +
                ", hasActivity=" + (activity != null) +
                '}';
    }
}