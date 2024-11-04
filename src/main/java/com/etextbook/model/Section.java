package com.etextbook.model;

import java.util.ArrayList;
import java.util.List;

public class Section {
    private String sectionId;
    private String sectionNumber;
    private String title;
    private String chapterId;
    private int textbookId;
    private List<ContentBlock> contentBlocks;

    // Constructors
    public Section() {
        this.contentBlocks = new ArrayList<>();
    }

    public Section(String sectionId, String sectionNumber, String title, String chapterId) {
        this.sectionId = sectionId;
        this.sectionNumber = sectionNumber;
        this.title = title;
        this.chapterId = chapterId;
        this.textbookId = textbookId;
        this.contentBlocks = new ArrayList<>();
    }

    // Getters and Setters
    public String getSectionId() { return sectionId; }
    public void setSectionId(String sectionId) { this.sectionId = sectionId; }

    public String getSectionNumber() { return sectionNumber; }
    public void setSectionNumber(String sectionNumber) { this.sectionNumber = sectionNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getChapterId() { return chapterId; }
    public void setChapterId(String chapterId) { this.chapterId = chapterId; }

    public int getTextbookId() { return textbookId; }  // Getter for TextbookID
    public void setTextbookId(int textbookId) { this.textbookId = textbookId; }

    public List<ContentBlock> getContentBlocks() { return contentBlocks; }
    public void setContentBlocks(List<ContentBlock> contentBlocks) { 
        this.contentBlocks = contentBlocks; 
    }

    public void addContentBlock(ContentBlock contentBlock) {
        this.contentBlocks.add(contentBlock);
    }

    @Override
    public String toString() {
        return "Section{" +
                "sectionId=" + sectionId +
                ", sectionNumber='" + sectionNumber + '\'' +
                ", title='" + title + '\'' +
                ", contentBlocks=" + contentBlocks.size() +
                '}';
    }
}
