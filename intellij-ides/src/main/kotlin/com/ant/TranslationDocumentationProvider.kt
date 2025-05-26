package com.ant

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.psi.PsiElement
import com.intellij.openapi.project.Project
import org.json.JSONObject
import org.json.JSONException
import java.io.File
import java.nio.file.Paths
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.text.StringUtil
import java.util.concurrent.ConcurrentHashMap
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.Messages
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import java.awt.Color
import javax.swing.JComponent

/**
 * Documentation provider that displays translation keys in a document form
 */
class TranslationDocumentationProvider : AbstractDocumentationProvider() {
    private val logger = Logger.getInstance(TranslationDocumentationProvider::class.java)
    private val translationsCache = ConcurrentHashMap<String, MutableMap<String, String>>() // Key -> (language -> translation)
    private val jsonCache = ConcurrentHashMap<String, Pair<JSONObject, Long>>() // filePath -> (jsonObject, lastModified)
    private var lastDebugInfo: String? = null

    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
        if (element == null) return null
        
        // Always clear cache to ensure fresh data
        // This ensures we always get the latest version from JSON files
        clearCache()
        
        // Extract translation key from the element text
        val key = extractTranslationKey(element.text ?: "") ?: 
                  extractTranslationKey(originalElement?.text ?: "") ?: 
                  return null
        
        // Find all translations
        val settings = ApplicationManager.getApplication().getService(TranslationSettings::class.java)
        val project = element.project
        val translationsMap = findTranslations(key, project, settings)
        
        // Create documentation
        return if (translationsMap.isNotEmpty()) {
            buildDocumentation(key, translationsMap)
        } else {
            val debugInfo = lastDebugInfo ?: "Unknown issue. Check IDE logs for details."
            buildNotFoundDocumentation(key, debugInfo)
        }
    }
    
    /**
     * Extracts the key from t('key') or t("key")
     */
    private fun extractTranslationKey(text: String): String? {
        val settings = ApplicationManager.getApplication().getService(TranslationSettings::class.java)
        val translationFunction = settings?.translationFunction ?: "t"
        
        val singleQuotePattern = "$translationFunction\\('([^']*)'\\)".toRegex()
        val doubleQuotePattern = "$translationFunction\\(\"([^\"]*)\"\\)".toRegex()
        
        val singleQuoteMatch = singleQuotePattern.find(text)
        if (singleQuoteMatch != null && singleQuoteMatch.groupValues.size > 1) {
            logger.info("Extracted key from single quotes: ${singleQuoteMatch.groupValues[1]}")
            return singleQuoteMatch.groupValues[1]
        }
        
        val doubleQuoteMatch = doubleQuotePattern.find(text)
        if (doubleQuoteMatch != null && doubleQuoteMatch.groupValues.size > 1) {
            logger.info("Extracted key from double quotes: ${doubleQuoteMatch.groupValues[1]}")
            return doubleQuoteMatch.groupValues[1]
        }
        
        logger.info("No translation key found in text: $text")
        return null
    }
    
    /**
     * Creates documentation with all translations
     */
    private fun buildDocumentation(key: String, translationsMap: Map<String, String>): String {
        val contentBuilder = StringBuilder()
        
        // Add each language and its translation
        for ((languageCode, translation) in translationsMap) {
            // Extract the language code from the file path
            val langDisplay = extractLanguageFromPath(languageCode)
            
            // Bold and red color for language code
            contentBuilder.append("<p><span style=\"color:#CC0000; font-weight:bold;\">")
            contentBuilder.append(langDisplay)
            contentBuilder.append("</span>: ")
            
            // Translation value
            contentBuilder.append(StringUtil.escapeXmlEntities(translation))
            contentBuilder.append("</p>")
        }
        
        return DocumentationMarkup.DEFINITION_START +
                "Translation" +
                DocumentationMarkup.DEFINITION_END +
                DocumentationMarkup.CONTENT_START +
                contentBuilder.toString() +
                DocumentationMarkup.CONTENT_END +
                DocumentationMarkup.SECTIONS_START +
                "<p><b>Key:</b> $key</p>" +
                DocumentationMarkup.SECTIONS_END
    }
    
    /**
     * Creates documentation for when translation is not found
     */
    private fun buildNotFoundDocumentation(key: String, debugInfo: String): String {
        return DocumentationMarkup.DEFINITION_START +
                "Translation Not Found" +
                DocumentationMarkup.DEFINITION_END +
                DocumentationMarkup.CONTENT_START +
                StringUtil.escapeXmlEntities("Translation not found: $key\nDebug Info: $debugInfo") +
                DocumentationMarkup.CONTENT_END +
                DocumentationMarkup.SECTIONS_START +
                "<p><b>Key:</b> $key</p>" +
                DocumentationMarkup.SECTIONS_END
    }
    
    /**
     * Extracts language code from file path
     */
    private fun extractLanguageFromPath(path: String): String {
        // Try to extract language code from filename
        val filename = path.substringAfterLast("/").substringBeforeLast(".")
        
        // Check if filename is a language code (tr.json, en.json, etc.)
        if (filename.matches(Regex("[a-zA-Z]{2}(-[a-zA-Z]{2})?"))) {
            return filename
        }
        
        // Check if the file is in a language folder (locales/tr/translation.json)
        val pathParts = path.split("/")
        for (i in 0 until pathParts.size - 1) {
            val part = pathParts[i]
            if (part.matches(Regex("[a-zA-Z]{2}(-[a-zA-Z]{2})?"))) {
                return part
            }
        }
        
        // Fallback to filename without extension
        return filename
    }
    
    /**
     * Finds translations for the specified key in all language files
     */
    private fun findTranslations(key: String, project: Project, settings: TranslationSettings?): Map<String, String> {
        lastDebugInfo = null
        val translationsMap = mutableMapOf<String, String>()
        
        try {
            if (settings == null) {
                lastDebugInfo = "Settings are null, cannot find translation"
                logger.warn(lastDebugInfo)
                return translationsMap
            }
            
            logger.warn("Finding translations for key: $key")
            
            // Check paths
            val translationPaths = settings.translationFilePaths
            if (translationPaths.isEmpty()) {
                lastDebugInfo = "No translation paths configured. Add translation files in Settings > Tools > Translation Settings"
                logger.warn(lastDebugInfo)
                return translationsMap
            }
            
            logger.warn("Checking translation paths: ${translationPaths.joinToString()}")
            
            var filesChecked = 0
            var invalidJsonFiles = 0
            var nonExistentFiles = 0
            
            // Check each translation file
            for (path in translationPaths) {
                try {
                    val filePath = Paths.get(project.basePath ?: "", path).toString()
                    val file = File(filePath)
                    filesChecked++
                    
                    logger.warn("Checking file: $filePath")
                    
                    // Does the file exist?
                    if (!file.exists() || !file.isFile) {
                        nonExistentFiles++
                        logger.warn("File does not exist: $filePath")
                        continue
                    }
                    
                    // Get file's last modified timestamp
                    val lastModified = file.lastModified()
                    
                    // Load JSON file
                    var jsonObject: JSONObject? = null
                    val cachedData = jsonCache[filePath]
                    
                    // Check if we have a cached version and if it's still valid
                    if (cachedData != null && cachedData.second == lastModified) {
                        jsonObject = cachedData.first
                        logger.warn("Using cached JSON for: $filePath (last modified: $lastModified)")
                    } else {
                        try {
                            val content = file.readText()
                            logger.warn("Read content from: $filePath (last modified: $lastModified)")
                            
                            // Check if content is valid JSON
                            try {
                                jsonObject = JSONObject(content)
                                jsonCache[filePath] = Pair(jsonObject, lastModified)
                                logger.warn("Successfully parsed JSON from: $filePath")
                                logger.warn("JSON keys at root level: ${getJsonKeys(jsonObject)}")
                            } catch (e: JSONException) {
                                invalidJsonFiles++
                                lastDebugInfo = "Invalid JSON format in file: ${file.name}"
                                logger.warn("Invalid JSON format in translation file: $filePath", e)
                                continue
                            }
                        } catch (e: Exception) {
                            logger.warn("Failed to read translation file: $filePath", e)
                            lastDebugInfo = "Failed to read translation file: ${file.name}"
                            continue
                        }
                    }
                    
                    if (jsonObject == null) {
                        continue
                    }
                    
                    // Split nested key path
                    val parts = key.split(".")
                    
                    // First try direct access - check if the entire key exists as a single entry
                    try {
                        if (jsonObject.has(key)) {
                            val value = jsonObject.getString(key)
                            logger.warn("Found direct key match: $key = $value")
                            translationsMap[path] = value
                            continue
                        }
                    } catch (e: Exception) {
                        logger.warn("Direct key access failed: ${e.message}")
                    }
                    
                    // Nested access method
                    try {
                        var data: JSONObject = jsonObject
                        var currentPath = ""
                        var navigatedSuccessfully = true
                        
                        // Navigate through nested keys
                        for (i in 0 until parts.size - 1) {
                            val part = parts[i]
                            currentPath = if (currentPath.isEmpty()) part else "$currentPath.$part"
                            
                            // Try case-sensitive first
                            if (data.has(part)) {
                                val nextData = data.get(part)
                                if (nextData is JSONObject) {
                                    data = nextData
                                } else {
                                    navigatedSuccessfully = false
                                    break
                                }
                            } else {
                                // Try case-insensitive
                                var found = false
                                
                                val allKeys = mutableListOf<String>()
                                val keysIterator = data.keys()
                                while (keysIterator.hasNext()) {
                                    allKeys.add(keysIterator.next() as String)
                                }
                                
                                for (existingKey in allKeys) {
                                    if (existingKey.equals(part, ignoreCase = true)) {
                                        val nextData = data.get(existingKey)
                                        if (nextData is JSONObject) {
                                            data = nextData
                                            found = true
                                            break
                                        }
                                    }
                                }
                                
                                if (!found) {
                                    navigatedSuccessfully = false
                                    break
                                }
                            }
                        }
                        
                        // Get value from the last key if navigation succeeded
                        if (navigatedSuccessfully && parts.isNotEmpty()) {
                            val lastKey = parts.last()
                            
                            // Try case-sensitive first
                            if (data.has(lastKey)) {
                                val value = data.getString(lastKey)
                                logger.warn("Found value for last key $lastKey: $value")
                                translationsMap[path] = value
                            } else {
                                // Try case-insensitive
                                val allKeys = mutableListOf<String>()
                                val keysIterator = data.keys()
                                while (keysIterator.hasNext()) {
                                    allKeys.add(keysIterator.next() as String)
                                }
                                
                                for (existingKey in allKeys) {
                                    if (existingKey.equals(lastKey, ignoreCase = true)) {
                                        val value = data.getString(existingKey)
                                        logger.warn("Found case-insensitive value for last key $lastKey = $existingKey: $value")
                                        translationsMap[path] = value
                                        break
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        logger.warn("Error navigating JSON: ${e.message}", e)
                    }
                } catch (e: Exception) {
                    logger.warn("Error accessing translation file: ${e.message}", e)
                    continue
                }
            }
            
            if (translationsMap.isEmpty()) {
                if (filesChecked == 0) {
                    lastDebugInfo = "No translation files were checked"
                } else if (nonExistentFiles == filesChecked) {
                    lastDebugInfo = "All configured translation files do not exist"
                } else if (invalidJsonFiles > 0) {
                    lastDebugInfo = "$invalidJsonFiles out of $filesChecked translation files had invalid JSON format"
                } else {
                    lastDebugInfo = "Key not found in any translation file"
                }
                
                logger.warn("No translations found for key: $key - $lastDebugInfo")
            }
            
            return translationsMap
        } catch (e: Exception) {
            lastDebugInfo = "Error finding translations: ${e.message}"
            logger.warn("Error finding translations", e)
            return translationsMap
        }
    }
    
    /**
     * Finds a single translation for the specified key
     * Returns the first translation found in any language file
     */
    fun findTranslation(key: String, project: Project, settings: TranslationSettings?): String? {
        val translations = findTranslations(key, project, settings)
        return if (translations.isNotEmpty()) {
            translations.values.first()
        } else {
            null
        }
    }
    
    /**
     * Gets all keys from a JSONObject for logging
     */
    private fun getJsonKeys(jsonObject: JSONObject): String {
        val keys = mutableListOf<String>()
        val keysIterator = jsonObject.keys()
        while (keysIterator.hasNext()) {
            keys.add(keysIterator.next() as String)
        }
        return keys.joinToString()
    }
    
    /**
     * Flattens a JSON structure for easier debugging
     */
    private fun flattenJson(jsonObject: JSONObject): List<String> {
        val result = mutableListOf<String>()
        
        // Extract all keys
        val keys = jsonObject.keys()
        while (keys.hasNext()) {
            val key = keys.next() as String
            try {
                val value = jsonObject.get(key)
                if (value is JSONObject) {
                    // If it's a nested object, recurse with prefix
                    val nestedEntries = flattenJson(value).map { "$key.$it" }
                    result.addAll(nestedEntries)
                } else {
                    // If it's a leaf value, add it
                    val stringValue = if (value.toString().length > 20) 
                        "${value.toString().substring(0, 20)}..." 
                    else 
                        value.toString()
                    result.add("$key: $stringValue")
                }
            } catch (e: Exception) {
                result.add("$key: [Error: ${e.message}]")
            }
        }
        
        return result
    }
    
    /**
     * Clears the cache
     */
    fun clearCache() {
        translationsCache.clear()
        jsonCache.clear()
        logger.warn("Translation and JSON caches cleared")
    }

    /**
     * Clears the cache and ensures translations are reloaded
     */
    companion object {
        fun clearCache() {
            // First get the service instance
            val providerInstance = ApplicationManager.getApplication()
                .getService(TranslationDocumentationProvider::class.java)
            
            // If service instance is available, clear the cache
            providerInstance?.apply {
                translationsCache.clear()
                jsonCache.clear()
                logger.warn("TranslationDocumentationProvider cache cleared")
            }
        }
    }
} 