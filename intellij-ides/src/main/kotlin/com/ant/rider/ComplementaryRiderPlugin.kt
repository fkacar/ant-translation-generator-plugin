package com.ant.extension

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.vfs.VirtualFile
import com.ant.rider.CSharpTranslationUtils

/**
 * A complementary component for Rider integration.
 * This class handles the Rider-specific initialization and functionality.
 */
@Service(Service.Level.PROJECT)
class ComplementaryRiderPlugin(private val project: Project) {
    private val logger = Logger.getInstance(ComplementaryRiderPlugin::class.java)
    private var translationFiles: List<VirtualFile> = emptyList()

    init {
        logger.info("ComplementaryRiderPlugin initialized for project: ${project.name}")
    }

    /**
     * Initializes Rider-specific functionality
     */
    fun initialize() {
        logger.info("Initializing Rider plugin functionality")
        
        // Detect C# translation files
        translationFiles = detectCSharpTranslationFiles()
        
        logger.info("Rider plugin initialization complete. Found ${translationFiles.size} translation files")
    }

    /**
     * Detects C# translation files in the Rider project
     * @return A list of virtual files representing translation files
     */
    fun detectCSharpTranslationFiles(): List<VirtualFile> {
        logger.info("Detecting C# translation files in the project")
        return CSharpTranslationUtils.findTranslationFiles(project)
    }
    
    /**
     * Gets all detected translation files
     */
    fun getTranslationFiles(): List<VirtualFile> = translationFiles
    
    /**
     * Finds translation files for the TranslationService
     */
    fun findTranslationFiles(): List<com.ant.model.TranslationFile> {
        return translationFiles.map { com.ant.model.TranslationFile(it) }
    }

    companion object {
        /**
         * Get the ComplementaryRiderPlugin service instance for the given project
         */
        fun getInstance(project: Project): ComplementaryRiderPlugin =
            project.service<ComplementaryRiderPlugin>()
    }
}

/**
 * Startup activity for Rider integration.
 * This class is responsible for initializing Rider-specific components
 * when a Rider project is opened.
 */
class RiderStartupActivity : StartupActivity {
    private val logger = Logger.getInstance(RiderStartupActivity::class.java)

    override fun runActivity(project: Project) {
        logger.info("RiderStartupActivity running for project: ${project.name}")
        
        try {
            // Initialize the Rider plugin component
            val riderPlugin = ComplementaryRiderPlugin.getInstance(project)
            riderPlugin.initialize()
            
            // Log found translation files
            val translationFiles = riderPlugin.getTranslationFiles()
            if (translationFiles.isNotEmpty()) {
                logger.info("Found ${translationFiles.size} C# translation files:")
                translationFiles.forEachIndexed { index, file ->
                    logger.info("  ${index + 1}. ${file.path}")
                }
            } else {
                logger.info("No C# translation files found in the project")
            }
            
        } catch (e: Exception) {
            logger.error("Error initializing Rider plugin functionality", e)
        }
    }
} 