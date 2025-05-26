package com.ant.startup

import com.ant.TranslationSettings
import com.ant.utils.ShortcutRegistrar
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.Shortcut
import com.intellij.openapi.actionSystem.KeyboardShortcut
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId

/**
 * Activity that runs at application startup
 * Configures shortcuts
 */
class ApplicationStartup : StartupActivity {
    private val logger = logger<ApplicationStartup>()

    override fun runActivity(project: Project) {
        // Log project name
        logger.warn("Translation Plugin is starting: ${project.name}")
        
        try {
            // Run in main thread - to ensure shortcuts are always registered
            ApplicationManager.getApplication().invokeLater {
                try {
                    // Register shortcuts
                    logger.warn("Shortcuts are being registered programmatically...")
                    ShortcutRegistrar.registerShortcuts()
                    
                    // Check plugin and shortcuts
                    checkPluginAndKeyboardShortcuts()
                    
                    logger.warn("Translation Plugin startup operations completed")
                    
                    // Try again if shortcuts were not registered correctly
                    ApplicationManager.getApplication().executeOnPooledThread {
                        try {
                            Thread.sleep(2000) // 2 seconds wait
                            
                            val actionManager = ActionManager.getInstance()
                            val generateShortcuts = actionManager.getAction("com.ant.GenerateTranslationKey")?.shortcutSet?.shortcuts?.size ?: 0
                            val removeShortcuts = actionManager.getAction("com.ant.RemoveTranslationKey")?.shortcutSet?.shortcuts?.size ?: 0
                            
                            if (generateShortcuts == 0 || removeShortcuts == 0) {
                                logger.warn("Shortcuts are missing, registering again...")
                                ApplicationManager.getApplication().invokeLater {
                                    ShortcutRegistrar.registerShortcuts()
                                }
                            }
                        } catch (e: Exception) {
                            logger.warn("Error in shortcut control thread: ${e.message}", e)
                        }
                    }
                } catch (e: Exception) {
                    logger.warn("Error in shortcut registration: ${e.message}", e)
                }
            }
        } catch (e: Exception) {
            logger.warn("Error in Translation Plugin startup: ${e.message}", e)
        }
    }
    
    /**
     * Performs plugin and shortcut checks
     */
    private fun checkPluginAndKeyboardShortcuts() {
        // Check Translation plugin existence
        val pluginIdString = "com.ant.translation"
        val pluginId = PluginId.getId(pluginIdString)
        val plugin = PluginManagerCore.getPlugin(pluginId)
        logger.warn("Plugin check: $pluginIdString ${if (plugin != null) "exists" else "not found"}")
        
        // Check shortcuts via ActionManager
        val actionManager = ActionManager.getInstance()
        
        // Check Translation action shortcuts
        val generateActionId = "com.ant.GenerateTranslationKey"
        val generateAction = actionManager.getAction(generateActionId)
        if (generateAction != null) {
            val shortcuts = generateAction.shortcutSet.shortcuts
            logger.warn("$generateActionId has ${shortcuts.size} shortcut(s) defined")
            
            if (shortcuts.isEmpty()) {
                logger.warn("WARNING: No shortcuts defined for $generateActionId!")
            } else {
                shortcuts.forEach { shortcut: Shortcut ->
                    logger.warn("$generateActionId shortcut: ${shortcutToString(shortcut)}")
                }
            }
        } else {
            logger.warn("$generateActionId action not found!")
        }
        
        // Check Generate Translation action shortcuts
        val removeActionId = "com.ant.RemoveTranslationKey"
        val removeAction = actionManager.getAction(removeActionId)
        if (removeAction != null) {
            val shortcuts = removeAction.shortcutSet.shortcuts
            logger.warn("$removeActionId has ${shortcuts.size} shortcut(s) defined")
            
            if (shortcuts.isEmpty()) {
                logger.warn("WARNING: No shortcuts defined for $removeActionId!")
            } else {
                shortcuts.forEach { shortcut: Shortcut ->
                    logger.warn("$removeActionId shortcut: ${shortcutToString(shortcut)}")
                }
            }
        } else {
            logger.warn("$removeActionId action not found!")
        }
        
        // Check user-defined shortcut settings
        val settings = TranslationSettings.getApplicationInstance()
        logger.warn("User settings - Check Translation shortcut: ${settings.getCheckTranslationShortcut()}")
        logger.warn("User settings - Generate Translation shortcut: ${settings.getGenerateTranslationShortcut()}")
    }
    
    /**
     * Converts a shortcut object to its text representation
     */
    private fun shortcutToString(shortcut: Shortcut): String {
        return when (shortcut) {
            is KeyboardShortcut -> {
                val first = shortcut.firstKeyStroke.toString()
                val second = shortcut.secondKeyStroke?.toString() ?: ""
                if (second.isNotEmpty()) "$first $second" else first
            }
            else -> shortcut.toString()
        }
    }
} 