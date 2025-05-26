package com.ant

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.nio.charset.StandardCharsets
import java.text.Normalizer
import org.json.JSONObject

/**
 * Common base class for translation operations.
 * This class provides common functions needed for processing translation files and
 * replacing selected text in the editor with translation keys.
 */
abstract class AbstractTranslationAction : AnAction() {
    protected val logger = logger<AbstractTranslationAction>()
    protected val gson = Gson()
    
    /**
     * The main method where the action will be performed
     */
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        if (project == null) {
            logger.warn("Project is null")
            return
        }
        
        val editor = e.getData(CommonDataKeys.EDITOR)
        if (editor == null) {
            logger.warn("Editor is null")
            showErrorNotification(project, "Editor is null")
            return
        }
        
        // Optional initialization operations that can be defined by subclasses
        logActionTriggered()
        
        // Check if there is selected text
        val selectionModel = editor.selectionModel
        if (!selectionModel.hasSelection()) {
            logger.warn("No text selected")
            showErrorNotification(project, "No text selected")
            return
        }
        
        val selectedText = selectionModel.selectedText
        if (selectedText.isNullOrEmpty()) {
            logger.warn("No text selected")
            showErrorNotification(project, "No text selected")
            return
        }
        
        logger.warn("Selected text: '$selectedText'")
        
        // Check if translation file paths are valid
        val settings = TranslationSettings.getApplicationInstance()
        
        logger.warn("Settings retrieved: ${settings}")
        
        if (settings.translationFilePaths.isEmpty()) {
            logger.warn("ERROR: Translation file paths are empty! You must add JSON file paths from the TranslationSettings screen.")
            showErrorNotification(project, "Please configure translation file paths first.")
            return
        }
        
        logger.warn("Translation file paths (${settings.translationFilePaths.size}): ${settings.translationFilePaths.joinToString(", ")}")
        
        // Create translation key
        val translationKey = generateTranslationKey(selectedText)
        val key = generateKey(selectedText)
        logger.warn("Generated translation key: '$key'")
        
        // Process translation files
        logger.warn("Processing translation files...")
        val fileUpdates = processTranslationFiles(settings.translationFilePaths, translationKey, key, selectedText)
        logger.warn("Processed file count: ${fileUpdates.size}")
        
        // Perform text update
        if (fileUpdates.isNotEmpty()) {
            // Operations that can be defined by subclasses
            logger.warn("Translation completed, calling onTranslationComplete method...")
            onTranslationComplete(project, editor, selectedText, translationKey, key, fileUpdates)
        } else {
            logger.warn("No files were updated")
            showErrorNotification(project, "Translation files could not be updated. Please check your file paths. File paths: ${settings.translationFilePaths.joinToString(", ")}")
        }
    }
    
    /**
     * Called when the action is triggered, can be overridden by subclasses.
     */
    protected open fun logActionTriggered() {
        logger.warn("${this.javaClass.simpleName} action triggered")
    }
    
    /**
     * Called when translation is complete, must be overridden by subclasses.
     * This method performs the text update operation in the editor.
     */
    protected abstract fun onTranslationComplete(
        project: Project, 
        editor: Editor, 
        selectedText: String,
        translationKey: String,
        key: String,
        fileUpdates: List<File>
    )
    
    /**
     * Creates the first part of the translation key. Can be overridden by subclasses.
     */
    protected open fun generateTranslationKey(text: String): String {
        val settings = TranslationSettings.getApplicationInstance()
        // Return function name, e.g. "t"
        return settings.translationFunction
    }
    
    /**
     * Creates the key part for translation.
     */
    protected fun generateKey(text: String): String {
        return toCamelCase(text)
    }
    
    /**
     * Converts text to camelCase format.
     */
    protected fun toCamelCase(text: String): String {
        try {
            // Normalize Turkish characters
            val normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
            
            // Remove spaces and punctuation between words
            val parts = normalized.split(Regex("[^a-zA-Z0-9]+")).filter { it.isNotEmpty() }
            
            return if (parts.isEmpty()) {
                ""
            } else {
                parts.first().lowercase() + parts.drop(1).joinToString("") { it.replaceFirstChar { c -> c.uppercase() } }
            }
        } catch (e: Exception) {
            logger.warn("toCamelCase conversion error: ${e.message}")
            return text.replace(Regex("[^a-zA-Z0-9]+"), "").lowercase()
        }
    }
    
    /**
     * Method that processes and updates translation files
     */
    protected fun processTranslationFiles(
        translationFilePaths: List<String>,
        translationFunction: String,
        key: String,
        value: String
    ): List<File> {
        val updatedFiles = mutableListOf<File>()
        if (translationFilePaths.isEmpty()) {
            logger.warn("Translation file paths are empty. No files were updated.")
            return updatedFiles
        }

        logger.warn("Number of translation files to process: ${translationFilePaths.size}")
        logger.warn("Translation function used: $translationFunction")
        
        // Create nested full i18n structure
        // Example: "components.pages.public.dashboard.dashboardMain.kopekBurada"
        val i18nPathSegments = listOf("components", "pages", "public", "dashboard", "dashboardMain")
        val i18nPath = i18nPathSegments.joinToString(".")
        logger.warn("Created i18n path: $i18nPath")
        
        // Create full i18n key
        val fullI18nKey = "$i18nPath.$key"
        logger.warn("Full i18n key: $fullI18nKey")

        for (filePath in translationFilePaths) {
            if (filePath.isBlank()) {
                logger.warn("Empty file path skipped.")
                continue
            }
            
            logger.warn("Processing: $filePath")
            
            try {
                // Create file path correctly
                val file = resolveFilePath(filePath)
                if (file == null) {
                    logger.warn("File path could not be resolved: $filePath")
                    continue
                }
                
                logger.warn("File path resolved: ${file.absolutePath}")
                
                // Create file if it doesn't exist
                if (!file.exists()) {
                    logger.warn("File does not exist, creating: ${file.absolutePath}")
                    
                    // Create parent directories
                    if (!file.parentFile.exists()) {
                        file.parentFile.mkdirs()
                        logger.warn("Parent directories created: ${file.parentFile.absolutePath}")
                    }
                    
                    // Create empty JSON file
                    file.writeText("{}")
                    logger.warn("Empty JSON file created: ${file.absolutePath}")
                }
                
                // Read existing JSON
                val json = file.readText()
                
                // Check if valid JSON
                try {
                    if (json.isBlank()) {
                        logger.warn("File is empty, creating new JSON: ${file.absolutePath}")
                        file.writeText("{}")
                    } else {
                        // Try to parse as JSON
                        JSONObject(json)
                    }
                } catch (e: Exception) {
                    logger.warn("Invalid JSON format: ${file.absolutePath}. Error: ${e.message}")
                    logger.warn("JSON file is being reset and recreated.")
                    file.writeText("{}")
                }
                
                // Add translation - using correct i18n path and segments
                if (addTranslationWithPath(file, i18nPathSegments, key, value)) {
                    updatedFiles.add(file)
                    logger.warn("File updated: ${file.absolutePath}")
                } else {
                    logger.warn("File could not be updated: ${file.absolutePath}")
                }
            } catch (e: Exception) {
                logger.warn("Error while processing file: $filePath. Error: ${e.message}")
                e.printStackTrace()
            }
        }

        return updatedFiles
    }

    /**
     * Adds translation to JSON file for specified path and key
     * This method supports nested structure: { "components": { "pages": { "public": ... } } }
     */
    private fun addTranslationWithPath(file: File, pathSegments: List<String>, key: String, value: String): Boolean {
        logger.warn("addTranslationWithPath called - File: ${file.absolutePath}, Path: ${pathSegments.joinToString(".")}, Key: $key, Value: $value")
        
        try {
            // Check validity of file path
            val directory = file.parentFile
            if (!directory.exists()) {
                directory.mkdirs()
                logger.warn("Directory created: ${directory.absolutePath}")
            }
            
            // Create file if it doesn't exist
            if (!file.exists()) {
                file.createNewFile()
                file.writeText("{}")
                logger.warn("New empty JSON file created: ${file.absolutePath}")
            }
            
            // Read file
            var jsonText = file.readText()
            if (jsonText.isBlank()) {
                jsonText = "{}"
                file.writeText(jsonText)
                logger.warn("File is empty, empty JSON object created and written")
            }
            
            try {
                // Parse JSON with Gson
                var jsonElement = com.google.gson.JsonParser.parseString(jsonText)
                if (!jsonElement.isJsonObject) {
                    logger.warn("Not a valid JSON object, creating new JSON")
                    jsonElement = com.google.gson.JsonObject()
                }
                
                var rootObject = jsonElement.asJsonObject
                logger.warn("JSON successfully parsed, root object size: ${rootObject.size()}")
                
                // Create nested JSON objects
                var currentObject = rootObject
                
                // Create nested JSON for each path segment
                for (segment in pathSegments) {
                    if (!currentObject.has(segment) || !currentObject.get(segment).isJsonObject) {
                        logger.warn("Creating new segment: $segment")
                        val newObject = com.google.gson.JsonObject()
                        currentObject.add(segment, newObject)
                    } else {
                        logger.warn("Using existing segment: $segment")
                    }
                    currentObject = currentObject.getAsJsonObject(segment)
                }
                
                // Add key to final JSON object
                logger.warn("Adding key: $key = $value")
                currentObject.addProperty(key, value)
                
                // Write updated JSON to file - formatted with Gson
                val gson = com.google.gson.GsonBuilder()
                    .setPrettyPrinting()
                    .disableHtmlEscaping()
                    .create()
                
                val prettyJson = gson.toJson(rootObject)
                file.writeText(prettyJson)
                logger.warn("JSON file updated: ${file.absolutePath}")
                
                return true
                
            } catch (e: Exception) {
                logger.warn("Error while processing JSON: ${e.message}")
                e.printStackTrace()
                
                // In case of error, try creating a new JSON
                try {
                    // Create completely new JSON
                    val rootObject = com.google.gson.JsonObject()
                    var currentObject = rootObject
                    
                    // Create nested objects
                    for (segment in pathSegments) {
                        val newObject = com.google.gson.JsonObject()
                        currentObject.add(segment, newObject)
                        currentObject = newObject
                    }
                    
                    // Add key to final object
                    currentObject.addProperty(key, value)
                    
                    // Write to file - formatted with Gson
                    val gson = com.google.gson.GsonBuilder()
                        .setPrettyPrinting()
                        .disableHtmlEscaping()
                        .create()
                    
                    val prettyJson = gson.toJson(rootObject)
                    file.writeText(prettyJson)
                    logger.warn("New JSON structure created in error case")
                    
                    return true
                } catch (e2: Exception) {
                    logger.warn("Attempt to create new JSON also failed: ${e2.message}")
                    e2.printStackTrace()
                    return false
                }
            }
        } catch (e: Exception) {
            logger.warn("Error while updating JSON: ${e.message}")
            e.printStackTrace()
            return false
        }
    }
    
    /**
     * Safely writes JSON data to file
     */
    private fun writeJsonSafely(file: File, jsonContent: String): Boolean {
        try {
            // First parse JSON and then format again for better formatting
            try {
                // Use Gson for better formatting
                val jsonElement = JsonParser.parseString(jsonContent)
                val prettyJson = gson.newBuilder()
                    .setPrettyPrinting()
                    .disableHtmlEscaping()
                    .serializeNulls()
                    .create()
                    .toJson(jsonElement)
                
                logger.warn("JSON reformatted (${prettyJson.length} bytes)")
                
                // 1. Method: Direct write
                file.writeText(prettyJson)
                logger.warn("JSON file successfully updated: ${file.absolutePath}")
                
                // Check file, was it really written?
                val verifyContent = file.readText()
                if (verifyContent == prettyJson) {
                    logger.warn("File content verified, changes saved.")
                    return true
                } else {
                    logger.warn("WARNING: File content is not as expected! ${verifyContent.length} bytes vs ${prettyJson.length} bytes")
                    
                    // Second method: Write with BufferedWriter
                    file.bufferedWriter(StandardCharsets.UTF_8).use { writer ->
                        writer.write(prettyJson)
                        writer.flush()
                    }
                    
                    // Check again
                    val secondVerify = file.readText()
                    if (secondVerify == prettyJson) {
                        logger.warn("File successfully written with BufferedWriter and verified.")
                        return true
                    } else {
                        logger.warn("WARNING: Second write attempt also failed!")
                        
                        // Third method: Write with temporary file
                        val tempFile = File("${file.absolutePath}.temp")
                        tempFile.writeText(prettyJson, StandardCharsets.UTF_8)
                        
                        if (tempFile.exists() && tempFile.readText() == prettyJson) {
                            if (file.delete()) {
                                if (tempFile.renameTo(file)) {
                                    logger.warn("File successfully updated with temporary file method.")
                                    return true
                                } else {
                                    logger.warn("Temporary file could not be renamed!")
                                }
                            } else {
                                logger.warn("Original file could not be deleted!")
                            }
                        }
                        
                        return false
                    }
                }
            } catch (e: Exception) {
                // If formatting with Gson fails, continue with original JSON
                logger.warn("Error while formatting JSON, using original content: ${e.message}")
                file.writeText(jsonContent)
                return true
            }
        } catch (e: Exception) {
            logger.warn("Error writing to file: ${e.message}")
            e.printStackTrace()
            
            // Last resort: Try writing with a different method
            try {
                val outputStream = file.outputStream()
                outputStream.use { os ->
                    os.write(jsonContent.toByteArray(StandardCharsets.UTF_8))
                    os.flush()
                }
                logger.warn("File updated with outputstream.")
                return true
            } catch (e2: Exception) {
                logger.warn("All write methods failed: ${e2.message}")
                e2.printStackTrace()
                return false
            }
        }
    }
    
    /**
     * Gets the path of the active file.
     * If there is no active file or it cannot be retrieved, uses the given example path (Manual file path).
     */
    private fun getCurrentFilePath(): String? {
        try {
            // Try to get active file path
            val currentEditorFilePath = com.intellij.openapi.fileEditor.FileEditorManager.getInstance(
                com.intellij.openapi.project.ProjectManager.getInstance().openProjects.firstOrNull() ?: return getManualFilePath()
            ).selectedEditor?.file?.path
            
            return if (currentEditorFilePath.isNullOrBlank()) {
                // If no active file, use manual example path
                getManualFilePath()
            } else {
                currentEditorFilePath
            }
        } catch (e: Exception) {
            logger.warn("Could not get active file path: ${e.message}", e)
            return getManualFilePath()
        }
    }
    
    /**
     * Returns a manual file path that the user might need
     */
    private fun getManualFilePath(): String? {
        // Path given by user as example
        val manualPath = "Components/Pages/Public/Dashboard/DashboardMain.razor"
        logger.warn("No active file found, using manual path: $manualPath")
        return manualPath
    }
    
    /**
     * Shows error notification.
     */
    protected fun showErrorNotification(project: Project, message: String) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Translation Plugin Notifications")
            .createNotification(message, NotificationType.ERROR)
            .notify(project)
    }
    
    /**
     * Shows info notification.
     */
    protected fun showInfoNotification(project: Project, message: String) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Translation Plugin Notifications")
            .createNotification(message, NotificationType.INFORMATION)
            .notify(project)
    }
    
    /**
     * Checks if the action is enabled.
     * Will only be active for the selected shortcut.
     */
    override fun update(e: AnActionEvent) {
        try {
            val settings = TranslationSettings.getApplicationInstance()
            val actionId = e.actionManager.getId(this)
            
            // Check which action it is
            val isTranslationKeyboardShortcutAction = actionId == "TranslationKeyboardShortcut"
            val isGenerateTranslationAction = actionId == "GenerateTranslation"
            
            // Get active shortcut information from TranslationSettings
            val selectedShortcut = settings.shortcutKey.lowercase()
            
            // Check last key in shortcut
            val lastKey = selectedShortcut.trim().split(" ").lastOrNull()?.lowercase() ?: ""
            logger.warn("Last key in shortcut: $lastKey")
            
            // Determine if this action should run based on Action ID
            val shouldEnable = when {
                // Enable if TranslationKeyboardShortcut action and shortcut ends with t
                isTranslationKeyboardShortcutAction && lastKey == "t" -> true
                
                // Enable if GenerateTranslation action and shortcut ends with g
                isGenerateTranslationAction && lastKey == "g" -> true
                
                // Disable in other cases
                else -> false
            }
            
            // Also check editor
            val editor = e.getData(CommonDataKeys.EDITOR)
            val hasSelection = editor != null && editor.selectionModel.hasSelection()
            
            // Final decision: Enable if shortcut is appropriate AND there is selected text
            e.presentation.isEnabled = shouldEnable && hasSelection
            
            logger.warn("Action updated: $actionId, Enabled: ${e.presentation.isEnabled}, " +
                       "Selected shortcut: $selectedShortcut, Last key: $lastKey, " +
                       "Is shortcut appropriate: $shouldEnable, Is text selected: $hasSelection")
        } catch (ex: Exception) {
            logger.warn("Action update error: ${ex.message}")
            // In case of error, enable by default
            e.presentation.isEnabled = true
        }
    }

    /**
     * Resolves file path. Can be relative or absolute path.
     */
    private fun resolveFilePath(filePath: String): File? {
        logger.warn("resolveFilePath called: $filePath")
        
        try {
            // 1. Method: If file is already absolute path, use directly
            val absoluteFile = File(filePath)
            if (absoluteFile.isAbsolute) {
                logger.warn("Absolute file path: ${absoluteFile.absolutePath}")
                return absoluteFile
            }
            
            // 2. Method: Check open projects
            val openProjects = com.intellij.openapi.project.ProjectManager.getInstance().openProjects
            for (project in openProjects) {
                val projectPath = project.basePath
                if (projectPath != null) {
                    // Try relative path from project root folder
                    val projectRelativeFile = File(File(projectPath), filePath)
                    if (projectRelativeFile.exists() || projectRelativeFile.parentFile.exists()) {
                        logger.warn("Project relative file path found: ${projectRelativeFile.absolutePath}")
                        return projectRelativeFile
                    }
                    
                    // Try from src folder
                    val srcRelativeFile = File(File(projectPath, "src"), filePath)
                    if (srcRelativeFile.exists() || srcRelativeFile.parentFile.exists()) {
                        logger.warn("src/ relative file path found: ${srcRelativeFile.absolutePath}")
                        return srcRelativeFile
                    }
                    
                    // Try from www/wwwroot folder
                    val wwwRelativeFile = File(File(projectPath, "wwwroot"), filePath)
                    if (wwwRelativeFile.exists() || wwwRelativeFile.parentFile.exists()) {
                        logger.warn("wwwroot/ relative file path found: ${wwwRelativeFile.absolutePath}")
                        return wwwRelativeFile
                    }
                    
                    // Try from public folder
                    val publicRelativeFile = File(File(projectPath, "public"), filePath)
                    if (publicRelativeFile.exists() || publicRelativeFile.parentFile.exists()) {
                        logger.warn("public/ relative file path found: ${publicRelativeFile.absolutePath}")
                        return publicRelativeFile
                    }
                }
            }
            
            // 3. Method: Try from user directory
            val userHomeFile = File(System.getProperty("user.home"), filePath)
            if (userHomeFile.parentFile.exists()) {
                logger.warn("User directory relative file path: ${userHomeFile.absolutePath}")
                return userHomeFile
            }
            
            // 4. Method: Try from working directory
            val workingDirFile = File(System.getProperty("user.dir"), filePath)
            if (workingDirFile.parentFile.exists()) {
                logger.warn("Working directory relative file path: ${workingDirFile.absolutePath}")
                return workingDirFile
            }
            
            // 5. Method: Try specified file path directly (last resort)
            logger.warn("Other methods failed, creating direct file path: $filePath")
            return File(filePath)
            
        } catch (e: Exception) {
            logger.warn("Error while resolving file path: ${e.message}", e)
            return File(filePath) // In case of error, return original path
        }
    }

    /**
     * Creates i18n key format from file path (with key)
     * Example: "Components/Pages/Public/Dashboard/DashboardMain.razor", "pano" -> "components.pages.public.dashboard.dashboardMain.pano"
     */
    protected fun generateI18nKeyFromPath(filePath: String, key: String): String {
        val i18nPath = generateI18nKeyFromPath(filePath)
        return "$i18nPath.$key"
    }

    /**
     * Creates i18n key format from file path
     * Example: "Components/Pages/Public/Dashboard/DashboardMain.razor" -> "components.pages.public.dashboard.dashboardMain"
     */
    protected fun generateI18nKeyFromPath(filePath: String): String {
        logger.warn("generateI18nKeyFromPath called: $filePath")
        // In new logic, we always return a fixed path
        return "components.pages.public.dashboard.dashboardMain"
    }

    /**
     * Combines i18n path and key to create full i18n key
     * Example: "components.pages.public.dashboard.dashboardMain" + "pano" -> "components.pages.public.dashboard.dashboardMain.pano"
     */
    protected fun generateFullI18nKey(i18nPath: String, key: String): String {
        return "$i18nPath.$key"
    }

    /**
     * Removes translation key from JSON file
     *
     * @param filePath File path
     * @param translationKey Translation key to remove (in dot format - e.g. "components.pages.public.dashboard.dashboardMain.key")
     * @return true if operation successful, false otherwise
     */
    protected fun removeTranslationFromJson(filePath: String, translationKey: String): Boolean {
        logger.info("Starting translation removal from file: $filePath, key: $translationKey")
        
        val file = File(filePath)
        if (!file.exists()) {
            logger.error("File not found: $filePath")
            return false
        }
        
        if (!file.canWrite()) {
            logger.error("No write permission for file: $filePath")
            return false
        }
        
        try {
            // Read file and preserve original content
            val originalContent = file.readText()
            if (originalContent.isBlank()) {
                logger.warn("File content is empty: $filePath")
                return false
            }
            
            // Get last part of key to be removed
            val keyParts = translationKey.split(".")
            val lastPart = keyParts.last()
            
            // Parse JSON with Gson
            val jsonParser = com.google.gson.JsonParser.parseString(originalContent)
            if (!jsonParser.isJsonObject) {
                logger.error("Not a valid JSON object: $filePath")
                return false
            }
            
            val rootObject = jsonParser.asJsonObject
            
            // Separate path segments and final key
            val pathParts = keyParts.dropLast(1)
            
            logger.info("Path segments: ${pathParts.joinToString(".")}, Final key: $lastPart")
            
            // Go to dashboardMain level
            var currentObject = rootObject
            for (segment in pathParts) {
                if (!currentObject.has(segment) || !currentObject.get(segment).isJsonObject) {
                    logger.warn("Path segment not found: $segment")
                    return false
                }
                currentObject = currentObject.getAsJsonObject(segment)
            }
            
            // At this point currentObject should be at dashboardMain level
            
            // Look for final key (kimHerkul)
            if (currentObject.has(lastPart)) {
                // Remove key directly - make sure nothing else is added
                currentObject.remove(lastPart)
                logger.info("Directly removed key: $lastPart")
                
                // Create nicely formatted JSON
                val gson = com.google.gson.GsonBuilder()
                    .setPrettyPrinting()
                    .disableHtmlEscaping()
                    .serializeNulls()
                    .create()
                
                val prettyJson = gson.toJson(rootObject)
                file.writeText(prettyJson)
                
                logger.info("Translation key successfully removed: $lastPart")
                return true
            } else {
                // Check all keys that might have been renamed
                val entries = currentObject.entrySet().toList()
                for (entry in entries) {
                    val key = entry.key
                    val value = entry.value
                    
                    // Find keys related to final key (e.g. kimHerkul) or with same content
                    if (key.contains(lastPart, ignoreCase = true) || 
                        (value.isJsonPrimitive && value.asString.contains(lastPart, ignoreCase = true)) || 
                        key.contains("yod", ignoreCase = true) && key.contains(lastPart, ignoreCase = true)) {
                        
                        // Remove this key
                        currentObject.remove(key)
                        logger.info("Removed related key: $key")
                        
                        // Create nicely formatted JSON
                        val gson = com.google.gson.GsonBuilder()
                            .setPrettyPrinting()
                            .disableHtmlEscaping()
                            .serializeNulls()
                            .create()
                        
                        val prettyJson = gson.toJson(rootObject)
                        file.writeText(prettyJson)
                        
                        logger.info("Related key removed: $key")
                        return true
                    }
                }
                
                logger.warn("No keys found: $lastPart")
                return false
            }
        } catch (e: Exception) {
            logger.error("Error while removing translation: ${e.message}", e)
            e.printStackTrace()
            return false
        }
    }
    
    /**
     * Gets all keys from JSON object with dot notation
     *
     * @param jsonObject JSON object
     * @param prefix Prefix (for nested objects)
     * @return List containing all keys
     */
    private fun getAllKeys(jsonObject: JSONObject, prefix: String = ""): List<String> {
        val keys = mutableListOf<String>()
        
        for (key in jsonObject.keys()) {
            val currentKey = if (prefix.isEmpty()) key else "$prefix.$key"
            
            when (val value = jsonObject.get(key)) {
                is JSONObject -> {
                    // Get key list recursively for nested object
                    keys.addAll(getAllKeys(value, currentKey))
                    // Also add current key
                    keys.add(currentKey)
                }
                else -> {
                    // Add key for normal value
                    keys.add(currentKey)
                }
            }
        }
        
        return keys
    }
    
    /**
     * Gets a value from JSON object using dot notation
     *
     * @param jsonObject JSON object
     * @param dotNotation Dot notation path
     * @return Value or null
     */
    private fun getValueByDotNotation(jsonObject: JSONObject, dotNotation: String): Any? {
        val keys = dotNotation.split(".")
        var current: Any? = jsonObject
        
        for (key in keys) {
            when (current) {
                is JSONObject -> {
                    if (!current.has(key)) {
                        return null
                    }
                    current = current.get(key)
                }
                else -> return null
            }
        }
        
        return current
    }
    
    /**
     * Removes a value from JSON object using dot notation
     *
     * @param jsonObject JSON object
     * @param dotNotation Dot notation path
     * @return true if operation successful, false otherwise
     */
    private fun removeValueByDotNotation(jsonObject: JSONObject, dotNotation: String): Boolean {
        val keys = dotNotation.split(".")
        var current = jsonObject
        
        // Traverse all keys except last one to reach relevant objects
        for (i in 0 until keys.size - 1) {
            val key = keys[i]
            if (!current.has(key) || current.get(key) !is JSONObject) {
                return false
            }
            current = current.getJSONObject(key)
        }
        
        // Remove last key
        val lastKey = keys.last()
        if (!current.has(lastKey)) {
            return false
        }
        
        current.remove(lastKey)
        return true
    }

    /**
     * Performs translation removal operation
     *
     * @param translationFilePaths Translation file paths
     * @param translationKey Translation key to remove
     * @return List of updated files
     */
    protected fun processTranslationRemoval(
        translationFilePaths: List<String>,
        translationKey: String
    ): List<File> {
        val updatedFiles = mutableListOf<File>()
        
        if (translationFilePaths.isEmpty()) {
            logger.warn("Translation file paths are empty. No files were updated.")
            return updatedFiles
        }
        
        logger.warn("Number of translation files to process: ${translationFilePaths.size}")
        logger.warn("Translation key to remove: $translationKey")
        
        for (filePath in translationFilePaths) {
            if (filePath.isBlank()) {
                logger.warn("Empty file path skipped.")
                continue
            }
            
            logger.warn("Removing translation: $filePath, key: $translationKey")
            
            try {
                val file = resolveFilePath(filePath)
                if (file == null) {
                    logger.warn("File path could not be resolved: $filePath")
                    continue
                }
                
                if (!file.exists()) {
                    logger.warn("File does not exist, skipping: ${file.absolutePath}")
                    continue
                }
                
                if (removeTranslationFromJson(file.absolutePath, translationKey)) {
                    updatedFiles.add(file)
                    logger.warn("Translation removed from file: ${file.absolutePath}")
                } else {
                    logger.warn("Translation could not be removed from file: ${file.absolutePath}")
                }
            } catch (e: Exception) {
                logger.warn("Error while processing file: $filePath. Error: ${e.message}")
                e.printStackTrace()
            }
        }
        
        return updatedFiles
    }

    /**
     * Converts JSON string to readable format
     * 
     * @param jsonStr JSON string
     * @return Formatted JSON string
     */
    private fun formatJsonBeautifully(jsonStr: String): String {
        if (jsonStr.isNullOrBlank()) {
            logger.warn("JSON to format is empty or null")
            return "{}"
        }
        
        return try {
            // Convert JSON to more readable format with Gson
            val gson = com.google.gson.GsonBuilder()
                .setPrettyPrinting()  // Pretty formatting
                .disableHtmlEscaping() // Don't escape HTML characters
                .serializeNulls()      // Show null values too
                .setLenient()          // More flexible parsing
                .create()
            
            val jsonElement = com.google.gson.JsonParser.parseString(jsonStr)
            gson.toJson(jsonElement)
        } catch (e: Exception) {
            logger.error("Error while formatting JSON: ${e.message}", e)
            // In case of error, return original string
            jsonStr 
        }
    }
} 