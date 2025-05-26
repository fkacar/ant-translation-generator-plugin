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
        
        // Disable by default
        e.presentation.isEnabled = false
        
        // Get editor and check if there's text selected
        val editor = e.getData(CommonDataKeys.EDITOR)
        if (editor != null && editor.selectionModel.hasSelection()) {
            val selectedText = editor.selectionModel.selectedText ?: ""
            
            // Enable only if some text is selected and it's not already in translation key format
            val isTranslationKeyFormat = selectedText.matches(Regex("\\w+\\(['\"](.*?)['\"]\\)"))
            e.presentation.isEnabled = !isTranslationKeyFormat
            
            logger.warn("GenerateTranslationAction: Selected text='$selectedText', isTranslationKey=$isTranslationKeyFormat, enabled=${e.presentation.isEnabled}")
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        logger.warn("GenerateTranslationAction.actionPerformed() called")
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        
        TranslationGenerator(project).generateTranslationKey(editor, "GenerateTranslationKey")
    }
} 