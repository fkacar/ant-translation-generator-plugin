package com.ant

import com.intellij.openapi.options.ConfigurableProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.diagnostic.Logger

class TranslationSettingsConfigurableProvider : ConfigurableProvider() {
    private val logger = Logger.getInstance(TranslationSettingsConfigurableProvider::class.java)
    
    override fun createConfigurable(): com.intellij.openapi.options.Configurable? {
        logger.warn("TranslationSettingsConfigurableProvider.createConfigurable() called")
        return TranslationSettingsConfigurable()
    }
    
    override fun canCreateConfigurable(): Boolean {
        logger.warn("TranslationSettingsConfigurableProvider.canCreateConfigurable() called")
        return true
    }
} 