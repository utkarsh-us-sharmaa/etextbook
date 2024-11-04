package com.etextbook.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NotificationScheduler {
    private final NotificationService notificationService;
    private final ScheduledExecutorService scheduler;

    public NotificationScheduler(NotificationService notificationService) {
        this.notificationService = notificationService;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void startScheduledTasks() {
        // Schedule cleanup of old notifications (run daily)
        scheduler.scheduleAtFixedRate(
            () -> notificationService.deleteOldNotifications(30), // Keep notifications for 30 days
            1, // Initial delay
            24, // Period
            TimeUnit.HOURS
        );
    }

    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}