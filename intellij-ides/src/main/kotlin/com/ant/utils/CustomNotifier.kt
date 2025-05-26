package com.ant.utils

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

/**
 * Helper class used to show notifications within the plugin
 */
class CustomNotifier {
    /**
     * Shows a successful operation notification
     */
    fun showSuccessNotification(project: Project?, title: String, content: String = "") {
        if (project == null) return
        
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Translation Plugin Notifications")
            .createNotification(title, content, NotificationType.INFORMATION)
            .notify(project)
    }
    
    /**
     * Shows an error notification
     */
    fun showErrorNotification(project: Project?, title: String, content: String = "") {
        if (project == null) return
        
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Translation Plugin Notifications")
            .createNotification(title, content, NotificationType.ERROR)
            .notify(project)
    }
    
    /**
     * Shows a warning notification
     */
    fun showWarningNotification(project: Project?, title: String, content: String = "") {
        if (project == null) return
        
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Translation Plugin Notifications")
            .createNotification(title, content, NotificationType.WARNING)
            .notify(project)
    }
} 