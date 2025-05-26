package com.ant.utils

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

/**
 * Utility for displaying notifications in a standardized way
 */
object NotificationUtil {
    
    /**
     * Shows an information notification
     */
    fun showInfo(project: Project, message: String) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Translation Plugin Notifications")
            .createNotification(message, NotificationType.INFORMATION)
            .notify(project)
    }
    
    /**
     * Shows a warning notification
     */
    fun showWarning(project: Project, message: String) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Translation Plugin Notifications")
            .createNotification(message, NotificationType.WARNING)
            .notify(project)
    }
    
    /**
     * Shows an error notification
     */
    fun showError(project: Project, message: String) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Translation Plugin Notifications")
            .createNotification(message, NotificationType.ERROR)
            .notify(project)
    }
} 