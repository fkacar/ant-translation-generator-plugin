package com.ant.utils

import com.ant.TranslationSettings
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.KeyboardShortcut
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.keymap.Keymap
import com.intellij.openapi.keymap.KeymapManager
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import javax.swing.KeyStroke

/**
 * Helper class for programmatically setting shortcuts
 */
object ShortcutManager {
    private val logger = Logger.getInstance(ShortcutManager::class.java)
    private const val ACTION_ID = "TranslationKeyboardShortcut"
    
    /**
     * Activates the shortcut selected by the user
     */
    fun applySelectedShortcut(shortcut: String) {
        val keymap = KeymapManager.getInstance().activeKeymap
        if (keymap == null) {
            logger.warn("Active keymap not found, shortcut could not be updated")
            return
        }
        
        try {
            // First remove all old shortcuts
            removeAllShortcuts(keymap)
            
            // Now add the new shortcut
            val keyboardShortcut = createKeyboardShortcut(shortcut)
            if (keyboardShortcut != null) {
                keymap.addShortcut(ACTION_ID, keyboardShortcut)
                logger.warn("Shortcut successfully updated: $shortcut")
            } else {
                // Add default shortcut
                val defaultShortcut = createKeyboardShortcut("shift control T")
                if (defaultShortcut != null) {
                    keymap.addShortcut(ACTION_ID, defaultShortcut)
                    logger.warn("Could not create shortcut, default shortcut (shift control T) added")
                }
            }
        } catch (e: Exception) {
            logger.warn("Error updating shortcut: ${e.message}", e)
        }
    }
    
    /**
     * Removes all existing shortcuts
     */
    private fun removeAllShortcuts(keymap: Keymap) {
        val existingShortcuts = keymap.getShortcuts(ACTION_ID)
        for (shortcut in existingShortcuts) {
            keymap.removeShortcut(ACTION_ID, shortcut)
        }
        logger.warn("${existingShortcuts.size} existing shortcuts removed")
    }
    
    /**
     * Creates a KeyboardShortcut object from string format
     */
    private fun createKeyboardShortcut(shortcutStr: String): KeyboardShortcut? {
        try {
            val parts = shortcutStr.trim().split("\\s+".toRegex())
            val keyString = parts.lastOrNull() ?: "T"
            
            // Determine modifiers
            val ctrl = parts.contains("control")
            val shift = parts.contains("shift")
            val alt = parts.contains("alt")
            val meta = parts.contains("meta")
            
            // Create modifiers value
            var modifiers = 0
            if (ctrl) modifiers = modifiers or InputEvent.CTRL_DOWN_MASK
            if (shift) modifiers = modifiers or InputEvent.SHIFT_DOWN_MASK
            if (alt) modifiers = modifiers or InputEvent.ALT_DOWN_MASK
            if (meta) modifiers = modifiers or InputEvent.META_DOWN_MASK
            
            // Determine KeyCode
            val keyCode = when (keyString.uppercase()) {
                "T" -> KeyEvent.VK_T
                "G" -> KeyEvent.VK_G
                else -> KeyEvent.VK_T // Default
            }
            
            val keyStroke = KeyStroke.getKeyStroke(keyCode, modifiers)
            return KeyboardShortcut(keyStroke, null)
        } catch (e: Exception) {
            logger.warn("Error creating shortcut: ${e.message}", e)
            return null
        }
    }
    
    /**
     * Initializes the application when IDE starts
     */
    fun initialize() {
        val settings = TranslationSettings.getApplicationInstance()
        val shortcut = settings.shortcutKey.takeIf { !it.isNullOrBlank() } ?: "shift control T"
        
        logger.warn("Initializing ShortcutManager, shortcut to be set: $shortcut")
        applySelectedShortcut(shortcut)
    }
} 