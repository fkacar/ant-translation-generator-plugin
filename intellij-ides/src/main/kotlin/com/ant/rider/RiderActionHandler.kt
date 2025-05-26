package com.ant.rider

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.ant.extension.ComplementaryRiderPlugin

/**
 * Base action handler for Rider-specific editor actions.
 * This class provides common functionality for handling text operations in Rider.
 */
abstract class RiderActionHandler : EditorActionHandler() {
    protected val logger = Logger.getInstance(this::class.java)

    /**
     * Executes when an action is performed on an editor
     */
    override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext) {
        val project = editor.project
        if (project == null) {
            logger.warn("Project is null in RiderActionHandler")
            return
        }
        
        // Get selected text
        val selectedText = editor.selectionModel.selectedText
        if (selectedText.isNullOrBlank()) {
            logger.warn("No text selected in RiderActionHandler")
            return
        }
        
        // Execute the Rider-specific action
        executeRiderAction(project, editor, selectedText, dataContext)
    }
    
    /**
     * Executes the Rider-specific action
     * @param project The current project
     * @param editor The current editor
     * @param selectedText The selected text
     * @param dataContext The data context
     */
    abstract fun executeRiderAction(
        project: Project,
        editor: Editor,
        selectedText: String,
        dataContext: DataContext
    )
    
    /**
     * Finds translation files in the current project
     * @param project The current project
     * @return List of translation files
     */
    protected fun findTranslationFiles(project: Project): List<VirtualFile> {
        // Get Rider plugin instance
        val riderPlugin = ComplementaryRiderPlugin.getInstance(project)
        
        // Get translation files from the Rider plugin
        return riderPlugin.getTranslationFiles()
    }
}

/**
 * Action handler to generate translation keys in Rider.
 * This handler is responsible for creating translation entries from selected text.
 */
class GenerateRiderTranslationHandler : RiderActionHandler() {
    override fun executeRiderAction(
        project: Project, 
        editor: Editor, 
        selectedText: String,
        dataContext: DataContext
    ) {
        logger.info("Executing Generate Translation in Rider: '$selectedText'")
        
        // Get translation files
        val translationFiles = findTranslationFiles(project)
        if (translationFiles.isEmpty()) {
            logger.warn("No translation files found in the project")
            return
        }
        
        // Process translation generation
        logger.info("Found ${translationFiles.size} translation files for potential updates")
        
        // Implementation for adding translation keys to files will go here
        // This is a placeholder for now
    }
}

/**
 * Action handler to remove translation keys in Rider.
 * This handler is responsible for removing translation entries from files.
 */
class RemoveRiderTranslationHandler : RiderActionHandler() {
    override fun executeRiderAction(
        project: Project, 
        editor: Editor, 
        selectedText: String,
        dataContext: DataContext
    ) {
        logger.info("Executing Remove Translation in Rider: '$selectedText'")
        
        // Get translation files
        val translationFiles = findTranslationFiles(project)
        if (translationFiles.isEmpty()) {
            logger.warn("No translation files found in the project")
            return
        }
        
        // Process translation removal
        logger.info("Found ${translationFiles.size} translation files for potential updates")
        
        // Implementation for removing translation keys from files will go here
        // This is a placeholder for now
    }
} 