package com.ant.actions

import com.ant.generator.TranslationGenerator
import com.ant.utils.LoggerUtils.getLogger
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.ActionUpdateThread

/**
 * Remove translation key action
 */
class RemoveTranslationAction : AnAction() {
    private val logger = getLogger(javaClass)
    
    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun update(e: AnActionEvent) {
        logger.warn("RemoveTranslationAction.update() called")
        
        // Disable by default
        e.presentation.isEnabled = false
        
        // Get editor and check if there's text selected
        val editor = e.getData(CommonDataKeys.EDITOR)
        if (editor != null && editor.selectionModel.hasSelection()) {
            val selectedText = editor.selectionModel.selectedText ?: ""
            
            // Enable only if selected text is in translation key format
            val isTranslationKeyFormat = selectedText.matches(Regex("\\w+\\(['\"](.*?)['\"]\\)"))
            e.presentation.isEnabled = isTranslationKeyFormat
            
            logger.warn("RemoveTranslationAction: Selected text='$selectedText', isTranslationKey=$isTranslationKeyFormat, enabled=${e.presentation.isEnabled}")
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        logger.warn("RemoveTranslationAction.actionPerformed() called")
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        
        TranslationGenerator(project).removeTranslationKey(editor, "RemoveTranslationKey")
    }
} 