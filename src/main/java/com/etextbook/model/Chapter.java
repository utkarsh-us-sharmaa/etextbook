package com.etextbook.model;

import java.util.ArrayList;
import java.util.List;


public class Chapter {
    private String chapterId;
    private String chapterNumber;
    private String title;
    private Integer textbookId;
    private List<Section> sections;

    // Constructors
    public Chapter() {
        this.sections = new ArrayList<>();
    }

    public Chapter(String chapterId, String chapterNumber, String title, Integer textbookId) {
        this.chapterId = chapterId;
        this.chapterNumber = chapterNumber;
        this.title = title;
        this.textbookId = textbookId;
        this.sections = new ArrayList<>();
    }

    // Getters and Setters
    public String getChapterId() { return chapterId; }
    public void setChapterId(String chapterId) { this.chapterId = chapterId; }

    public String getChapterNumber() { return chapterNumber; }
    public void setChapterNumber(String chapterNumber) { this.chapterNumber = chapterNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getTextbookId() { return textbookId; }
    public void setTextbookId(Integer textbookId) { this.textbookId = textbookId; }

    public List<Section> getSections() { return sections; }
    public void setSections(List<Section> sections) { this.sections = sections; }

    // Helper methods
    public void addSection(Section section) {
        this.sections.add(section);
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "chapterId=" + chapterId +
                ", chapterNumber='" + chapterNumber + '\'' +
                ", title='" + title + '\'' +
                ", sections=" + sections.size() +
                '}';
    }
}