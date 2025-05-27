package com.ant.actions

import com.ant.generator.TranslationGenerator
import com.ant.utils.LoggerUtils.getLogger
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.ActionUpdateThread

/**
 * Generate translation key action
 */
class GenerateTranslationAction : AnAction() {
    private val logger = getLogger(javaClass)
    
    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun update(e: AnActionEvent) {
        logger.warn("GenerateTranslationAction.update() called")
        
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
                "Generate Translation Key (already a translation key)"
            } else {
                "Generate Translation Key"
            }
            
            logger.warn("GenerateTranslationAction: Selected text='$selectedText', isTranslationKey=$isTranslationKeyFormat")
        } else {
            e.presentation.text = "Generate Translation Key"
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        logger.warn("GenerateTranslationAction.actionPerformed() called")
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        
        // Check if there's text selected
        if (!editor.selectionModel.hasSelection()) {
            logger.warn("No text selected for generate translation key action")
            return
        }
        
        val selectedText = editor.selectionModel.selectedText ?: ""
        
        // Check if selected text is already in translation key format
        val isTranslationKeyFormat = selectedText.matches(Regex("\\w+\\(['\"](.*?)['\"]\\)"))
        if (isTranslationKeyFormat) {
            logger.warn("Selected text is already in translation key format: $selectedText")
            return
        }
        
        TranslationGenerator(project).generateTranslationKey(editor, "GenerateTranslationKey")
    }
} 