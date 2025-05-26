package com.ant

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.options.ConfigurationException
import com.intellij.ui.components.JBList
import com.intellij.ui.dsl.builder.*
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import javax.swing.*
import java.io.File
import org.json.JSONObject
import org.json.JSONException
import com.intellij.openapi.ui.Messages
import java.awt.Component
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import java.nio.file.Paths
import com.intellij.openapi.keymap.KeymapUtil
import com.intellij.openapi.actionSystem.KeyboardShortcut
import com.intellij.openapi.util.SystemInfo
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.border.TitledBorder
import com.intellij.openapi.components.service
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.openapi.options.ConfigurableProvider
import com.ant.utils.LanguageCodes
import com.ant.services.OpenAITranslationService
import java.awt.GridBagLayout
import java.awt.GridBagConstraints
import java.awt.Insets
import javax.swing.ButtonGroup
import javax.swing.JRadioButton

/**
 * Data class for language file entries
 */
data class LanguageFileEntry(
    val filePath: String,
    val languageCode: String,
    val languageName: String
) {
    override fun toString(): String = "$filePath ($languageName)"
}

class TranslationSettingsConfigurable : SearchableConfigurable, Configurable.NoScroll {
    companion object {
        private val logger = Logger.getInstance(TranslationSettingsConfigurable::class.java)
    }
    
    init {
        // Log when the class is instantiated
        logger.warn("TranslationSettingsConfigurable INSTANTIATED") 
    }
    
    private var translationPathsList: JBList<String>? = null
    private var translationFunctionField: JTextField? = null
    private var shortcutComboBox: JComboBox<String>? = null
    private var shortcutPreviewLabel: JLabel? = null
    
    // Auto Translate fields
    private var autoTranslateEnabledRadio: JRadioButton? = null
    private var autoTranslateDisabledRadio: JRadioButton? = null
    private var openAiApiKeyField: JTextField? = null
    private var sourceLanguageComboBox: JComboBox<String>? = null
    private var languageFilesList: JBList<LanguageFileEntry>? = null
    
    // Property for accessing the service to get the latest update each time
    private val settings: TranslationSettings
        get() = ApplicationManager.getApplication().getService(TranslationSettings::class.java)
        
    // Shortcut options
    private val shortcutOptions = arrayOf(
        "shift + control + t",
        "shift + meta + t",
        "control + alt + t",
        "shift + control + alt + t",
        "shift + meta + alt + t",
        "control + alt + g",
        "shift + control + alt + g",
        "shift + meta + alt + g"
    )
    
    // Add ID for search
    override fun getId(): String = "com.ant.TranslationSettingsConfigurable"

    override fun getDisplayName(): String = "Ant Translation Settings"

    override fun getHelpTopic(): String? = "com.ant.TranslationSettingsConfigurable"

    override fun createComponent(): JComponent {
        logger.warn("TranslationSettingsConfigurable.createComponent() called, current shortcut: ${settings.shortcutKey}")
        val panel = JPanel(BorderLayout())
        panel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        
        try {
            // Add instruction label
            val instructionLabel = JLabel("<html><body>" +
                    "<b>Translation Files Configuration</b><br>" +
                    "Add JSON translation files using project-relative paths.<br>" +
                    "Use paths relative to project root, like: 'public/locales/en.json' or 'src/assets/i18n/en.json'<br>" +
                    "Translation format should be nested JSON objects matching the dot notation in your code.<br>" +
                    "Example: If your code uses t('user.profile.name'), the JSON should have: { \"user\": { \"profile\": { \"name\": \"value\" } } }" +
                    "</body></html>")
            instructionLabel.border = BorderFactory.createEmptyBorder(0, 0, 10, 0)
            panel.add(instructionLabel, BorderLayout.NORTH)
            
            // Main panel with tabs
            val tabbedPane = JTabbedPane()
            
            // Files tab
            val filesPanel = JPanel(BorderLayout())
            filesPanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
            
            // Translation paths list
            translationPathsList = JBList<String>().apply {
                model = DefaultListModel<String>().apply {
                    // Safely add translation paths
                    settings.translationFilePaths.forEach { path ->
                        if (path.isNotBlank()) {
                            addElement(path)
                        }
                    }
                    logger.warn("${settings.translationFilePaths.size} translation paths loaded")
                }
            }
            
            val listPanel = JPanel(BorderLayout()).apply {
                add(JScrollPane(translationPathsList), BorderLayout.CENTER)
                add(createButtonPanel(), BorderLayout.SOUTH)
            }
            
            filesPanel.add(listPanel, BorderLayout.CENTER)
            tabbedPane.addTab("Translation Files", filesPanel)
            
            // Settings tab
            val settingsPanel = JPanel(BorderLayout(10, 10))
            settingsPanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
            
            // Function name panel
            val functionPanel = JPanel(BorderLayout())
            functionPanel.border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Translation Function")
            
            // Translation function input - Protection against NullPointerException
            val translationFunction = if (settings.translationFunction.isBlank()) "t" else settings.translationFunction
            translationFunctionField = JTextField(translationFunction)
            translationFunctionField!!.columns = 20
            
            val functionHelpText = JLabel("Function name used in code, e.g., t('key') or i18n('key')")
            functionHelpText.border = BorderFactory.createEmptyBorder(5, 0, 0, 0)
            
            functionPanel.add(JLabel("Function Name:"), BorderLayout.WEST)
            functionPanel.add(translationFunctionField, BorderLayout.CENTER)
            functionPanel.add(functionHelpText, BorderLayout.SOUTH)
            
            // Shortcut panel
            val shortcutPanel = JPanel(BorderLayout())
            shortcutPanel.border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Keyboard Shortcut")
            
            // Using ComboBox
            shortcutComboBox = JComboBox(shortcutOptions)
            shortcutComboBox!!.maximumRowCount = shortcutOptions.size // Show all options
            
            // Set current selected value in combo box, protection against NullPointerException
            val currentShortcut = if (settings.shortcutKey.isBlank()) "shift control T" else settings.shortcutKey
            logger.warn("Selecting current shortcut: $currentShortcut")
            val index = shortcutOptions.indexOf(currentShortcut)
            if (index >= 0) {
                shortcutComboBox!!.selectedIndex = index
                logger.warn("Selected index: $index")
            } else {
                logger.warn("Shortcut not found in list: $currentShortcut, selecting default")
                shortcutComboBox!!.selectedIndex = 0 // Default selection
            }
            
            shortcutPreviewLabel = JLabel()
            updateShortcutPreview() // Set preview initially
            
            // Update preview when combo box changes
            shortcutComboBox!!.addActionListener { 
                updateShortcutPreview()
            }
            
            val shortcutHelpText = JLabel("<html>Select one of the predefined shortcuts.<br>" +
                    "You may need to restart the IDE to use the shortcuts.<br>" +
                    "T = Check translation, G = Generate translation</html>")
            shortcutHelpText.border = BorderFactory.createEmptyBorder(5, 0, 0, 0)
            
            val shortcutInputPanel = JPanel(BorderLayout())
            shortcutInputPanel.add(JLabel("Shortcut:"), BorderLayout.WEST)
            shortcutInputPanel.add(shortcutComboBox, BorderLayout.CENTER)
            
            val shortcutPreviewPanel = JPanel(BorderLayout())
            shortcutPreviewPanel.add(JLabel("Preview:"), BorderLayout.WEST)
            shortcutPreviewPanel.add(shortcutPreviewLabel, BorderLayout.CENTER)
            
            val shortcutContentPanel = JPanel(BorderLayout(5, 5))
            shortcutContentPanel.add(shortcutInputPanel, BorderLayout.NORTH)
            shortcutContentPanel.add(shortcutPreviewPanel, BorderLayout.CENTER)
            shortcutContentPanel.add(shortcutHelpText, BorderLayout.SOUTH)
            
            shortcutPanel.add(shortcutContentPanel, BorderLayout.CENTER)
            
            // Add fixed shortcut information
            val removeShortcutLabel = JLabel("Remove Translation Shortcut (fixed): Shift + Control + D")
            removeShortcutLabel.border = BorderFactory.createEmptyBorder(10, 0, 0, 0) // Space at top
            shortcutPanel.add(removeShortcutLabel, BorderLayout.SOUTH)
            
            // Add panels to settings tab
            settingsPanel.add(functionPanel, BorderLayout.NORTH)
            settingsPanel.add(shortcutPanel, BorderLayout.CENTER)
            
            tabbedPane.addTab("General Settings", settingsPanel)
            
            // Auto Translate tab
            val autoTranslatePanel = createAutoTranslatePanel()
            tabbedPane.addTab("Auto Translate", autoTranslatePanel)
            
            panel.add(tabbedPane, BorderLayout.CENTER)
            
            logger.warn("UI components successfully created")
        } catch (e: Exception) {
            logger.warn("createComponent error occurred: ${e.message}", e)
            val errorLabel = JLabel("An error occurred while creating the UI. Please check the logs.")
            panel.add(errorLabel, BorderLayout.CENTER)
        }
        
        return panel
    }
    
    private fun createAutoTranslatePanel(): JPanel {
        val panel = JPanel(BorderLayout())
        panel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        
        // Main content panel
        val contentPanel = JPanel(GridBagLayout())
        val gbc = GridBagConstraints()
        
        // Auto Translate Enable/Disable
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST
        gbc.insets = Insets(0, 0, 10, 0)
        val enablePanel = JPanel()
        enablePanel.border = BorderFactory.createTitledBorder("Auto Translate")
        
        autoTranslateEnabledRadio = JRadioButton("Enabled", settings.autoTranslateEnabled)
        autoTranslateDisabledRadio = JRadioButton("Disabled", !settings.autoTranslateEnabled)
        
        val buttonGroup = ButtonGroup()
        buttonGroup.add(autoTranslateEnabledRadio)
        buttonGroup.add(autoTranslateDisabledRadio)
        
        enablePanel.add(autoTranslateEnabledRadio)
        enablePanel.add(autoTranslateDisabledRadio)
        contentPanel.add(enablePanel, gbc)
        
        // OpenAI API Key
        gbc.gridy++; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE
        gbc.insets = Insets(5, 0, 5, 5)
        contentPanel.add(JLabel("OpenAI API Key:"), gbc)
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0
        gbc.insets = Insets(5, 0, 5, 0)
        openAiApiKeyField = JTextField(settings.openAiApiKey, 30)
        contentPanel.add(openAiApiKeyField, gbc)
        
        // Test API Key button
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0
        gbc.insets = Insets(5, 5, 5, 0)
        val testApiButton = JButton("Test API Key")
        testApiButton.addActionListener { testOpenAiApiKey() }
        contentPanel.add(testApiButton, gbc)
        
        // Source Language File
        gbc.gridx = 0; gbc.gridy++; gbc.fill = GridBagConstraints.NONE
        gbc.insets = Insets(5, 0, 5, 5)
        contentPanel.add(JLabel("Source Language File:"), gbc)
        
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0
        gbc.insets = Insets(5, 0, 5, 0)
        sourceLanguageComboBox = JComboBox<String>()
        updateSourceLanguageOptions()
        contentPanel.add(sourceLanguageComboBox, gbc)
        
        // Language Files List
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.BOTH
        gbc.weightx = 1.0; gbc.weighty = 1.0
        gbc.insets = Insets(10, 0, 0, 0)
        
        val languageFilesPanel = JPanel(BorderLayout())
        languageFilesPanel.border = BorderFactory.createTitledBorder("Language Files")
        
        languageFilesList = JBList<LanguageFileEntry>()
        updateLanguageFilesList()
        
        val listScrollPane = JScrollPane(languageFilesList)
        languageFilesPanel.add(listScrollPane, BorderLayout.CENTER)
        
        val languageButtonPanel = JPanel()
        languageButtonPanel.add(JButton("Add Language").apply {
            addActionListener { addLanguageFile() }
        })
        languageButtonPanel.add(JButton("Edit Selected").apply {
            addActionListener { editSelectedLanguageFile() }
        })
        languageButtonPanel.add(JButton("Remove Selected").apply {
            addActionListener { removeSelectedLanguageFile() }
        })
        
        languageFilesPanel.add(languageButtonPanel, BorderLayout.SOUTH)
        contentPanel.add(languageFilesPanel, gbc)
        
        panel.add(contentPanel, BorderLayout.CENTER)
        
        // Help text
        val helpText = JLabel("<html><body>" +
                "<b>Auto Translate Configuration:</b><br>" +
                "1. Enable auto translate and enter your OpenAI API key<br>" +
                "2. Select the source language file (e.g., tr.json for Turkish)<br>" +
                "3. Configure language codes for each translation file<br>" +
                "4. When generating translation keys, text will be auto-translated to other languages" +
                "</body></html>")
        helpText.border = BorderFactory.createEmptyBorder(10, 0, 0, 0)
        panel.add(helpText, BorderLayout.SOUTH)
        
        return panel
    }
    
    private fun updateShortcutPreview() {
        try {
            val selectedShortcut = shortcutComboBox?.selectedItem as? String ?: settings.shortcutKey
            val displayText = settings.getShortcutDisplayText(selectedShortcut.ifBlank { "shift control T" })
            shortcutPreviewLabel?.text = displayText
            logger.warn("Shortcut preview updated: $selectedShortcut -> ${shortcutPreviewLabel?.text}")
        } catch (e: Exception) {
            logger.warn("Error updating shortcut preview: ${e.message}")
            shortcutPreviewLabel?.text = "Shift+Ctrl+T (default)"
        }
    }

    private fun createButtonPanel(): JPanel {
        return JPanel().apply {
            add(JButton("Add Path").apply {
                addActionListener { addRelativePath() }
            })
            add(JButton("Remove Selected").apply {
                addActionListener { removeSelectedPath() }
            })
            add(JButton("Validate All").apply {
                addActionListener { validateAllPaths() }
            })
        }
    }
    
    @Throws(ConfigurationException::class)
    override fun apply() {
        logger.warn("TranslationSettingsConfigurable.apply() called")
        
        try {
            // Update translation paths
            val listModel = translationPathsList?.model as? DefaultListModel<String>
            if (listModel != null) {
                val paths = mutableListOf<String>()
                for (i in 0 until listModel.size()) {
                    paths.add(listModel.getElementAt(i).toString().trim())
                }
                
                // If path list is empty, add the specified special path
                if (paths.isEmpty()) {
                    paths.add("wwwroot/languages/en.json")
                    logger.warn("Translation paths empty, default file path added: wwwroot/languages/en.json")
                }
                
                // Check if a specific file doesn't exist and add it automatically
                if (!paths.contains("wwwroot/languages/en.json")) {
                    paths.add("wwwroot/languages/en.json")
                    logger.warn("User-specified file path added: wwwroot/languages/en.json")
                }
                
                settings.translationFilePaths = paths
                logger.warn("Translation paths updated (${paths.size}): ${paths.joinToString()}")
            } else {
                // If list model is null, add the user-specified file path as default
                settings.translationFilePaths = mutableListOf("wwwroot/languages/en.json")
                logger.warn("List model null, default translation path added: wwwroot/languages/en.json")
            }
            
            // Update translation function
            translationFunctionField?.text?.trim()?.let {
                settings.translationFunction = it
                logger.warn("Translation function updated: $it")
            } ?: logger.warn("Translation function field null, couldn't update")
            
            // Update shortcut
            shortcutComboBox?.selectedItem?.let { selectedItem ->
                val shortcut = selectedItem.toString().trim()
                
                // If shortcut changed, activate it immediately
                val shortcutChanged = settings.shortcutKey != shortcut
                
                if (shortcutChanged) {
                    logger.warn("Shortcut changed: ${settings.shortcutKey} -> $shortcut")
                    
                    // First save settings
                    settings.shortcutKey = shortcut
                    
                    // Activate shortcut programmatically
                    try {
                        // Remove ALL shortcuts from keymaps and add new shortcut
                        com.ant.utils.ShortcutRegistrar.registerShortcuts()
                        logger.warn("Shortcuts instantly updated: $shortcut")
                        
                        // Change already applied in IDE keymap system
                        logger.warn("Keymap changes saved")
                    } catch (e: Exception) {
                        logger.warn("Error updating shortcuts programmatically: ${e.message}", e)
                    }
                }
            } ?: logger.warn("Shortcut combobox selection null, couldn't update shortcut")
            
            // Update Auto Translate settings
            settings.autoTranslateEnabled = autoTranslateEnabledRadio?.isSelected ?: false
            settings.openAiApiKey = openAiApiKeyField?.text?.trim() ?: ""
            settings.sourceLanguageFile = sourceLanguageComboBox?.selectedItem as? String ?: ""
            
            logger.warn("Auto Translate settings updated: enabled=${settings.autoTranslateEnabled}, sourceFile=${settings.sourceLanguageFile}")
            
            // Permanently save changes
            ApplicationManager.getApplication().saveSettings()
            logger.warn("Settings saved. Current state: ${settings.getState()}")
            
            // Inform user
            Messages.showInfoMessage(
                "Settings saved. Shortcuts have been activated immediately.\n\n" +
                "IMPORTANT: Please ensure that at least one of the translation files exists or can be created.\n" +
                "Example translation file path: 'locales/tr.json'",
                "Settings Saved"
            )
        } catch (e: Exception) {
            logger.warn("Error during apply: ${e.message}", e)
            throw ConfigurationException("An error occurred while applying settings: ${e.message}")
        }
    }

    override fun reset() {
        logger.warn("TranslationSettingsConfigurable.reset() called. Current shortcut: ${settings.shortcutKey}")
        
        try {
            // Reset translation paths
            val model = DefaultListModel<String>()
            if (settings.translationFilePaths.isNotEmpty()) {
                settings.translationFilePaths.forEach { path ->
                    if (path.isNotBlank()) {
                        model.addElement(path)
                    }
                }
                logger.warn("${settings.translationFilePaths.size} translation paths reset")
            } else {
                logger.warn("No translation paths found to reset")
            }
            translationPathsList?.model = model
            
            // Reset translation function
            val functionText = if (settings.translationFunction.isBlank()) "t" else settings.translationFunction
            translationFunctionField?.text = functionText
            logger.warn("Translation function reset: $functionText")
            
            // Reset shortcut
            val currentShortcut = if (settings.shortcutKey.isBlank()) "shift control T" else settings.shortcutKey
            val index = shortcutOptions.indexOf(currentShortcut)
            if (index >= 0) {
                shortcutComboBox?.selectedIndex = index
                logger.warn("Shortcut selection reset, index: $index")
            } else {
                shortcutComboBox?.selectedIndex = 0
                logger.warn("Shortcut selection reset to default")
            }
            
            // Update shortcut preview
            updateShortcutPreview()
            
            // Reset Auto Translate settings
            autoTranslateEnabledRadio?.isSelected = settings.autoTranslateEnabled
            autoTranslateDisabledRadio?.isSelected = !settings.autoTranslateEnabled
            openAiApiKeyField?.text = settings.openAiApiKey
            updateSourceLanguageOptions()
            updateLanguageFilesList()
            
            logger.warn("Settings reset: ${settings.getState()}")
        } catch (e: Exception) {
            logger.warn("Error resetting settings: ${e.message}", e)
            // Set default values in case of error
            try {
                translationFunctionField?.text = "t"
                shortcutComboBox?.selectedIndex = 0
                translationPathsList?.model = DefaultListModel<String>()
                updateShortcutPreview()
                logger.warn("Default values set after error")
            } catch (e2: Exception) {
                logger.warn("Error setting default values: ${e2.message}", e2)
            }
        }
    }

    override fun isModified(): Boolean {
        try {
            // Check translation paths
            val currentPaths = settings.translationFilePaths.toSet()
            val uiPathsModel = translationPathsList?.model as? DefaultListModel<String>
            val uiPaths = if (uiPathsModel != null) {
                val paths = mutableSetOf<String>()
                for (i in 0 until uiPathsModel.size()) {
                    paths.add(uiPathsModel.getElementAt(i))
                }
                paths
            } else {
                emptySet()
            }
            
            // Check ComboBox selection
            val selectedShortcut = shortcutComboBox?.selectedItem as? String
            
            val pathsChanged = currentPaths != uiPaths
            val shortcutChanged = settings.shortcutKey != selectedShortcut
            val functionChanged = settings.translationFunction != translationFunctionField?.text?.trim()
            
            // Check Auto Translate changes
            val autoTranslateChanged = settings.autoTranslateEnabled != (autoTranslateEnabledRadio?.isSelected ?: false)
            val apiKeyChanged = settings.openAiApiKey != (openAiApiKeyField?.text?.trim() ?: "")
            val sourceFileChanged = settings.sourceLanguageFile != (sourceLanguageComboBox?.selectedItem as? String ?: "")
            
            val result = pathsChanged || shortcutChanged || functionChanged || autoTranslateChanged || apiKeyChanged || sourceFileChanged
            logger.warn("TranslationSettingsConfigurable.isModified() = $result (paths: $pathsChanged, shortcut: $shortcutChanged, function: $functionChanged)")
            return result
        } catch (e: Exception) {
            logger.warn("isModified error: ${e.message}")
            return false
        }
    }
    
    private fun addRelativePath() {
        val inputValue = JOptionPane.showInputDialog(
            null,
            "Enter a relative path to the translation file (e.g., 'locales/en.json'):",
            "Add Translation File Path",
            JOptionPane.PLAIN_MESSAGE
        )
        
        if (inputValue != null && inputValue.isNotEmpty()) {
            // Check if the path is absolute and reject it
            val file = File(inputValue)
            if (file.isAbsolute) {
                Messages.showErrorDialog(
                    "Please enter a relative path (e.g., 'locales/en.json'). Absolute paths are not supported.",
                    "Invalid Path"
                )
                return
            }
            
            // Store the relative path as provided
            (translationPathsList?.model as? DefaultListModel<String>)?.addElement(inputValue)
            
            // Try to validate if any open project has this file
            val openProjects = ProjectManager.getInstance().openProjects
            var fileExists = false
            
            for (project in openProjects) {
                val projectPath = project.basePath
                if (projectPath != null) {
                    val fullPath = Paths.get(projectPath, inputValue).toString()
                    val projectFile = File(fullPath)
                    if (projectFile.exists() && projectFile.isFile) {
                        fileExists = true
                        val isValidJson = validateJsonFile(fullPath)
                        if (!isValidJson) {
                            Messages.showWarningDialog(
                                "The file at '$inputValue' exists but is not valid JSON. " +
                                "You can still add it, but translations might not work correctly.",
                                "Warning: Invalid JSON Format"
                            )
                        } else {
                            Messages.showInfoMessage(
                                "The path '$inputValue' was validated successfully in project '${project.name}'.",
                                "File Validation"
                            )
                        }
                        break
                    }
                }
            }
            
            if (!fileExists && openProjects.isNotEmpty()) {
                Messages.showWarningDialog(
                    "The file at '$inputValue' was not found in any open project. " +
                    "Make sure the path is correct relative to the project root.",
                    "Warning: File Not Found"
                )
            }
        }
    }

    private fun validateAllPaths() {
        val model = translationPathsList?.model as? DefaultListModel<String>
        if (model == null || model.size() == 0) {
            Messages.showInfoMessage("No translation paths are configured.", "Validation Result")
            return
        }
        
        var validPaths = 0
        var invalidPaths = 0
        var notFoundPaths = 0
        val openProjects = ProjectManager.getInstance().openProjects
        
        for (i in 0 until model.size()) {
            val path = model.getElementAt(i)
            
            // Check if this is an absolute path (which we now disallow)
            val file = File(path)
            if (file.isAbsolute) {
                Messages.showWarningDialog(
                    "The path '$path' is absolute. Please remove it and add a relative path instead.",
                    "Invalid Path Format"
                )
                continue
            }
            
            // For relative paths, check against all open projects
            var found = false
            for (project in openProjects) {
                val projectPath = project.basePath
                if (projectPath != null) {
                    val fullPath = Paths.get(projectPath, path).toString()
                    val projectFile = File(fullPath)
                    if (projectFile.exists() && projectFile.isFile) {
                        found = true
                        if (validateJsonFile(fullPath)) {
                            validPaths++
                        } else {
                            invalidPaths++
                        }
                        break
                    }
                }
            }
            
            if (!found) {
                notFoundPaths++
            }
        }
        
        val message = StringBuilder()
        message.append("Validation complete:\n")
        message.append("- Valid JSON files: $validPaths\n")
        if (invalidPaths > 0) message.append("- Invalid JSON files: $invalidPaths\n")
        if (notFoundPaths > 0) message.append("- Files not found: $notFoundPaths\n")
        
        if (notFoundPaths > 0) {
            message.append("\nNote: Make sure the paths are correct relative to your project root.")
        }
        
        Messages.showInfoMessage(message.toString(), "Validation Result")
    }

    private fun validateJsonFile(filePath: String): Boolean {
        val file = File(filePath)
        if (!file.exists() || !file.isFile) {
            return false
        }

        try {
            val content = file.readText()
            JSONObject(content)
            return true
        } catch (e: JSONException) {
            logger.warn("Invalid JSON in file: $filePath - ${e.message}")
            return false
        } catch (e: Exception) {
            logger.warn("Error reading file: $filePath - ${e.message}")
            return false
        }
    }

    private fun removeSelectedPath() {
        val selectedIndex = translationPathsList?.selectedIndex ?: return
        if (selectedIndex != -1) {
            (translationPathsList?.model as? DefaultListModel<String>)?.remove(selectedIndex)
        }
    }
    
    // Auto Translate helper methods
    private fun updateSourceLanguageOptions() {
        sourceLanguageComboBox?.removeAllItems()
        settings.translationFilePaths.forEach { path ->
            sourceLanguageComboBox?.addItem(path)
        }
        
        // Set current selection
        if (settings.sourceLanguageFile.isNotBlank()) {
            sourceLanguageComboBox?.selectedItem = settings.sourceLanguageFile
        }
    }
    
    private fun updateLanguageFilesList() {
        val model = DefaultListModel<LanguageFileEntry>()
        
        settings.translationFilePaths.forEach { filePath ->
            val languageCode = settings.languageFileLanguages[filePath] ?: ""
            val languageName = if (languageCode.isNotBlank()) {
                LanguageCodes.getLanguageName(languageCode)
            } else {
                "Not configured"
            }
            model.addElement(LanguageFileEntry(filePath, languageCode, languageName))
        }
        
        languageFilesList?.model = model
    }
    
    private fun testOpenAiApiKey() {
        val apiKey = openAiApiKeyField?.text?.trim() ?: ""
        if (apiKey.isBlank()) {
            Messages.showWarningDialog("Please enter an OpenAI API key first.", "API Key Required")
            return
        }
        
        try {
            val service = OpenAITranslationService(apiKey)
            val isValid = service.testApiKey()
            
            if (isValid) {
                Messages.showInfoMessage("API key is valid and working!", "API Key Test Successful")
            } else {
                Messages.showErrorDialog("API key test failed. Please check your key.", "API Key Test Failed")
            }
        } catch (e: Exception) {
            Messages.showErrorDialog("Error testing API key: ${e.message}", "API Key Test Error")
        }
    }
    
    private fun addLanguageFile() {
        if (settings.translationFilePaths.isEmpty()) {
            Messages.showWarningDialog("Please add translation files first in the 'Translation Files' tab.", "No Translation Files")
            return
        }
        
        // Show file selection dialog
        val availableFiles = settings.translationFilePaths.filter { path ->
            !settings.languageFileLanguages.containsKey(path)
        }
        
        if (availableFiles.isEmpty()) {
            Messages.showInfoMessage("All translation files already have language codes assigned.", "All Files Configured")
            return
        }
        
        val selectedFile = JOptionPane.showInputDialog(
            null,
            "Select a translation file:",
            "Add Language File",
            JOptionPane.QUESTION_MESSAGE,
            null,
            availableFiles.toTypedArray(),
            availableFiles[0]
        ) as? String
        
        if (selectedFile != null) {
            // Show language selection dialog
            val languageOptions = LanguageCodes.getLanguageOptions()
            val selectedLanguage = JOptionPane.showInputDialog(
                null,
                "Select the language for file '$selectedFile':",
                "Select Language",
                JOptionPane.QUESTION_MESSAGE,
                null,
                languageOptions,
                languageOptions[0]
            ) as? String
            
            if (selectedLanguage != null) {
                val languageCode = LanguageCodes.getCodeFromOption(selectedLanguage)
                settings.languageFileLanguages[selectedFile] = languageCode
                updateLanguageFilesList()
                updateSourceLanguageOptions()
            }
        }
    }
    
    private fun editSelectedLanguageFile() {
        val selectedEntry = languageFilesList?.selectedValue ?: return
        
        val languageOptions = LanguageCodes.getLanguageOptions()
        val currentOption = languageOptions.find { it.startsWith(selectedEntry.languageCode) }
        
        val selectedLanguage = JOptionPane.showInputDialog(
            null,
            "Select the language for file '${selectedEntry.filePath}':",
            "Edit Language",
            JOptionPane.QUESTION_MESSAGE,
            null,
            languageOptions,
            currentOption ?: languageOptions[0]
        ) as? String
        
        if (selectedLanguage != null) {
            val languageCode = LanguageCodes.getCodeFromOption(selectedLanguage)
            settings.languageFileLanguages[selectedEntry.filePath] = languageCode
            updateLanguageFilesList()
        }
    }
    
    private fun removeSelectedLanguageFile() {
        val selectedEntry = languageFilesList?.selectedValue ?: return
        settings.languageFileLanguages.remove(selectedEntry.filePath)
        updateLanguageFilesList()
        updateSourceLanguageOptions()
    }
} 