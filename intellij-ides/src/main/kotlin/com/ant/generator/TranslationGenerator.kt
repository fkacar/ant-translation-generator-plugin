package com.ant.generator

import com.ant.TranslationService
import com.ant.utils.LoggerUtils.getLogger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import java.io.File
import org.json.JSONObject
import org.json.JSONTokener
import org.json.JSONException
import java.nio.file.Paths
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.ant.utils.NotificationUtil
import com.ant.services.OpenAITranslationService
import com.ant.utils.LanguageCodes

/**
 * Generates translation keys and manages translations
 */
class TranslationGenerator(private val project: Project) {
    private val logger = getLogger(javaClass)
    private val translationService = TranslationService

    /**
     * Generates a translation key from the selected text
     */
    fun generateTranslationKey(editor: Editor, actionId: String = "GenerateTranslationKey") {
        try {
            val project = editor.project
            if (project == null || project.isDisposed) {
                logger.warn("Project is null or disposed, cannot generate translation key")
                return
            }
            
            // Get selected text
            val selectionModel = editor.selectionModel
            val selectedText = selectionModel.selectedText
            
            if (selectedText.isNullOrBlank()) {
                logger.warn("No text selected for translation")
                NotificationUtil.showWarning(project, "No text selected for translation")
                return
            }
            
            // Get translation function name from settings
            val settings = com.ant.TranslationSettings.getApplicationInstance()
            val translationFunction = settings.translationFunction
            
            // Convert to camelCase for key generation
            val camelCaseText = toCamelCase(selectedText)
            // Get file path segments for hierarchical key
            val filePath = getCurrentFilePath(editor)
            val pathSegments = createPathSegmentsFromFilePath(filePath)
            
            // Create the full key - joining path segments with dots
            val fullKey = if (pathSegments.isNotEmpty()) {
                pathSegments.joinToString(".") + "." + camelCaseText
            } else {
                camelCaseText
            }
            
            // Find translation files before proceeding
            val translationFiles = translationService.findTranslationFiles(project)
            if (translationFiles.isEmpty()) {
                logger.warn("No translation files found")
                NotificationUtil.showWarning(project, "No translation files found in the project")
                return
            }
            
            // Add translations to all files
            addTranslations(project, translationFiles, fullKey, selectedText)
            
            // Replace selected text with translationFunction('key')
            val document = editor.document
            WriteCommandAction.runWriteCommandAction(project) {
                document.replaceString(
                    selectionModel.selectionStart,
                    selectionModel.selectionEnd,
                    "$translationFunction('$fullKey')"
                )
            }
            
            // Show success notification
            NotificationUtil.showInfo(project, "Translation key generated: $fullKey")
            
        } catch (e: Exception) {
            logger.error("Error generating translation key: ${e.message}", e)
            editor.project?.let { 
                NotificationUtil.showError(it, "Error generating translation key: ${e.message}")
            }
        }
    }

    /**
     * Removes a translation key at cursor position
     */
    fun removeTranslationKey(editor: Editor, actionId: String = "RemoveTranslationKey") {
        logger.warn("Removing translation key")
        
        val selectedText = editor.selectionModel.selectedText
        if (selectedText.isNullOrBlank()) {
            logger.warn("No text selected")
            NotificationUtil.showError(project, "Cannot remove translation key: No text selected")
            return
        }
        
        // Get translation function name from settings for the regex pattern
        val settings = com.ant.TranslationSettings.getApplicationInstance()
        val translationFunction = settings.translationFunction
        
        // Extract key from translation key format functionName('key.path')
        val keyPattern = "\\w+\\(['\"]([^'\"]*)['\"]\\)".toRegex()
        val matchResult = keyPattern.find(selectedText)
        val fullKey = matchResult?.groupValues?.get(1)
        
        if (fullKey.isNullOrBlank()) {
            logger.warn("No translation key found in selected text: $selectedText")
            NotificationUtil.showError(project, "No translation key found in selected text")
            return
        }
        
        logger.warn("Found translation key: $fullKey")
        
        // Find translation files
        val translationFiles = translationService.findTranslationFiles(project)
        if (translationFiles.isNullOrEmpty()) {
            logger.warn("No translation files found")
            NotificationUtil.showError(project, "Translation files not found. Please check your settings.")
            return
        }
        
        // First get the original text before removing from files
        val keySegments = fullKey.split(".")
        val lastSegment = keySegments.lastOrNull() ?: ""
        
        // Try to find the original text from the translation files
        var originalText = getOriginalTextFromTranslationFiles(fullKey, translationFiles)
        
        // Remove from translation files - using the nested path structure
        val updatedFiles = removeNestedTranslationFromFiles(fullKey, translationFiles)
        
        if (updatedFiles.isNotEmpty()) {
            // Replace the translation key with the original text
            WriteCommandAction.runWriteCommandAction(project) {
                editor.document.replaceString(
                    editor.selectionModel.selectionStart,
                    editor.selectionModel.selectionEnd,
                    if (originalText.isNotEmpty()) originalText else lastSegment
                )
                logger.warn("Translation key replaced with: ${if (originalText.isNotEmpty()) originalText else lastSegment}")
            }
            
            NotificationUtil.showInfo(project, "Translation key removed: $fullKey")
        } else {
            NotificationUtil.showWarning(project, "Translation key not found or could not be removed: $fullKey")
        }
    }
    
    /**
     * Get original text from translation files based on key
     */
    private fun getOriginalTextFromTranslationFiles(fullKey: String, files: List<File>): String {
        for (file in files) {
            try {
                if (!file.exists() || file.length() <= 0) continue
                
                val json = JSONObject(JSONTokener(file.reader()))
                
                // Navigate through the nested structure using the full key path
                val keySegments = fullKey.split(".")
                var currentObj: Any? = json
                
                for (segment in keySegments) {
                    if (currentObj is JSONObject && currentObj.has(segment)) {
                        currentObj = currentObj.get(segment)
                    } else {
                        currentObj = null
                        break
                    }
                }
                
                if (currentObj is String) {
                    return currentObj
                }
            } catch (e: Exception) {
                logger.warn("Error reading file ${file.absolutePath}: ${e.message}")
            }
        }
        return ""
    }
    
    /**
     * Recursively find a value in nested JSON
     */
    private fun findValueInNestedJson(json: JSONObject, key: String): Any? {
        if (json.has(key)) {
            return json.get(key)
        }
        
        // Search in nested objects
        json.keys().forEach { jsonKey ->
            val value = json.get(jsonKey)
            if (value is JSONObject) {
                val nestedResult = findValueInNestedJson(value, key)
                if (nestedResult != null) {
                    return nestedResult
                }
            }
        }
        
        return null
    }
    
    /**
     * Converts text to camelCase for use in translation keys
     */
    private fun toCamelCase(text: String): String {
        if (text.isBlank()) return "emptyText"
        val cleanText = text.replace(Regex("[^\\p{L}\\p{Nd}\\s]"), "").trim().take(30)
        val words = cleanText.split(Regex("\\s+"))
        return words.mapIndexed { i, w ->
            val lower = w.lowercase()
            if (i == 0) lower else lower.replaceFirstChar { it.uppercase() }
        }.joinToString("")
    }
    
    /**
     * Capitalize the first letter of a string
     */
    private fun String.capitalizeFirst(): String {
        if (isEmpty()) return this
        return this[0].uppercase() + substring(1).lowercase()
    }
    
    /**
     * Creates path segments for i18n from file path
     * Converts the file path to a list of camelCase segments for translation key generation.
     * Only relevant folders and the file name are included.
     */
    private fun createPathSegmentsFromFilePath(filePath: String): List<String> {
        if (filePath.isBlank()) return emptyList()

        // Find project base path
        val projectBasePath = project.basePath ?: ""
        var relativePath = filePath
        if (filePath.startsWith(projectBasePath)) {
            relativePath = filePath.removePrefix(projectBasePath).trimStart('/', '\\')
        }

        // Normalize Windows path
        val normalizedPath = relativePath.replace('\\', '/')

        // Only include relevant segments
        val relevantPathSegments = listOf("components", "pages", "views", "containers", "layouts", "sections")
        val segments = normalizedPath.split('/')
            .filter { segment ->
                relevantPathSegments.any { segment.equals(it, ignoreCase = true) } ||
                (segment.isNotBlank() && !segment.contains('.'))
            }
            .toMutableList()

        // Add the file name (without extension, camelCase)
        val fileName = normalizedPath.substringAfterLast('/')
        val fileNameWithoutExtension = fileName.substringBeforeLast('.', fileName)
        if (fileNameWithoutExtension.isNotBlank() && !segments.contains(fileNameWithoutExtension)) {
            // Split the filename into words if it contains PascalCase or camelCase
            // Example: "DashboardMain" -> "Dashboard Main"
            val words = fileNameWithoutExtension.replace(Regex("([a-z])([A-Z])"), "$1 $2").split(" ")
            
            // Add the processed file name
            val camelCaseFileName = words.joinToString("") { 
                if (words.indexOf(it) == 0) it.lowercase() else it.replaceFirstChar { c -> c.uppercase() }
            }
            segments.add(camelCaseFileName)
        }

        // Convert directory segments to camelCase
        return segments.distinct().map { segment ->
            if (segment == segments.lastOrNull()) {
                // Already processed file name
                segment
            } else {
                // Regular path segments processing
                val parts = segment.split(Regex("[-_]"))
                parts.first().lowercase() + parts.drop(1).joinToString("") { 
                    it.replaceFirstChar { c -> c.uppercase() } 
                }
            }
        }
    }
    
    /**
     * Adds translations to all translation files with auto-translate support
     */
    private fun addTranslations(
        project: Project,
        translationFiles: List<File>,
        key: String,
        text: String
    ) {
        if (translationFiles.isEmpty()) {
            logger.warn("No translation files to update")
            return
        }
        
        val settings = com.ant.TranslationSettings.getApplicationInstance()
        var updatedCount = 0
        
        try {
            // Check if auto translate is enabled
            if (settings.autoTranslateEnabled && settings.openAiApiKey.isNotBlank() && settings.sourceLanguageFile.isNotBlank()) {
                logger.info("Auto translate enabled, processing translations...")
                addTranslationsWithAutoTranslate(project, translationFiles, key, text, settings)
            } else {
                // Original behavior - add same text to all files
                addTranslationsOriginal(project, translationFiles, key, text)
            }
            
        } catch (e: Exception) {
            logger.error("Error adding translations: ${e.message}", e)
            NotificationUtil.showError(project, "Error adding translations: ${e.message}")
        }
    }
    
    /**
     * Add translations with auto-translate feature
     */
    private fun addTranslationsWithAutoTranslate(
        project: Project,
        translationFiles: List<File>,
        key: String,
        text: String,
        settings: com.ant.TranslationSettings
    ) {
        val openAiService = OpenAITranslationService(settings.openAiApiKey)
        var updatedCount = 0
        
        // Find source language from settings
        val sourceLanguageCode = settings.languageFileLanguages[settings.sourceLanguageFile]
        val sourceLanguageName = if (sourceLanguageCode != null) {
            LanguageCodes.getLanguageName(sourceLanguageCode)
        } else {
            "Turkish" // Default fallback
        }
        
        logger.info("Source language: $sourceLanguageName (${sourceLanguageCode ?: "tr"})")
        
        for (file in translationFiles) {
            if (!file.exists()) {
                logger.warn("Translation file does not exist: ${file.absolutePath}")
                continue
            }
            
            // Parse existing JSON
            val json = if (file.length() > 0) {
                try {
                    val content = FileUtil.loadFile(file)
                    JsonParser.parseString(content).asJsonObject
                } catch (e: Exception) {
                    logger.warn("Failed to parse JSON file ${file.name}: ${e.message}")
                    JsonObject()
                }
            } else {
                JsonObject()
            }
            
            // Split key by dots to create nested objects
            val keyParts = key.split(".")
            var currentObj = json
            
            // Create nested objects for each key part except the last
            for (i in 0 until keyParts.size - 1) {
                val part = keyParts[i]
                if (!currentObj.has(part) || !currentObj.get(part).isJsonObject) {
                    currentObj.add(part, JsonObject())
                }
                currentObj = currentObj.getAsJsonObject(part)
            }
            
            val lastKey = keyParts.last()
            val filePath = file.absolutePath
            
            // Determine the text to add
            val textToAdd = if (filePath.endsWith(settings.sourceLanguageFile)) {
                // Source file - use original text
                text
            } else {
                // Target file - translate if language is configured
                val targetLanguageCode = settings.languageFileLanguages.entries
                    .find { filePath.endsWith(it.key) }?.value
                
                if (targetLanguageCode != null) {
                    val targetLanguageName = LanguageCodes.getLanguageName(targetLanguageCode)
                    logger.info("Translating '$text' from $sourceLanguageName to $targetLanguageName")
                    
                    val translatedText = openAiService.translateText(text, sourceLanguageName, targetLanguageName)
                    if (translatedText != null) {
                        logger.info("Translation successful: '$text' -> '$translatedText'")
                        translatedText
                    } else {
                        logger.warn("Translation failed, using original text")
                        text
                    }
                } else {
                    logger.warn("No language configured for file: ${file.name}, using original text")
                    text
                }
            }
            
            // Add the translation
            currentObj.addProperty(lastKey, textToAdd)
            
            // Write back to file
            try {
                val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
                val prettyJson = gson.toJson(json)
                FileUtil.writeToFile(file, prettyJson)
                updatedCount++
                logger.info("Updated translation file: ${file.name} with text: '$textToAdd'")
            } catch (e: Exception) {
                logger.error("Failed to write to translation file ${file.name}: ${e.message}", e)
            }
        }
        
        logger.info("Updated $updatedCount translation files with auto-translate")
        NotificationUtil.showInfo(project, "Updated $updatedCount translation files with auto-translate")
    }
    
    /**
     * Original translation behavior - add same text to all files
     */
    private fun addTranslationsOriginal(
        project: Project,
        translationFiles: List<File>,
        key: String,
        text: String
    ) {
        var updatedCount = 0
        
        for (file in translationFiles) {
            if (!file.exists()) {
                logger.warn("Translation file does not exist: ${file.absolutePath}")
                continue
            }
            
            // Parse existing JSON
            val json = if (file.length() > 0) {
                try {
                    val content = FileUtil.loadFile(file)
                    JsonParser.parseString(content).asJsonObject
                } catch (e: Exception) {
                    logger.warn("Failed to parse JSON file ${file.name}: ${e.message}")
                    JsonObject()
                }
            } else {
                JsonObject()
            }
            
            // Split key by dots to create nested objects
            val keyParts = key.split(".")
            var currentObj = json
            
            // Create nested objects for each key part except the last
            for (i in 0 until keyParts.size - 1) {
                val part = keyParts[i]
                if (!currentObj.has(part) || !currentObj.get(part).isJsonObject) {
                    currentObj.add(part, JsonObject())
                }
                currentObj = currentObj.getAsJsonObject(part)
            }
            
            // Add the actual translation using the last key part
            val lastKey = keyParts.last()
            currentObj.addProperty(lastKey, text)
            
            // Write back to file with pretty printing
            try {
                val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
                val prettyJson = gson.toJson(json)
                FileUtil.writeToFile(file, prettyJson)
                updatedCount++
                logger.info("Updated translation file: ${file.name}")
            } catch (e: Exception) {
                logger.error("Failed to write to translation file ${file.name}: ${e.message}", e)
            }
        }
        
        logger.info("Updated $updatedCount translation files with key: $key")
        NotificationUtil.showInfo(project, "Updated $updatedCount translation files")
    }
    
    /**
     * Removes a nested translation from all translation files
     */
    private fun removeNestedTranslationFromFiles(fullKey: String, files: List<File>): List<File> {
        val updatedFiles = mutableListOf<File>()
        val keySegments = fullKey.split(".")
        
        for (file in files) {
            try {
                if (!file.exists() || file.length() <= 0) {
                    continue
                }
                
                // Read and parse with Gson for better formatting
                val content = FileUtil.loadFile(file)
                val json = JsonParser.parseString(content).asJsonObject
                
                // Use helper function to remove nested key
                if (removeNestedKeyFromGson(json, keySegments)) {
                    // Write back with pretty printing using Gson
                    val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
                    val prettyJson = gson.toJson(json)
                    
                    FileUtil.writeToFile(file, prettyJson)
                    updatedFiles.add(file)
                    logger.warn("Removed translation from file: ${file.absolutePath}")
                }
            } catch (e: Exception) {
                logger.warn("Error removing translation from file ${file.absolutePath}: ${e.message}")
            }
        }
        
        return updatedFiles
    }
    
    /**
     * Recursively removes a key from nested JSON structure (JSONObject version)
     */
    private fun removeNestedKey(json: JSONObject, keySegments: List<String>): Boolean {
        if (keySegments.isEmpty()) return false
        
        if (keySegments.size == 1) {
            // Last segment - remove the key if it exists
            val key = keySegments[0]
            if (json.has(key)) {
                json.remove(key)
                return true
            }
            return false
        }
        
        // Handle nested segments
        val segment = keySegments[0]
        val remainingSegments = keySegments.drop(1)
        
        if (json.has(segment) && json.get(segment) is JSONObject) {
            val nested = json.getJSONObject(segment)
            return removeNestedKey(nested, remainingSegments)
        }
        
        return false
    }
    
    /**
     * Recursively removes a key from nested JSON structure (Gson version)
     */
    private fun removeNestedKeyFromGson(json: JsonObject, keySegments: List<String>): Boolean {
        if (keySegments.isEmpty()) return false
        
        if (keySegments.size == 1) {
            // Last segment - remove the key if it exists
            val key = keySegments[0]
            if (json.has(key)) {
                json.remove(key)
                return true
            }
            return false
        }
        
        // Handle nested segments
        val segment = keySegments[0]
        val remainingSegments = keySegments.drop(1)
        
        if (json.has(segment) && json.get(segment).isJsonObject) {
            val nested = json.getAsJsonObject(segment)
            return removeNestedKeyFromGson(nested, remainingSegments)
        }
        
        return false
    }

    /**
     * Get the current file path from the editor
     */
    private fun getCurrentFilePath(editor: Editor): String {
        val file = FileDocumentManager.getInstance().getFile(editor.document)
        return file?.path ?: ""
    }
} 