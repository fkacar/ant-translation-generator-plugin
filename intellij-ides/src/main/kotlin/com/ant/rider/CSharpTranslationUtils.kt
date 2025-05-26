package com.ant.rider

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VfsUtil
import java.io.File

/**
 * Utility class for C# translation file operations in Rider.
 * This class handles the detection and processing of translation files in C# projects.
 */
class CSharpTranslationUtils {
    companion object {
        private val logger = Logger.getInstance(CSharpTranslationUtils::class.java)
        
        /**
         * Common paths where translation files are typically located in C# projects
         */
        private val COMMON_TRANSLATION_PATHS = listOf(
            "Resources/Localization",
            "Resources/Translations",
            "Resources/i18n",
            "Localization",
            "Translations",
            "i18n"
        )
        
        /**
         * Common file extensions for translation files in C# projects
         */
        private val TRANSLATION_FILE_EXTENSIONS = listOf(
            "json",
            "resx",
            "xml"
        )
        
        /**
         * Finds all translation files in the given C# project
         * @param project The Rider project to search in
         * @return A list of virtual files representing translation files
         */
        fun findTranslationFiles(project: Project): List<VirtualFile> {
            logger.info("Searching for translation files in C# project: ${project.name}")
            
            val basePath = project.basePath ?: return emptyList()
            val baseDir = File(basePath)
            
            val result = mutableListOf<VirtualFile>()
            
            // Search in common locations
            for (path in COMMON_TRANSLATION_PATHS) {
                val dir = File(baseDir, path)
                if (dir.exists() && dir.isDirectory) {
                    logger.info("Found translation directory: ${dir.absolutePath}")
                    findTranslationFilesInDirectory(dir, result)
                }
            }
            
            // Search project-wide for translation files (limited to prevent performance issues)
            findTranslationFilesRecursively(baseDir, result, maxDepth = 5)
            
            logger.info("Found ${result.size} translation files in C# project")
            return result
        }
        
        /**
         * Finds translation files in the specified directory
         * @param directory The directory to search in
         * @param result The list to add found files to
         */
        private fun findTranslationFilesInDirectory(directory: File, result: MutableList<VirtualFile>) {
            directory.listFiles()?.forEach { file ->
                if (file.isFile && isTranslationFile(file)) {
                    VfsUtil.findFileByIoFile(file, true)?.let { vFile ->
                        result.add(vFile)
                        logger.info("Found translation file: ${file.absolutePath}")
                    }
                } else if (file.isDirectory) {
                    findTranslationFilesInDirectory(file, result)
                }
            }
        }
        
        /**
         * Recursively finds translation files in the project, with a maximum depth limit
         * to prevent excessive searching
         */
        private fun findTranslationFilesRecursively(
            directory: File, 
            result: MutableList<VirtualFile>,
            currentDepth: Int = 0,
            maxDepth: Int = 5
        ) {
            if (currentDepth > maxDepth) return
            
            directory.listFiles()?.forEach { file ->
                if (file.isFile && isTranslationFile(file)) {
                    VfsUtil.findFileByIoFile(file, true)?.let { vFile ->
                        result.add(vFile)
                        logger.info("Found translation file: ${file.absolutePath}")
                    }
                } else if (file.isDirectory) {
                    // Skip some directories that are unlikely to contain translation files
                    val dirName = file.name.lowercase()
                    if (!dirName.startsWith(".") && 
                        dirName != "bin" && 
                        dirName != "obj" &&
                        dirName != "node_modules") {
                        findTranslationFilesRecursively(file, result, currentDepth + 1, maxDepth)
                    }
                }
            }
        }
        
        /**
         * Determines if a file is likely a translation file based on its extension and name
         */
        private fun isTranslationFile(file: File): Boolean {
            val extension = file.extension?.lowercase() ?: return false
            val name = file.nameWithoutExtension.lowercase()
            
            return TRANSLATION_FILE_EXTENSIONS.contains(extension) &&
                   (name.contains("translation") || 
                    name.contains("localization") || 
                    name.contains("lang") ||
                    name.contains("resource") ||
                    name.startsWith("strings") ||
                    name.matches(Regex(".*[._-]en[._-].*")) ||
                    name.matches(Regex(".*[._-]us[._-].*")) ||
                    name.matches(Regex(".*[._-]de[._-].*")) ||
                    name.matches(Regex(".*[._-]fr[._-].*")) ||
                    name.matches(Regex(".*[._-]es[._-].*")) ||
                    name.matches(Regex(".*[._-]tr[._-].*")))
        }
    }
} 