package com.etextbook.service;

import com.etextbook.dao.CourseCustomizationDAO;
import com.etextbook.dao.CourseContentVersioningDAO;
import com.etextbook.model.*;
import com.etextbook.service.exception.ServiceException;
import java.util.List;
// import java.util.Optional;

public class CustomizationService {
    private final CourseCustomizationDAO customizationDAO;
    private final CourseContentVersioningDAO versioningDAO;
    private final NotificationService notificationService;

    public CustomizationService(CourseCustomizationDAO customizationDAO,
                              CourseContentVersioningDAO versioningDAO,
                              NotificationService notificationService) {
        this.customizationDAO = customizationDAO;
        this.versioningDAO = versioningDAO;
        this.notificationService = notificationService;
    }

    // Content Customization Methods
    public CourseCustomization createCustomization(CourseCustomization customization, User creator) {
        validateCustomizationPermissions(creator, customization.getCourseId());
        validateCustomizationData(customization);

        try {
            // Set creator details
            customization.setAddedByRole(creator.getRole());
            customization.setCreatedByUserId(creator.getUserId());
            customization.setIsOriginalContent(false);

            // Set display order
            int newOrder = getNextDisplayOrder(customization.getCourseId());
            customization.setDisplayOrder(newOrder);

            customizationDAO.save(customization);

            // Notify course faculty about new customization
            notifyCustomizationCreated(customization, creator);

            return customization;
        } catch (Exception e) {
            throw new ServiceException("Error creating course customization", e);
        }
    }

    public void toggleContentVisibility(Integer customizationId, User modifier) {
        CourseCustomization customization = customizationDAO.findById(customizationId)
            .orElseThrow(() -> new ServiceException("Customization not found"));

        validateCustomizationPermissions(modifier, customization.getCourseId());
        
        customizationDAO.toggleVisibility(customizationId);
        notifyContentVisibilityChanged(customization, modifier);
    }

    public void updateDisplayOrder(String courseId, List<CourseCustomization> newOrder, User modifier) {
        validateCustomizationPermissions(modifier, courseId);

        try {
            customizationDAO.reorderContent(courseId, newOrder);
            notifyContentOrderChanged(courseId, modifier);
        } catch (Exception e) {
            throw new ServiceException("Error updating content order", e);
        }
    }

    // Content Versioning Methods
    public void createContentVersion(CourseContentVersioning versioning, User creator) {
        validateVersioningPermissions(creator, versioning.getCourseId());

        try {
            // Set display order
            int newOrder = getNextVersioningOrder(versioning.getCourseId());
            versioning.setDisplayOrder(newOrder);

            versioningDAO.save(versioning);
            notifyVersionCreated(versioning, creator);
        } catch (Exception e) {
            throw new ServiceException("Error creating content version", e);
        }
    }

    public void updateVersionOrder(String courseId, List<CourseContentVersioning> newOrder, User modifier) {
        validateVersioningPermissions(modifier, courseId);

        try {
            versioningDAO.reorderContent(courseId, newOrder);
            notifyVersionOrderChanged(courseId, modifier);
        } catch (Exception e) {
            throw new ServiceException("Error updating version order", e);
        }
    }

    // Retrieval Methods
    public List<CourseCustomization> getCourseCustomizations(String courseId) {
        return customizationDAO.findByCourse(courseId);
    }

    public List<CourseCustomization> getCustomizationsByCreator(String userId) {
        return customizationDAO.findByCreator(userId);
    }

    public List<CourseContentVersioning> getCourseVersioning(String courseId) {
        return versioningDAO.findByCourse(courseId);
    }

    // Validation Methods
    private void validateCustomizationPermissions(User user, String courseId) {
        if (!List.of("Faculty", "TA").contains(user.getRole())) {
            throw new ServiceException("User not authorized to modify course content");
        }
    }

    private void validateVersioningPermissions(User user, String courseId) {
        if (!"Faculty".equals(user.getRole())) {
            throw new ServiceException("Only faculty can modify content versioning");
        }
    }

    private void validateCustomizationData(CourseCustomization customization) {
        if (customization.getCourseId() == null || customization.getCourseId().trim().isEmpty()) {
            throw new ServiceException("Course ID is required");
        }
        if (customization.getContentBlockId() == null && customization.getActivityId() == null) {
            throw new ServiceException("Either content block or activity must be specified");
        }
    }

    // Helper Methods
    private int getNextDisplayOrder(String courseId) {
        List<CourseCustomization> existing = customizationDAO.findByCourse(courseId);
        return existing.isEmpty() ? 1 : existing.size() + 1;
    }

    private int getNextVersioningOrder(String courseId) {
        List<CourseContentVersioning> existing = versioningDAO.findByCourse(courseId);
        return existing.isEmpty() ? 1 : existing.size() + 1;
    }

    // Notification Methods
    private void notifyCustomizationCreated(CourseCustomization customization, User creator) {
        String message = String.format("New content customization added by %s %s (%s)",
            creator.getFirstName(), creator.getLastName(), creator.getRole());
        notificationService.createNotification(customization.getCourseId(), message);
    }

    private void notifyContentVisibilityChanged(CourseCustomization customization, User modifier) {
        String visibility = customization.getIsHidden() ? "hidden" : "visible";
        String message = String.format("Content visibility changed to %s by %s %s",
            visibility, modifier.getFirstName(), modifier.getLastName());
        notificationService.createNotification(customization.getCourseId(), message);
    }

    private void notifyContentOrderChanged(String courseId, User modifier) {
        String message = String.format("Course content order updated by %s %s",
            modifier.getFirstName(), modifier.getLastName());
        notificationService.createNotification(courseId, message);
    }

    private void notifyVersionCreated(CourseContentVersioning versioning, User creator) {
        String message = String.format("New content version created by %s %s",
            creator.getFirstName(), creator.getLastName());
        notificationService.createNotification(versioning.getCourseId(), message);
    }

    private void notifyVersionOrderChanged(String courseId, User modifier) {
        String message = String.format("Content version order updated by %s %s",
            modifier.getFirstName(), modifier.getLastName());
        notificationService.createNotification(courseId, message);
    }
}