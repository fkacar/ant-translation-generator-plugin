package com.ant

import com.ant.extension.ComplementaryRiderPlugin
import com.ant.model.TranslationFile
import com.ant.utils.Constants
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import java.io.File

object TranslationService {
    private val logger = Logger.getInstance(TranslationService::class.java)

    /**
     * Find translation files in the project
     */
    fun findTranslationFiles(project: Project?): List<File> {
        // Öncelikle ayarlardaki yolları kontrol et
        val settingsFiles = findTranslationFilesFromSettings(project)
        if (settingsFiles.isNotEmpty()) {
            logger.info("Found ${settingsFiles.size} translation files from user settings")
            return settingsFiles
        }

        if (project == null) {
            logger.warn("Project is null, cannot find translation files")
            return emptyList()
        }

        if (project.isDisposed) {
            logger.warn("Project is disposed, cannot find translation files")
            return emptyList()
        }

        val basePath = project.basePath
        if (basePath == null) {
            logger.warn("Project base path is null, cannot find translation files")
            return emptyList()
        }

        // First, try to find translation files in standard locations
        val standardFiles = findStandardTranslationFiles(basePath)
        if (standardFiles.isNotEmpty()) {
            logger.info("Found ${standardFiles.size} translation files in standard locations")
            return standardFiles
        }

        // If no standard files found, try Rider project
        val riderFiles = findRiderTranslationFiles(project)
        if (riderFiles.isNotEmpty()) {
            logger.info("Found ${riderFiles.size} translation files in Rider project")
            return riderFiles
        }

        logger.warn("No translation files found in the project")
        return emptyList()
    }

    /**
     * Find translation files from user settings (TranslationSettings.translationFilePaths)
     */
    private fun findTranslationFilesFromSettings(project: Project?): List<File> {
        if (project == null) return emptyList()
        val settings = com.ant.TranslationSettings.getApplicationInstance()
        val basePath = project.basePath ?: return emptyList()
        return settings.translationFilePaths.mapNotNull { relPath ->
            val file = File(basePath, relPath)
            if (file.exists() && file.isFile) file else null
        }
    }

    /**
     * Find translation files in standard locations (src/main/resources/i18n)
     */
    private fun findStandardTranslationFiles(basePath: String): List<File> {
        // Check if i18n directory exists
        val i18nDir = File(basePath, Constants.I18N_DIRECTORY)
        if (!i18nDir.exists() || !i18nDir.isDirectory) {
            logger.info("i18n directory not found at: ${i18nDir.absolutePath}")
            return emptyList()
        }

        // Find all JSON files in the i18n directory
        val jsonFiles = i18nDir.listFiles { file ->
            file.isFile && file.name.endsWith(".json")
        }

        if (jsonFiles.isNullOrEmpty()) {
            logger.warn("No JSON files found in i18n directory: ${i18nDir.absolutePath}")
            return emptyList()
        }

        logger.info("Found ${jsonFiles.size} translation files in i18n directory")
        return jsonFiles.toList()
    }

    /**
     * Find translation files in Rider project
     */
    private fun findRiderTranslationFiles(project: Project): List<File> {
        try {
            val riderPlugin = ComplementaryRiderPlugin.getInstance(project)
            if (riderPlugin == null) {
                logger.warn("Rider plugin not available, cannot find Rider translation files")
                return emptyList()
            }

            val translationFiles = riderPlugin.findTranslationFiles()
            logger.info("Found ${translationFiles.size} translation files in Rider project")
            return translationFiles.map { it.file }
        } catch (e: Exception) {
            logger.error("Error finding Rider translation files: ${e.message}", e)
            return emptyList()
        }
    }
} 