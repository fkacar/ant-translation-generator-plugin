# Change Log

All notable changes to the "ant-translation-generator" extension will be documented in this file.

## [1.1.5] - 2025-05-29

### Changed
- 🎨 **Images** - README images and descriptions
- **Docs** - Some fixes for misspeling and wrong info

## [1.1.3] - 2025-05-28

### Added
- 🎨 **Images** - Added images

## [1.1.2] - 2025-05-28

### Changed
- 🎨 **Updated Repository Links** - Changed wrong gihub repository links

## [1.1.1] - 2025-05-28

### Changed
- 🎨 **Updated Extension Icon** - Improved visual design and marketplace presentation

## [1.1.0] - 2025-05-28

### Added
- 🚀 **Complete VS Code Extension** - Full port of IntelliJ IDEA plugin to VS Code
- ⌨️ **Built-in Keyboard Shortcuts** - Platform-specific shortcuts (Cmd+Alt+T/Ctrl+Alt+T for generate, Cmd+Alt+R/Ctrl+Alt+R for remove)
- 📁 **Multi-Language File Management** - Support for multiple translation files with project-relative paths
- 🤖 **OpenAI GPT-4 Integration** - Automatic translation to 70+ languages with improved response cleaning
- 🎯 **Smart Hierarchical Key Generation** - Automatic "components.pages" prefix with camelCase conversion
- 🔧 **Settings Webview Interface** - Modern configuration panel in Explorer sidebar
- 🌍 **70+ Language Support** - Complete ISO 639-1 language code support
- 📝 **Context Menu Integration** - Right-click actions for selected text
- 💡 **Hover Information** - View translation values by hovering over keys
- 🔒 **Secure API Key Storage** - Safe OpenAI API key management

### Features
- **Translation Key Generation**: Convert selected text to hierarchical translation keys
  - Automatic file path-based key structure (e.g., `components.pages.public.dashboard.welcomeMessage`)
  - PascalCase to camelCase conversion (DashboardMain → dashboardMain)
  - Smart text processing and key sanitization
- **Translation Key Removal**: Remove keys and restore original text from source language file
- **Auto Translation**: OpenAI GPT-4 powered automatic translation
  - Improved response cleaning (removes unwanted quotes and escape characters)
  - Support for 70+ target languages
  - Configurable source language file
- **File Management**: 
  - Add/remove translation files through settings panel
  - Project-relative path support
  - Language code mapping for each file
- **Customization**:
  - Configurable translation function name (t, i18n, $t, etc.)
  - Source language file selection
  - Translation file paths and language mappings

### Fixed
- 🐛 **Path Segment Generation** - Fixed camelCase conversion and added automatic "components.pages" prefix
- 🐛 **OpenAI Response Cleaning** - Removed unwanted quotes and escape characters from translations
- 🐛 **Remove Key Functionality** - Now correctly uses sourceLanguageFile for text restoration
- 🐛 **Keyboard Shortcuts** - Simplified to built-in VS Code keybindings for maximum reliability
- 🐛 **Settings Configuration** - Improved validation and error handling

### Technical Improvements
- **TypeScript Implementation** - Full TypeScript codebase with proper type definitions
- **VS Code API Integration** - Native VS Code extension architecture
- **Error Handling** - Comprehensive error handling and user feedback
- **Debug Logging** - Detailed logging for troubleshooting
- **Code Organization** - Modular architecture with separate services

### Supported File Types
- JavaScript (.js, .jsx)
- TypeScript (.ts, .tsx)
- Vue (.vue)
- HTML (.html)
- PHP (.php)
- Razor (.razor)
- And more...

### Supported Languages
Complete support for 70+ languages including:
- **European**: English (en), Turkish (tr), Spanish (es), French (fr), German (de), Italian (it), Portuguese (pt), Russian (ru), Dutch (nl), Polish (pl)
- **Asian**: Japanese (ja), Korean (ko), Chinese (zh), Arabic (ar), Hindi (hi), Thai (th), Vietnamese (vi)
- **Nordic**: Swedish (sv), Norwegian (no), Danish (da), Finnish (fi)
- **And 50+ more languages...**

## [Unreleased]

### Planned Features
- 🔄 **Batch Translation Operations** - Process multiple files at once
- 📈 **Translation Statistics** - Analytics and usage reports
- 🔍 **Key Search & Navigation** - Find and navigate translation keys
- 📱 **Mobile Integration** - React Native and mobile framework support
- 🎨 **Custom Themes** - Customizable settings panel themes
- 🔗 **Library Integration** - Direct integration with popular i18n libraries
- 📋 **Import/Export** - Bulk translation file operations
- 🔄 **Translation Service Sync** - Integration with external translation services
- 📝 **Translation Notes** - Comments and context for translators
- 🎯 **Smart Suggestions** - AI-powered translation key suggestions 