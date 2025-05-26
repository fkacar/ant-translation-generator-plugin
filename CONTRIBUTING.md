# 🤝 Contributing to Ant Translation Generator

Thank you for your interest in contributing to the Ant Translation Generator! We welcome contributions from the community and are excited to see what you can bring to this project.

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [How to Contribute](#how-to-contribute)
- [Pull Request Process](#pull-request-process)
- [Coding Standards](#coding-standards)
- [Testing Guidelines](#testing-guidelines)
- [Issue Reporting](#issue-reporting)
- [Feature Requests](#feature-requests)
- [Documentation](#documentation)
- [Community](#community)

## 📜 Code of Conduct

This project adheres to a code of conduct that we expect all contributors to follow. Please read and follow our [Code of Conduct](CODE_OF_CONDUCT.md) to help us maintain a welcoming and inclusive community.

### Our Standards

- **Be respectful**: Treat everyone with respect and kindness
- **Be inclusive**: Welcome newcomers and help them get started
- **Be constructive**: Provide helpful feedback and suggestions
- **Be patient**: Remember that everyone has different skill levels
- **Be collaborative**: Work together towards common goals

## 🚀 Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:

- **Java 17+** (OpenJDK recommended)
- **IntelliJ IDEA** (Community or Ultimate edition)
- **Git** for version control
- **Gradle** (included with IntelliJ IDEA)

### Recommended Tools

- **JetBrains Rider** (for testing .NET compatibility)
- **WebStorm** (for testing web development features)
- **Postman** or similar tool (for testing OpenAI API integration)

## 🛠️ Development Setup

### 1. Fork and Clone

```bash
# Fork the repository on GitHub
# Then clone your fork
git clone https://github.com/YOUR_USERNAME/ant-translation-generator.git
cd ant-translation-generator

# Add upstream remote
git remote add upstream https://github.com/fatihkacar/ant-translation-generator.git
```

### 2. Import Project

1. Open IntelliJ IDEA
2. Select "Open" and choose the project directory
3. Wait for Gradle to sync and download dependencies
4. Ensure Project SDK is set to Java 17+

### 3. Run the Plugin

```bash
# Run the plugin in a development IDE instance
./gradlew runIde

# Build the plugin
./gradlew buildPlugin

# Run tests
./gradlew test

# Verify plugin compatibility
./gradlew verifyPlugin
```

### 4. Project Structure

```
src/
├── main/
│   ├── kotlin/com/ant/
│   │   ├── actions/           # Action classes for menu items
│   │   ├── generator/         # Core translation generation logic
│   │   ├── services/          # OpenAI and other services
│   │   ├── utils/             # Utility classes
│   │   ├── TranslationSettings.kt
│   │   └── TranslationSettingsConfigurable.kt
│   └── resources/
│       └── META-INF/
│           └── plugin.xml     # Plugin configuration
└── test/                      # Test files
```

## 🎯 How to Contribute

### Types of Contributions

We welcome various types of contributions:

1. **🐛 Bug Fixes**: Fix existing issues
2. **✨ New Features**: Add new functionality
3. **📚 Documentation**: Improve docs and examples
4. **🧪 Tests**: Add or improve test coverage
5. **🎨 UI/UX**: Enhance user interface and experience
6. **🌐 Translations**: Add support for new languages
7. **⚡ Performance**: Optimize existing code

### Finding Issues to Work On

- Check the [Issues](https://github.com/fatihkacar/ant-translation-generator/issues) page
- Look for issues labeled `good first issue` for beginners
- Issues labeled `help wanted` are great for contributors
- Feel free to create new issues for bugs or feature requests

## 🔄 Pull Request Process

### 1. Create a Branch

```bash
# Update your fork
git checkout main
git pull upstream main

# Create a new branch
git checkout -b feature/your-feature-name
# or
git checkout -b fix/issue-number-description
```

### 2. Make Changes

- Write clean, readable code
- Follow the existing code style
- Add tests for new functionality
- Update documentation as needed
- Ensure all tests pass

### 3. Commit Changes

```bash
# Stage your changes
git add .

# Commit with a descriptive message
git commit -m "feat: add batch translation support

- Implement batch translation for multiple keys
- Add progress indicator for large operations
- Update settings UI to include batch options
- Add tests for batch functionality

Closes #123"
```

### 4. Push and Create PR

```bash
# Push to your fork
git push origin feature/your-feature-name

# Create a Pull Request on GitHub
# Fill out the PR template with details
```

### 5. PR Review Process

- Maintainers will review your PR
- Address any feedback or requested changes
- Once approved, your PR will be merged
- Your contribution will be credited in the changelog

## 📝 Coding Standards

### Kotlin Style Guide

Follow the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html):

```kotlin
// ✅ Good
class TranslationGenerator(private val project: Project) {
    private val logger = getLogger(javaClass)
    
    fun generateTranslationKey(editor: Editor, text: String): String {
        return toCamelCase(text)
    }
    
    private fun toCamelCase(text: String): String {
        // Implementation
    }
}

// ❌ Avoid
class translationGenerator(project: Project) {
    val logger = getLogger(javaClass)
    
    fun generateTranslationKey(editor: Editor,text: String): String{
        return toCamelCase(text)
    }
}
```

### Code Organization

- **Single Responsibility**: Each class should have one clear purpose
- **Dependency Injection**: Use constructor injection where possible
- **Error Handling**: Always handle exceptions gracefully
- **Logging**: Use appropriate log levels (info, warn, error)
- **Documentation**: Add KDoc for public APIs

### Naming Conventions

- **Classes**: PascalCase (`TranslationGenerator`)
- **Functions**: camelCase (`generateTranslationKey`)
- **Variables**: camelCase (`translationFiles`)
- **Constants**: UPPER_SNAKE_CASE (`DEFAULT_FUNCTION_NAME`)
- **Files**: PascalCase (`TranslationGenerator.kt`)

## 🧪 Testing Guidelines

### Writing Tests

```kotlin
class TranslationGeneratorTest {
    @Test
    fun `should generate camelCase key from text`() {
        // Given
        val generator = TranslationGenerator(mockProject)
        val inputText = "Hello World"
        
        // When
        val result = generator.toCamelCase(inputText)
        
        // Then
        assertEquals("helloWorld", result)
    }
    
    @Test
    fun `should handle empty text gracefully`() {
        // Given
        val generator = TranslationGenerator(mockProject)
        
        // When
        val result = generator.toCamelCase("")
        
        // Then
        assertEquals("emptyText", result)
    }
}
```

### Test Categories

- **Unit Tests**: Test individual functions and classes
- **Integration Tests**: Test component interactions
- **UI Tests**: Test user interface components
- **API Tests**: Test OpenAI integration (with mocks)

### Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "TranslationGeneratorTest"

# Run tests with coverage
./gradlew test jacocoTestReport
```

## 🐛 Issue Reporting

### Before Creating an Issue

1. **Search existing issues** to avoid duplicates
2. **Check the documentation** for solutions
3. **Try the latest version** to see if it's already fixed
4. **Gather relevant information** about your environment

### Bug Report Template

```markdown
**Bug Description**
A clear description of what the bug is.

**Steps to Reproduce**
1. Go to '...'
2. Click on '...'
3. Select text '...'
4. See error

**Expected Behavior**
What you expected to happen.

**Actual Behavior**
What actually happened.

**Environment**
- IDE: IntelliJ IDEA 2023.2
- Plugin Version: 1.0.0
- OS: macOS 14.0
- Java Version: 17.0.8

**Screenshots**
If applicable, add screenshots.

**Additional Context**
Any other context about the problem.
```

## 💡 Feature Requests

### Feature Request Template

```markdown
**Feature Description**
A clear description of the feature you'd like to see.

**Problem Statement**
What problem does this feature solve?

**Proposed Solution**
How would you like this feature to work?

**Alternatives Considered**
Other solutions you've considered.

**Additional Context**
Any other context, mockups, or examples.
```

### Feature Development Process

1. **Discussion**: Create an issue to discuss the feature
2. **Design**: Plan the implementation approach
3. **Implementation**: Develop the feature
4. **Testing**: Ensure quality and compatibility
5. **Documentation**: Update relevant docs
6. **Review**: Get feedback from maintainers

## 📚 Documentation

### Types of Documentation

- **Code Comments**: Explain complex logic
- **KDoc**: Document public APIs
- **README**: User-facing documentation
- **Wiki**: Detailed guides and tutorials
- **Changelog**: Track version changes

### Documentation Standards

```kotlin
/**
 * Generates translation keys from selected text with hierarchical structure.
 * 
 * This function takes the selected text and converts it to a camelCase key,
 * then adds it to all configured translation files with proper nesting
 * based on the file path structure.
 * 
 * @param editor The current editor instance
 * @param actionId The ID of the action that triggered this generation
 * @throws IllegalStateException if no translation files are configured
 * @see TranslationSettings.translationFilePaths
 * @since 1.0.0
 */
fun generateTranslationKey(editor: Editor, actionId: String = "GenerateTranslationKey") {
    // Implementation
}
```

## 🌟 Recognition

### Contributors

All contributors will be recognized in:

- **README.md**: Contributors section
- **CHANGELOG.md**: Version release notes
- **GitHub**: Contributor graphs and statistics
- **Plugin Description**: Acknowledgments section

### Types of Recognition

- **Code Contributors**: Direct code contributions
- **Documentation Contributors**: Improve docs and guides
- **Bug Reporters**: Help identify and fix issues
- **Feature Requesters**: Suggest valuable improvements
- **Community Helpers**: Answer questions and help users

## 🎯 Roadmap

### Current Priorities

1. **Performance Optimization**: Improve translation generation speed
2. **UI/UX Enhancements**: Better user experience
3. **Additional Language Support**: More programming languages
4. **Translation Providers**: Support for other translation services
5. **Team Collaboration**: Multi-developer workflow features

### Future Goals

- **Translation Memory**: Reuse previous translations
- **Batch Operations**: Handle multiple files at once
- **Advanced Validation**: Check translation completeness
- **Integration**: Support for popular i18n frameworks
- **Analytics**: Usage statistics and insights

## 📞 Community

### Getting Help

- **GitHub Issues**: For bugs and feature requests
- **Discussions**: For questions and general discussion
- **Email**: info@fatihkacar.com for direct contact
- **Documentation**: Check README and wiki first

### Communication Guidelines

- **Be specific**: Provide clear details and examples
- **Be patient**: Maintainers are volunteers with limited time
- **Be helpful**: Help others when you can
- **Be respectful**: Follow the code of conduct

### Maintainers

- **Fatih Kacar** ([@fatihkacar](https://github.com/fatihkacar)) - Project Lead

---

## 🙏 Thank You

Thank you for contributing to the Ant Translation Generator! Your contributions help make internationalization easier for developers worldwide. Every contribution, no matter how small, is valuable and appreciated.

**Happy coding! 🚀** 