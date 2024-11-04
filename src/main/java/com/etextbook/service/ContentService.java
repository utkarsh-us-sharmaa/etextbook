package com.etextbook.service;
// import com.etextbook.service.NotificationService;
import com.etextbook.dao.*;
import com.etextbook.model.*;
import com.etextbook.service.exception.ServiceException;

import java.util.List;
// import java.util.Optional;

public class ContentService {
    private final ETextbookDAO textbookDAO;
    private final ChapterDAO chapterDAO;
    private final SectionDAO sectionDAO;
    private final ContentBlockDAO contentBlockDAO;
    private final CourseContentVersioningDAO versioningDAO;
    private final NotificationService notificationService;

    public ContentService(ETextbookDAO textbookDAO, ChapterDAO chapterDAO,
                         SectionDAO sectionDAO, ContentBlockDAO contentBlockDAO,
                         CourseContentVersioningDAO versioningDAO,
                         NotificationService notificationService) {
        this.textbookDAO = textbookDAO;
        this.chapterDAO = chapterDAO;
        this.sectionDAO = sectionDAO;
        this.contentBlockDAO = contentBlockDAO;
        this.versioningDAO = versioningDAO;
        this.notificationService = notificationService;
    }

    // Textbook Management
    public ETextbook createTextbook(ETextbook textbook, User creator) {
        validateTextbookData(textbook);
        
        try {
            textbookDAO.save(textbook);
            notificationService.sendTextbookCreationNotification(creator, textbook);
            return textbook;
        } catch (Exception e) {
            throw new ServiceException("Error creating textbook", e);
        }
    }

    public void updateTextbook(ETextbook textbook, User updater) {
        if (!hasPermissionToModifyContent(updater)) {
            throw new ServiceException("Unauthorized to modify textbook content");
        }

        validateTextbookData(textbook);
        textbookDAO.update(textbook);
    }

    // Chapter Management
    public Chapter addChapter(Chapter chapter, User creator) {
        if (!hasPermissionToModifyContent(creator)) {
            throw new ServiceException("Unauthorized to add chapters");
        }

        validateChapterData(chapter);
        
        try {
            chapterDAO.save(chapter);
            updateContentVersioning(chapter);
            return chapter;
        } catch (Exception e) {
            throw new ServiceException("Error adding chapter", e);
        }
    }

    public void updateChapterOrder(Integer textbookId, List<Chapter> newOrder, User updater) {
        if (!hasPermissionToModifyContent(updater)) {
            throw new ServiceException("Unauthorized to modify chapter order");
        }

        try {
            for (int i = 0; i < newOrder.size(); i++) {
                Chapter chapter = newOrder.get(i);
                CourseContentVersioning versioning = new CourseContentVersioning();
                versioning.setChapterId(chapter.getChapterId());
                versioning.setDisplayOrder(i + 1);
                versioningDAO.save(versioning);
            }
        } catch (Exception e) {
            throw new ServiceException("Error updating chapter order", e);
        }
    }

    // Section Management
    public Section addSection(Section section, User creator) {
        if (!hasPermissionToModifyContent(creator)) {
            throw new ServiceException("Unauthorized to add sections");
        }

        validateSectionData(section);
        
        try {
            sectionDAO.save(section);
            updateContentVersioning(section);
            return section;
        } catch (Exception e) {
            throw new ServiceException("Error adding section", e);
        }
    }

    // Content Block Management
    public ContentBlock addContentBlock(ContentBlock contentBlock, User creator) {
        if (!hasPermissionToModifyContent(creator)) {
            throw new ServiceException("Unauthorized to add content blocks");
        }

        validateContentBlockData(contentBlock);
        
        try {
            contentBlockDAO.save(contentBlock);
            return contentBlock;
        } catch (Exception e) {
            throw new ServiceException("Error adding content block", e);
        }
    }

    public void updateContentBlockSequence(String sectionId, List<ContentBlock> newSequence, User updater) {
        if (!hasPermissionToModifyContent(updater)) {
            throw new ServiceException("Unauthorized to modify content sequence");
        }

        try {
            for (int i = 0; i < newSequence.size(); i++) {
                ContentBlock block = newSequence.get(i);
                block.setSequenceNumber(i + 1);
                contentBlockDAO.update(block);
            }
        } catch (Exception e) {
            throw new ServiceException("Error updating content sequence", e);
        }
    }

    // Content Retrieval Methods
    public List<Chapter> getChaptersByTextbook(Integer textbookId) {
        return chapterDAO.findByTextbook(textbookId);
    }

    public List<Section> getSectionsByChapter(String chapterId) {
        return sectionDAO.findByChapter(chapterId);
    }

    public List<ContentBlock> getContentBlocksBySection(String sectionId) {
        return contentBlockDAO.findBySection(sectionId);
    }

    // Validation Methods
    private void validateTextbookData(ETextbook textbook) {
        if (textbook.getTitle() == null || textbook.getTitle().trim().isEmpty()) {
            throw new ServiceException("Textbook title is required");
        }
        if (textbook.getTextContent() == null && textbook.getImageUrl() == null) {
            throw new ServiceException("Textbook must have either text content or image URL");
        }
    }

    private void validateChapterData(Chapter chapter) {
        if (chapter.getChapterNumber() == null || !chapter.getChapterNumber().matches("chap_\\d+")) {
            throw new ServiceException("Invalid chapter number format. Must be 'chap_X'");
        }
        if (chapter.getTitle() == null || chapter.getTitle().trim().isEmpty()) {
            throw new ServiceException("Chapter title is required");
        }
    }

    private void validateSectionData(Section section) {
        if (section.getSectionNumber() == null || section.getSectionNumber().trim().isEmpty()) {
            throw new ServiceException("Section number is required");
        }
        if (section.getTitle() == null || section.getTitle().trim().isEmpty()) {
            throw new ServiceException("Section title is required");
        }
    }

    private void validateContentBlockData(ContentBlock contentBlock) {
        if (!List.of("Text", "Image").contains(contentBlock.getContentType())) {
            throw new ServiceException("Invalid content type");
        }
        if (contentBlock.getContent() == null || contentBlock.getContent().trim().isEmpty()) {
            throw new ServiceException("Content is required");
        }
    }

    private boolean hasPermissionToModifyContent(User user) {
        return List.of("Admin", "Faculty").contains(user.getRole());
    }

    private void updateContentVersioning(Chapter chapter) {
        CourseContentVersioning versioning = new CourseContentVersioning();
        versioning.setChapterId(chapter.getChapterId());
        versioning.setDisplayOrder(getNextDisplayOrder(chapter.getTextbookId()));
        versioningDAO.save(versioning);
    }

    private void updateContentVersioning(Section section) {
        CourseContentVersioning versioning = new CourseContentVersioning();
        versioning.setSectionId(section.getSectionId());
        versioning.setChapterId(section.getChapterId());
        Integer nextDisplayOrder = getNextDisplayOrder(section.getChapterId());
    versioning.setDisplayOrder(nextDisplayOrder != null ? nextDisplayOrder : 1);
        versioningDAO.save(versioning);
    }

    public void updateSection(Section section, User updater) {
        try {
            // Validate section data
            validateSectionData(section);
            
            // Update section
            sectionDAO.update(section);
            
            // Send notification
            notificationService.sendSectionUpdateNotification(updater, section);
        } catch (Exception e) {
            throw new ServiceException("Error updating section: " + e.getMessage(), e);
        }
    }

    private int getNextDisplayOrder(Integer parentId) {
        List<CourseContentVersioning> existing = versioningDAO.findByChapter(String.valueOf(parentId));
        return existing.isEmpty() ? 1 : existing.size() + 1;
    }

    private int getNextDisplayOrder(String chapterId) {
        List<CourseContentVersioning> existing = versioningDAO.findByChapter(chapterId);
        return (existing == null || existing.isEmpty()) ? 1 : existing.size() + 1;
    }
    

    // private int getNextDisplayOrder(String chapterId) {
    //     List<CourseContentVersioning> existing = versioningDAO.findByChapter(chapterId);
    //     return existing.isEmpty() ? 1 : existing.size() + 1;
    // }

    public Chapter createChapter(String chapterId, String title, Integer textbookId, User createdBy) {
        try {
            // Validate chapter ID format
            // if (!chapterId.matches("chap_\\d+")) {
            //     throw new ServiceException("Chapter ID must be in format 'chap_X' where X is a number");
            // }
    
            // Check if chapter ID already exists
            if (chapterDAO.findById(chapterId).isPresent()) {
                throw new ServiceException("Chapter ID already exists");
            }
    
            // Verify textbook exists
            if (!textbookDAO.findById(textbookId).isPresent()) {
                throw new ServiceException("Textbook not found");
            }
    
            Chapter chapter = new Chapter();
            chapter.setChapterId(chapterId);
            chapter.setTitle(title);
            chapter.setTextbookId(textbookId);
            
            chapterDAO.save(chapter);
            
            // Send notification
            notificationService.sendChapterCreationNotification(createdBy, chapter);
            
            return chapter;
        } catch (Exception e) {
            throw new ServiceException("Error creating chapter: " + e.getMessage(), e);
        }
    }

    public Section createSection(String sectionNumber, String title, String chapterId, User createdBy) {
        try {
            // Validate chapter exists
            Chapter chapter = chapterDAO.findById(chapterId)
                .orElseThrow(() -> new ServiceException("Chapter not found"));
    
            
            // Check if section number already exists in this chapter
            if (sectionDAO.findBySectionNumber(sectionNumber, chapterId).isPresent()) {
                throw new ServiceException("Section number already exists in this chapter");
            }
    
            // Create new section
            Section section = new Section();
            section.setSectionNumber(sectionNumber);
            section.setSectionId(sectionNumber);
            section.setTitle(title);
            section.setTextbookId(chapter.getTextbookId());
            section.setChapterId(chapterId);
    
            // Save section
            sectionDAO.save(section);
    
            // Create version record
            // CourseContentVersioning versioning = new CourseContentVersioning();
            // versioning.setChapterId(chapterId);
            // versioning.setSectionId(section.getSectionId());
            
            // // versioning.setDisplayOrder(calculateDisplayOrder(chapterId));
            // int nextDisplayOrder = getNextDisplayOrder(chapterId); // Get next display order
            // versioning.setDisplayOrder(nextDisplayOrder); // Set the display order
            // versioningDAO.save(versioning);
    
            // Send notification
            notificationService.sendSectionCreationNotification(createdBy, section);
    
            return section;
        } catch (Exception e) {
            throw new ServiceException("Error creating section: " + e.getMessage(), e);
        }
    }

}