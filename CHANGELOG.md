# Changelog

All notable changes to the Ant Translation Generator plugin will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).


## [1.1.5] - 2025-05-29

### Changed - VS Code Extension
- 🎨 **Images** - README images and descriptions
- **Docs** - Some fixes for misspeling and wrong info

## [1.1.3] - 2025-05-28

### Added - VS Code Extension
- 🎨 **Images** - Added images

## [1.1.2] - 2025-05-28

### Changed - VS Code Extension
- 🎨 **Updated Repository Links** - Changed wrong gihub repository links

## [1.1.1] - 2025-05-28

### Changed - VS Code Extension
- 🎨 **Updated Extension Icon** - Improved visual design and marketplace presentation

### IntelliJ Plugin
- No changes made to IntelliJ plugin in this release

## [1.1.0] - 2025-05-28 - VS Code Extension Release 🚀

### Added - VS Code Extension
- 🚀 **Complete VS Code Extension** - Full port of IntelliJ IDEA plugin to Visual Studio Code
- ⌨️ **Built-in Keyboard Shortcuts** - Platform-specific shortcuts:
  - Generate: `Cmd+Alt+T` (macOS) / `Ctrl+Alt+T` (Windows/Linux)
  - Remove: `Cmd+Alt+R` (macOS) / `Ctrl+Alt+R` (Windows/Linux)
- 📁 **Multi-Language File Management** - Support for multiple translation files with project-relative paths
- 🤖 **Enhanced OpenAI GPT-4 Integration** - Improved automatic translation with response cleaning
- 🎯 **Smart Hierarchical Key Generation** - Automatic "components.pages" prefix with camelCase conversion
- 🔧 **Modern Settings Webview** - Configuration panel in Explorer sidebar
- 💡 **Hover Information** - View translation values by hovering over keys
- 📝 **Context Menu Integration** - Right-click actions for selected text

### Features - VS Code Extension
- **Advanced Key Generation**: 
  - File path-based hierarchical structure (e.g., `components.pages.public.dashboard.welcomeMessage`)
  - PascalCase to camelCase conversion (DashboardMain → dashboardMain)
  - Smart text processing and sanitization
- **Improved Translation Removal**: Restore original text from configurable source language file
- **Enhanced Auto Translation**: 
  - Cleaned OpenAI responses (removes unwanted quotes and escape characters)
  - 70+ target languages support
  - Configurable source language file
- **Professional File Management**: 
  - Project-relative path support
  - Language code mapping for each file
  - Settings validation and error handling

### Fixed - VS Code Extension
- 🐛 **Path Segment Generation** - Fixed camelCase conversion and added automatic "components.pages" prefix
- 🐛 **OpenAI Response Cleaning** - Removed unwanted quotes and escape characters from translations
- 🐛 **Remove Key Functionality** - Now correctly uses sourceLanguageFile for text restoration
- 🐛 **Keyboard Shortcuts** - Simplified to built-in VS Code keybindings for maximum reliability
- 🐛 **Settings Configuration** - Improved validation and error handling

### Technical - VS Code Extension
- **TypeScript Implementation** - Full TypeScript codebase with proper type definitions
- **VS Code API Integration** - Native VS Code extension architecture
- **Comprehensive Error Handling** - Detailed error handling and user feedback
- **Debug Logging** - Extensive logging for troubleshooting
- **Modular Architecture** - Separate services for translation, settings, and OpenAI integration

### Supported Platforms
- **IntelliJ IDEA Family** - IntelliJ IDEA, WebStorm, PhpStorm, Rider, PyCharm, etc.
- **Visual Studio Code** - Cross-platform support (Windows, macOS, Linux)

## [1.0.1] - 2025-05-27 - IntelliJ Plugin

### Fixed
- **Plugin.xml**: Removed illegal chars

### Added
- **README.md**: Added razor to supported file types

## [1.0.0] - 2025-05-26 - IntelliJ Plugin Initial Release

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

### Documentation
- Comprehensive README with usage examples
- Installation and configuration guide
- Troubleshooting section
- API integration documentation
- Contributing guidelines

---

## Future Releases

### Planned Features for v1.2.0
- [ ] **Batch Translation Support** - Process multiple files at once
- [ ] **Translation Statistics** - Analytics and usage reports
- [ ] **Key Search & Navigation** - Find and navigate translation keys across projects
- [ ] **Mobile Framework Integration** - React Native and mobile framework support
- [ ] **Custom Themes** - Customizable interface themes
- [ ] **Library Integration** - Direct integration with popular i18n libraries

### Planned Features for v1.3.0
- [ ] **Translation Validation** - Validate translations for completeness and consistency
- [ ] **Pluralization Support** - Handle plural forms in translations
- [ ] **Context-Aware Translations** - Smart translations based on code context
- [ ] **Translation Comments** - Add comments and context for translators
- [ ] **Team Collaboration** - Multi-user translation workflows
- [ ] **Import/Export** - Bulk translation file operations
- [ ] **Translation Service Sync** - Integration with external translation services

---

## Platform-Specific Information

### IntelliJ IDEA Plugin
- **Location**: `intellij-ides/` directory
- **Installation**: JetBrains Marketplace or manual installation
- **Language**: Kotlin
- **Build**: Gradle

### VS Code Extension
- **Location**: `visual-studio-code/` directory  
- **Installation**: VS Code Marketplace or manual installation
- **Language**: TypeScript
- **Build**: npm/Node.js

---

## Support

For bug reports, feature requests, or questions:
- **GitHub Issues**: [Report an issue](https://github.com/fkacar/ant-translation-generator-plugin/issues)
- **Email**: info@fatihkacar.com
- **Documentation**: [README.md](README.md)
- **IntelliJ Plugin**: [intellij-ides/README.md](intellij-ides/README.md)
- **VS Code Extension**: [visual-studio-code/README.md](visual-studio-code/README.md)

---

**Note**: This project now supports both IntelliJ IDEA family IDEs and Visual Studio Code. Each platform has its own dedicated directory and documentation. Please check the platform-specific README files for detailed installation and usage instructions. 