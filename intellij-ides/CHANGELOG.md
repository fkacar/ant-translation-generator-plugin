# Change Log - IntelliJ IDEA Plugin

All notable changes to the Ant Translation Generator IntelliJ IDEA plugin will be documented in this file.

## [1.1.5] - 2025-05-29

### IntelliJ Plugin
- No changes made to IntelliJ plugin in this release

## [1.1.3] - 2025-05-28

### IntelliJ Plugin
- No changes made to IntelliJ plugin in this release

## [1.1.2] - 2025-05-28

### IntelliJ Plugin
- No changes made to IntelliJ plugin in this release

## [1.1.1] - 2025-05-28

### IntelliJ Plugin
- No changes made to IntelliJ plugin in this release

### Project Updates
- 🎨 **VS Code Extension**: Updated extension icon and publisher configuration
- 📋 **Documentation**: Synchronized version history across platforms

## [1.1.0] - 2025-05-28

### IntelliJ Plugin
- No changes made to IntelliJ plugin in this release

### Project Updates
- 🚀 **VS Code Extension Release**: Complete VS Code extension with all IntelliJ plugin features
- 📁 **Multi-Platform Support**: Project now supports both IntelliJ IDEA family and VS Code
- 🔧 **Enhanced Features**: Improved OpenAI integration and keyboard shortcuts for VS Code

## [1.0.1] - 2025-05-27

### Fixed
- **Plugin.xml**: Removed illegal chars

### Added
- **README.md**: Added razor to supported file types

## [1.0.0] - 2025-05-26

### Added
- **Initial Release** 🎉
- **Translation Key Generation**: Generate hierarchical translation keys from selected text
- **AI-Powered Auto Translation**: Integrate with OpenAI GPT-4 for automatic translations
- **Multi-Language File Management**: Support for multiple translation files simultaneously
- **Smart Key Management**: Remove translation keys and restore original text
- **Keyboard Shortcuts**: 
  - Generate Translation Key: `Shift + Ctrl + T`
  - Remove Translation Key: `Shift + Alt + R`
- **Context Menu Integration**: Right-click menu options for translation actions
- **Customizable Settings**: Configure translation function names and file paths
- **Language Support**: 70+ languages with ISO 639-1 language codes
- **JSON Validation**: Automatic JSON formatting and validation
- **Project-Relative Paths**: Support for project-relative translation file paths
- **Hierarchical Key Structure**: Automatic key generation based on file structure
- **CamelCase Conversion**: Smart text-to-key conversion
- **Real-time Validation**: File existence and JSON syntax validation
- **OpenAI API Integration**: Secure API key storage and testing
- **Source Language Configuration**: Set primary language file for translations
- **Language Code Mapping**: Assign language codes to translation files
- **Error Handling**: Comprehensive error handling and user notifications
- **IDE Compatibility**: Support for IntelliJ IDEA, WebStorm, PhpStorm, Rider, and more

### Features
- **Instant Translation**: Select text and generate translation keys with one keystroke
- **Auto-Translate**: Automatically translate to multiple languages using AI
- **Smart Fallback**: Use original text if translation fails
- **Nested JSON Support**: Create and manage nested JSON structures
- **File Type Support**: JavaScript, TypeScript, JSX, TSX, Vue, PHP, HTML, Kotlin, Razor, and more
- **Cross-Platform**: Works on Windows, macOS, and Linux
- **Secure**: Local API key storage, no data collection
- **Open Source**: Full source code available for review

### Technical Details
- **Minimum IDE Version**: 2023.1 (build 231)
- **Maximum IDE Version**: 2024.1.* (build 241.*)
- **Dependencies**: IntelliJ Platform SDK
- **Language**: Kotlin
- **Build System**: Gradle
- **License**: MIT

---

## Future Releases

### Planned Features for IntelliJ Plugin v1.2.0
- [ ] **Enhanced UI**: Improved settings panel design
- [ ] **Performance Optimization**: Faster key generation and file processing
- [ ] **Advanced Key Management**: Batch operations and key refactoring
- [ ] **Integration Improvements**: Better IDE integration and workflow

### Cross-Platform Features (Shared with VS Code)
- [ ] **Batch Translation Support** - Process multiple files at once
- [ ] **Translation Statistics** - Analytics and usage reports
- [ ] **Key Search & Navigation** - Find and navigate translation keys
- [ ] **Translation Validation** - Validate translations for completeness
- [ ] **Team Collaboration** - Multi-user translation workflows

---

**Note**: This IntelliJ plugin is part of a multi-platform project that also includes a VS Code extension. For overall project changes and cross-platform features, see the main [CHANGELOG.md](../CHANGELOG.md) file. 