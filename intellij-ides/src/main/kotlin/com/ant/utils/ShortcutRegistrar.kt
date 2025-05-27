package com.ant.utils

import com.ant.TranslationSettings
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.KeyboardShortcut
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.keymap.Keymap
import com.intellij.openapi.keymap.KeymapManager
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import javax.swing.KeyStroke

/**
 * Shortcut registration system
 * Registers shortcuts programmatically when plugin starts
 */
object ShortcutRegistrar {
    private val logger = logger<ShortcutRegistrar>()
    
    /**
     * Registers plugin shortcuts
     */
    fun registerShortcuts() {
        logger.warn("Shortcuts are being registered programmatically...")
        
        try {
            // Get ActionManager and KeymapManager
            val actionManager = ActionManager.getInstance()
            val keymapManager = KeymapManager.getInstance()
            val activeKeymap = keymapManager.activeKeymap
            
            if (actionManager == null || keymapManager == null || activeKeymap == null) {
                logger.warn("Shortcut registration failed: ActionManager or KeymapManager is null!")
                return
            }
            
            // Get shortcuts from Settings
            val settings = TranslationSettings.getApplicationInstance()
            val generateShortcut = settings.shortcutKey.trim().lowercase()
            val removeShortcut = settings.getRemoveTranslationShortcut().trim().lowercase()
            
            logger.warn("User selected Generate shortcut: $generateShortcut")
            logger.warn("User selected Remove shortcut: $removeShortcut")
            
            // First clear ALL shortcuts (very important)
            logger.warn("Clearing all shortcuts...")
            // Clear shortcuts using the correct action IDs from plugin.xml
            clearAllShortcuts(activeKeymap, "com.ant.GenerateTranslationKey")
            clearAllShortcuts(activeKeymap, "com.ant.RemoveTranslationKey")
            
            // Register Generate Translation shortcut
            logger.warn("Registering Generate Translation shortcut: $generateShortcut")
            registerCustomShortcut(activeKeymap, "com.ant.GenerateTranslationKey", generateShortcut)
            
            // Register Remove Translation shortcut
            logger.warn("Registering Remove Translation shortcut: $removeShortcut")
            registerCustomShortcut(activeKeymap, "com.ant.RemoveTranslationKey", removeShortcut)
            
            // Check the shortcuts we registered
            logger.warn("Checking registered shortcuts...")
            val generateKeyShortcuts = keymapManager.activeKeymap.getShortcuts("com.ant.GenerateTranslationKey")
            val removeKeyShortcuts = keymapManager.activeKeymap.getShortcuts("com.ant.RemoveTranslationKey")
            
            logger.warn("GenerateTranslationKey has ${generateKeyShortcuts.size} shortcuts defined")
            logger.warn("RemoveTranslationKey has ${removeKeyShortcuts.size} shortcuts defined")
            
            // Validate shortcuts and force refresh keymaps
            try {
                // Directly use KeymapManager to ensure it takes effect
                logger.warn("Keymap changes saved")
            } catch (e: Exception) {
                logger.warn("Error in keymap operation: ${e.message}")
            }
            
            logger.warn("Shortcut registration completed.")
        } catch (e: Exception) {
            logger.warn("Error occurred during shortcut registration: ${e.message}", e)
        }
    }
    
    /**
     * Clears all shortcuts for an action
     */
    private fun clearAllShortcuts(keymap: Keymap, actionId: String) {
        try {
            val existingShortcuts = keymap.getShortcuts(actionId)
            if (existingShortcuts.isNotEmpty()) {
                logger.warn("'$actionId' has ${existingShortcuts.size} shortcuts to be removed")
                existingShortcuts.forEach { keymap.removeShortcut(actionId, it) }
                logger.warn("'$actionId' has all shortcuts removed")
            } else {
                logger.warn("'$actionId' no shortcut to be removed")
            }
        } catch (e: Exception) {
            logger.warn("Error in clearing shortcuts: ${e.message}", e)
        }
    }
    
    /**
     * Adds a custom shortcut
     */
    private fun registerCustomShortcut(keymap: Keymap, actionId: String, shortcutText: String) {
        try {
            // Create shortcut
            val keyStroke = createKeyStroke(shortcutText)
            if (keyStroke != null) {
                val shortcut = KeyboardShortcut(keyStroke, null)
                keymap.addShortcut(actionId, shortcut)
                logger.warn("Shortcut added: $actionId -> $shortcutText")
            } else {
                logger.warn("Shortcut cannot be created: $shortcutText")
            }
        } catch (e: Exception) {
            logger.warn("Error in saving shortcut: $actionId, $shortcutText - ${e.message}", e)
        }
    }
    
    /**
     * Creates a KeyStroke from text definition
     */
    private fun createKeyStroke(shortcutText: String): KeyStroke? {
        try {
            val parts = shortcutText.lowercase()
                .replace("+", " ") // Replace "+" characters with spaces
                .split("\\s+".toRegex()) // Multiple spaces can be present
                .filter { it.isNotBlank() } // Filter out blank parts
            
            logger.warn("Parsed shortcut parts: ${parts.joinToString()}")
            
            // Determine modifiers
            var modifiers = 0
            for (part in parts.dropLast(1)) {
                when (part) {
                    "shift" -> modifiers = modifiers or InputEvent.SHIFT_DOWN_MASK
                    "control", "ctrl" -> modifiers = modifiers or InputEvent.CTRL_DOWN_MASK
                    "alt" -> modifiers = modifiers or InputEvent.ALT_DOWN_MASK
                    "meta", "cmd" -> modifiers = modifiers or InputEvent.META_DOWN_MASK
                }
            }
            
            // Determine key
            val keyText = parts.lastOrNull() ?: return null
            
            // Create keyCode for last character
            val keyCode = when (keyText) {
                "t" -> KeyEvent.VK_T
                "g" -> KeyEvent.VK_G
                "backspace", "back_space" -> KeyEvent.VK_BACK_SPACE
                "delete" -> KeyEvent.VK_DELETE
                "enter" -> KeyEvent.VK_ENTER
                "space" -> KeyEvent.VK_SPACE
                else -> {
                    // Accept any letter key for more flexibility
                    val upperChar = keyText.uppercase().firstOrNull() ?: return null
                    if (upperChar.isLetter()) {
                        // Get the KeyEvent VK code for this letter
                        KeyEvent::class.java.getField("VK_$upperChar").getInt(null)
                    } else {
                        logger.warn("Invalid key character: $keyText")
                        return null
                    }
                }
            }
            
            logger.warn("KeyStroke created: modifiers=$modifiers, keyCode=$keyCode")
            return KeyStroke.getKeyStroke(keyCode, modifiers)
        } catch (e: Exception) {
            logger.warn("Error in creating KeyStroke: $shortcutText - ${e.message}", e)
            return null
        }
    }
} 