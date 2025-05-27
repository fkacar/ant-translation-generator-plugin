package com.ant.actions

import com.ant.TranslationSettings
import com.ant.utils.LoggerUtils.getLogger
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project

/**
 * Abstract base class for remove translation actions
 */
abstract class AbstractRemoveTranslationAction : AnAction() {
    protected val logger = getLogger(javaClass)
    
    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun update(e: AnActionEvent) {
        logger.info("${this.javaClass.simpleName}.update() called")
        
        // Always enable the action - let actionPerformed handle the logic
        e.presentation.isEnabled = true
        e.presentation.isVisible = true
        
        // Get editor and check if there's text selected
        val editor = e.getData(CommonDataKeys.EDITOR)
        if (editor != null && editor.selectionModel.hasSelection()) {
            val selectedText = editor.selectionModel.selectedText ?: ""
            val isTranslationKeyFormat = selectedText.matches(Regex("\\w+\\(['\"](.*?)['\"]\\)"))
            
            // Update the action text to be more descriptive
            e.presentation.text = if (isTranslationKeyFormat) {
                "Remove Translation Key"
            } else {
                "Remove Translation Key"
            }
            
            logger.info("${this.javaClass.simpleName}: Selected text='$selectedText', isTranslationKey=$isTranslationKeyFormat")
        } else {
            e.presentation.text = "Remove Translation Key"
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        logger.info("🔥 ${this.javaClass.simpleName}.actionPerformed() TRIGGERED!")
        val project = e.project
        val editor = e.getData(CommonDataKeys.EDITOR)
        
        logger.info("Project: $project, Editor: $editor")
        
        if (project == null) {
            logger.warn("No project available")
            return
        }
        
        if (editor == null) {
            logger.warn("No editor available")
            return
        }
        
        // Check if there's text selected
        if (!editor.selectionModel.hasSelection()) {
            logger.warn("No text selected for remove translation key action")
            return
        }
        
        val selectedText = editor.selectionModel.selectedText ?: ""
        logger.info("Selected text: '$selectedText'")
        
        // Check if selected text is in translation key format
        val isTranslationKeyFormat = selectedText.matches(Regex("\\w+\\(['\"](.*?)['\"]\\)"))
        logger.info("Is translation key format: $isTranslationKeyFormat")
        
        if (!isTranslationKeyFormat) {
            logger.warn("Selected text is not in translation key format: $selectedText")
            return
        }
        
        // Call the abstract method to be implemented by subclasses
        performRemoveAction(project, editor, selectedText)
    }
    
    /**
     * Abstract method to be implemented by subclasses
     */
    protected abstract fun performRemoveAction(project: Project, editor: Editor, selectedText: String)
} 