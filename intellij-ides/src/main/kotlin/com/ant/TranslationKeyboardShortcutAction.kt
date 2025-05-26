package com.ant

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.command.WriteCommandAction
import java.io.File

/**
 * Converts selected text to a translation key and adds it to translation files.
 */
class TranslationKeyboardShortcutAction : AbstractTranslationAction() {
    /**
     * Logs when the action is triggered.
     */
    override fun logActionTriggered() {
        val settings = TranslationSettings.getApplicationInstance()
        logger.warn("Check Translation action triggered. Used shortcut: ${settings.getCheckTranslationShortcut()}")
        
        // Check translation file paths and warn
        if (settings.translationFilePaths.isEmpty()) {
            logger.warn("WARNING: No translation file paths defined! User should add file paths from settings screen.")
        } else {
            logger.warn("Defined translation file paths (${settings.translationFilePaths.size}):")
            settings.translationFilePaths.forEachIndexed { index, path -> 
                logger.warn("  ${index + 1}. $path")
            }
        }
    }

    /**
     * Called when translation is complete. This action replaces the selected text with a translation key
     * and adds the translation entries to JSON files.
     */
    override fun onTranslationComplete(
        project: Project,
        editor: Editor,
        selectedText: String,
        translationKey: String,
        key: String,
        fileUpdates: List<File>
    ) {
        // Log information
        logger.warn("onTranslationComplete called: selectedText='$selectedText', key='$key', translationKey='$translationKey', fileUpdates size=${fileUpdates.size}")
        
        // Create i18n key from file path
        val manualPath = "Components/Pages/Public/Dashboard/DashboardMain.razor"
        val i18nPath = generateI18nKeyFromPath(manualPath)
        val fullI18nKey = generateFullI18nKey(i18nPath, key)
        logger.warn("I18n key created: $i18nPath, Full Key: $fullI18nKey")
        
        // Changes the text in the editor to the i18n function call with translation format
        WriteCommandAction.runWriteCommandAction(project) {
            val newText = "$translationKey('$fullI18nKey')"
            editor.document.replaceString(
                editor.selectionModel.selectionStart,
                editor.selectionModel.selectionEnd,
                newText
            )
            logger.warn("Text in editor changed to i18n format: '$selectedText' -> '$newText'")
        }
        
        // Process translation files (AbstractTranslationAction should have completed this)
        if (fileUpdates.isEmpty()) {
            logger.warn("ERROR: Translation files could not be updated - fileUpdates list is empty")
            showErrorNotification(
                project, 
                "Translation files could not be updated. Please check your settings and try again.\n" +
                "Make sure that translation file paths are correct and accessible."
            )
            return
        }

        // Log information about translation files
        logger.warn("Translation file information:")
        fileUpdates.forEachIndexed { index, file ->
            logger.warn("  ${index + 1}. File: ${file.absolutePath}")
            
            // Check file contents
            try {
                val content = file.readText()
                logger.warn("  ${index + 1}. File content: ${content.take(500)}${if (content.length > 500) "..." else ""}")
            } catch (e: Exception) {
                logger.warn("  ${index + 1}. Could not read file: ${e.message}")
            }
        }

        // Show success notification
        showInfoNotification(
            project,
            "Translation successfully created: $translationKey('$fullI18nKey')\n" +
            "Translation files updated. (${fileUpdates.size} files)"
        )
        
        logger.warn("Translation process completed: ${fileUpdates.size} files updated, i18n key: $fullI18nKey")
    }
    
    /**
     * Creates the first part of the translation key - gets from user settings
     */
    override fun generateTranslationKey(text: String): String {
        val settings = TranslationSettings.getApplicationInstance()
        val fn = settings.translationFunction.takeIf { it.isNotBlank() } ?: "t"
        logger.warn("Translation function: $fn")
        return fn
    }
} 