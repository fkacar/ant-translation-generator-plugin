package com.ant.actions

import com.ant.generator.TranslationGenerator
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project

/**
 * Remove translation key action
 */
class RemoveTranslationAction : AbstractRemoveTranslationAction() {
    
    override fun performRemoveAction(project: Project, editor: Editor, selectedText: String) {
        logger.info("Calling TranslationGenerator.removeTranslationKey()")
        TranslationGenerator(project).removeTranslationKey(editor, "RemoveTranslationKey")
    }
} 