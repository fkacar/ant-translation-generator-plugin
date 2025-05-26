# Changelog

All notable changes to the Ant Translation Generator plugin will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2025-05-26

### Added
- **Initial Release** 🎉
- **Translation Key Generation**: Generate hierarchical translation keys from selected text
- **AI-Powered Auto Translation**: Integrate with OpenAI GPT-4 for automatic translations
- **Multi-Language File Management**: Support for multiple translation files simultaneously
- **Smart Key Management**: Remove translation keys and restore original text
- **Keyboard Shortcuts**: 
  - Generate Translation Key: `Shift + Ctrl + T`
  - Remove Translation Key: `Shift + Ctrl + D`
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
- **File Type Support**: JavaScript, TypeScript, JSX, TSX, Vue, PHP, HTML, Kotlin, and more
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

### Planned Features for v1.1.0
- [ ] Batch translation support
- [ ] Translation memory integration
- [ ] Custom translation providers
- [ ] Advanced key filtering
- [ ] Translation statistics
- [ ] Export/import functionality

### Planned Features for v1.2.0
- [ ] Translation validation
- [ ] Pluralization support
- [ ] Context-aware translations
- [ ] Translation comments
- [ ] Team collaboration features

---

## Support

For bug reports, feature requests, or questions:
- **GitHub Issues**: [Report an issue](https://github.com/fatihkacar/ant-translation-generator-plugin/issues)
- **Email**: info@fatihkacar.com
- **Documentation**: [README.md](README.md)

---

**Note**: This changelog will be updated with each release. Please check back for the latest changes and improvements. 