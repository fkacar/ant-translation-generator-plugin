package com.ant

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.openapi.application.ApplicationManager
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.SystemInfo
import java.util.*
import com.intellij.openapi.keymap.KeymapUtil
import com.intellij.openapi.actionSystem.KeyboardShortcut
import com.intellij.util.messages.Topic
import javax.swing.KeyStroke
import java.awt.event.KeyEvent
import java.awt.event.InputEvent

// Persistent XML storage for settings
@State(
    name = "TranslationSettings",
    storages = [Storage("translation-generator.xml")]
)
@Service
class TranslationSettings : PersistentStateComponent<TranslationSettings> {
    // Interface for receiving notifications when settings change
    interface SettingsChangeListener {
        fun settingsChanged()
    }
    
    companion object {
        private val logger = Logger.getInstance(TranslationSettings::class.java)
        
        // Topic for broadcasting messages
        val SETTINGS_CHANGED_TOPIC = Topic.create("TranslationSettingsChanged", SettingsChangeListener::class.java)
        
        // Access for application service
        fun getApplicationInstance(): TranslationSettings {
            val settings = ApplicationManager.getApplication().getService(TranslationSettings::class.java)
            logger.warn("Application service obtained: $settings")
            return settings
        }
        
        // Access for project service
        fun getInstance(project: Project): TranslationSettings {
            val settings = project.getService(TranslationSettings::class.java)
            logger.warn("Project service obtained: $settings")
            return settings
        }
    }
    
    // Variables for settings
    var shortcutKey = "shift + control + t"
    var removeShortcutKey = "shift + control + backspace"
    var translationFilePaths: MutableList<String> = mutableListOf()
    var translationFunction: String = "t"
    
    // Auto Translate settings
    var autoTranslateEnabled: Boolean = false
    var openAiApiKey: String = ""
    var sourceLanguageFile: String = ""
    var languageFileLanguages: MutableMap<String, String> = mutableMapOf() // file path -> language code
    
    init {
        logger.warn("TranslationSettings class initialized. Default values: shortcut=$shortcutKey, function=$translationFunction")
    }
    
    override fun getState(): TranslationSettings {
        logger.warn("TranslationSettings.getState() called, current settings: shortcut=$shortcutKey, file count=${translationFilePaths.size}, function=$translationFunction")
        return this
    }
    
    override fun loadState(state: TranslationSettings) {
        try {
            logger.warn("TranslationSettings.loadState() called, settings to load: shortcut=${state.shortcutKey}, file count=${state.translationFilePaths.size}, function=${state.translationFunction}")
            
            // Shortcut validation - use default if empty or invalid
            val validShortcut = if (state.shortcutKey.isNullOrBlank()) {
                "shift + control + t"
            } else {
                state.shortcutKey
            }
            
            // Check if shortcut value changed
            if (this.shortcutKey != validShortcut) {
                logger.warn("Shortcut changed: '$this.shortcutKey' -> '$validShortcut'")
                this.shortcutKey = validShortcut
                // Notify about changes but first load other settings
            }
            
            // Filter out empty file paths
            this.translationFilePaths = state.translationFilePaths
                .filterNotNull()
                .filter { it.isNotBlank() }
                .toMutableList() 
                
            this.translationFunction = if (state.translationFunction.isNullOrBlank()) "t" else state.translationFunction
            
            // Load auto translate settings
            this.autoTranslateEnabled = state.autoTranslateEnabled
            this.openAiApiKey = state.openAiApiKey ?: ""
            this.sourceLanguageFile = state.sourceLanguageFile ?: ""
            this.languageFileLanguages = state.languageFileLanguages ?: mutableMapOf()
            
            logger.warn("Settings successfully loaded. Current values: shortcut=$shortcutKey, file count=${translationFilePaths.size}, function=$translationFunction, autoTranslate=$autoTranslateEnabled")
            
            // Notify listeners when settings change
            notifySettingsChanged()
        } catch (e: Exception) {
            logger.warn("Error occurred while loading settings: ${e.message}", e)
            // Set default values in case of an error
            shortcutKey = "shift + control + t"
            translationFilePaths = mutableListOf()
            translationFunction = "t"
            logger.warn("Default settings loaded after error")
        }
    }
    
    /**
     * Notify about changes but first load other settings
     */
    private fun notifySettingsChanged() {
        try {
            logger.warn("Settings changed, notifying listeners")
            ApplicationManager.getApplication().messageBus.syncPublisher(SETTINGS_CHANGED_TOPIC).settingsChanged()
        } catch (e: Exception) {
            logger.warn("Error during settings change notification: ${e.message}", e)
        }
    }
    
    // Display shortcut in IDE format
    fun getShortcutDisplayText(shortcutStr: String): String {
        try {
            val parts = shortcutStr.trim()
                .replace("+", " ") // Convert "+" characters to spaces
                .split("\\s+".toRegex()) // There may be multiple spaces
                .filter { it.isNotBlank() } // Filter out empty parts
            
            val keyString = parts.lastOrNull() ?: "t"
            
            // Determine modifiers
            val ctrl = parts.contains("control") || parts.contains("ctrl")
            val shift = parts.contains("shift")
            val alt = parts.contains("alt")
            val meta = parts.contains("meta") || parts.contains("cmd")
            
            // Create KeyStroke
            var modifiers = 0
            if (ctrl) modifiers = modifiers or InputEvent.CTRL_DOWN_MASK
            if (shift) modifiers = modifiers or InputEvent.SHIFT_DOWN_MASK
            if (alt) modifiers = modifiers or InputEvent.ALT_DOWN_MASK
            if (meta) modifiers = modifiers or InputEvent.META_DOWN_MASK
            
            // Determine KeyCode
            val keyCode = when (keyString.lowercase()) {
                "t" -> KeyEvent.VK_T
                "g" -> KeyEvent.VK_G
                else -> KeyEvent.VK_T // Default
            }
            
            val keyStroke = KeyStroke.getKeyStroke(keyCode, modifiers)
            val shortcut = KeyboardShortcut(keyStroke, null)
            val shortcutText = KeymapUtil.getShortcutText(shortcut)
            
            logger.warn("Shortcut text created: $shortcutStr -> $shortcutText")
            return shortcutText
        } catch (e: Exception) {
            logger.warn("Error while creating shortcut text: ${e.message}", e)
            return if (SystemInfo.isMac) "⇧⌘T" else "Shift+Ctrl+T"
        }
    }
    
    /**
     * Getter method for Check Translation shortcut.
     * Default shortcut: shift + control + t
     */
    fun getCheckTranslationShortcut(): String {
        return "shift + control + t"
    }

    /**
     * Getter method for Generate Translation shortcut.
     * Default shortcut: shift + control + g
     */
    fun getGenerateTranslationShortcut(): String {
        return "shift + control + g"
    }

    /**
     * Getter method for Remove Translation shortcut.
     * Default shortcut: shift + control + backspace
     */
    fun getRemoveTranslationShortcut(): String {
        return removeShortcutKey.ifBlank { "shift + control + backspace" }
    }
    
    // toString method for logging
    override fun toString(): String {
        return "TranslationSettings(shortcutKey='$shortcutKey', paths=${translationFilePaths.size}, function='$translationFunction', autoTranslate=$autoTranslateEnabled, sourceFile='$sourceLanguageFile')"
    }
} 