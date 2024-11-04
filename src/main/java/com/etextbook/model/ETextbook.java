package com.etextbook.model;

import java.util.ArrayList;
import java.util.List;

public class ETextbook {
    private Integer textbookId;
    private String title;
    private String textContent;
    private String imageUrl;
    private List<Chapter> chapters;

    // Constructors
    public ETextbook() {
        this.chapters = new ArrayList<>();
    }

    public ETextbook(Integer textbookId, String title, String textContent, String imageUrl) {
        this.textbookId = textbookId;
        this.title = title;
        this.textContent = textContent;
        this.imageUrl = imageUrl;
        this.chapters = new ArrayList<>();
    }

    // Getters and Setters
    public Integer getTextbookId() { return textbookId; }
    public void setTextbookId(Integer textbookId) { this.textbookId = textbookId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTextContent() { return textContent; }
    public void setTextContent(String textContent) { this.textContent = textContent; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public List<Chapter> getChapters() { return chapters; }
    public void setChapters(List<Chapter> chapters) { this.chapters = chapters; }

    // Helper methods
    public void addChapter(Chapter chapter) {
        this.chapters.add(chapter);
    }

    @Override
    public String toString() {
        return "ETextbook{" +
                "textbookId=" + textbookId +
                ", title='" + title + '\'' +
                ", chapters=" + chapters.size() +
                '}';
    }
}