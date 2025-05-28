# 🌍 Ant Translation Generator

[![JetBrains Plugin](https://img.shields.io/badge/JetBrains-Plugin-orange.svg)](https://plugins.jetbrains.com/)
[![VS Code Extension](https://img.shields.io/badge/VS%20Code-Extension-blue.svg)](https://marketplace.visualstudio.com/)
[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)](https://github.com/fatihkacar/ant-translation-generator)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

**Ant Translation Generator** is a powerful internationalization (i18n) tool available for both **JetBrains IDEs** and **Visual Studio Code**. Generate translation keys instantly, manage multiple language files, and leverage AI-powered auto-translation with OpenAI GPT-4.

## 🚀 Available Platforms

### 🔧 JetBrains IDEs Plugin
**For IntelliJ IDEA, WebStorm, PhpStorm, PyCharm, Rider, and more**

[![JetBrains Plugin](https://img.shields.io/badge/Install-JetBrains%20Plugin-orange.svg)](https://plugins.jetbrains.com/)

- Full-featured plugin with comprehensive settings panel
- Advanced Kotlin-based implementation
- Supports all JetBrains IDEs (2023.1+)
- [📖 View JetBrains Plugin Documentation](intellij-ides/README.md)

### 💻 Visual Studio Code Extension
**For Visual Studio Code**

[![VS Code Extension](https://img.shields.io/badge/Install-VS%20Code%20Extension-blue.svg)](https://marketplace.visualstudio.com/)

- Modern TypeScript implementation
- Webview-based settings interface
- Optimized for VS Code workflow
- [📖 View VS Code Extension Documentation](visual-studio-code/README.md)

## ✨ Core Features

### 🚀 **Instant Translation Key Generation**
- Select any text in your code and generate translation keys with a single keystroke
- Automatically creates hierarchical keys based on your file structure
- Supports nested JSON structure for organized translations
- Customizable translation function names (t, i18n, $t, etc.)

### 🤖 **AI-Powered Auto Translation**
- Integrate with OpenAI GPT-4 for automatic translations
- Set a source language file and auto-translate to target languages
- Supports 70+ languages with ISO 639-1 language codes
- Smart fallback to original text if translation fails

### 📁 **Multi-Language File Management**
- Support for multiple translation files simultaneously
- Project-relative path configuration
- Automatic JSON validation and formatting
- Real-time file existence validation

### ⌨️ **Keyboard Shortcuts & Context Menu**
- **Generate Translation Key**: `Shift + Ctrl + T` (Windows/Linux) / `Shift + Cmd + T` (macOS)
- **Remove Translation Key**: `Shift + Ctrl + D` (Windows/Linux) / `Shift + Cmd + D` (macOS)
- Right-click context menu integration
- Customizable keyboard shortcuts

### 🎯 **Smart Key Management**
- Remove translation keys and restore original text
- Hierarchical key structure based on file paths
- CamelCase key generation from selected text
- Duplicate key prevention

## 🛠️ Quick Installation

### JetBrains IDEs
```bash
# Via IDE
File → Settings → Plugins → Search "Ant Translation Generator"

# Or download from marketplace
https://plugins.jetbrains.com/
```

### Visual Studio Code
```bash
# Via VS Code
Ctrl+Shift+X → Search "Ant Translation Generator"

# Or via command line
code --install-extension ant.ant-translation-generator
```

## 🎮 Usage Example

### Before
```javascript
const message = "Welcome to our application";
const title = "User Profile Settings";
const button = "Save Changes";
```

### After (Shift+Ctrl+T / Shift+Cmd+T)
```javascript
const message = t('components.pages.welcome.welcomeToOurApplication');
const title = t('components.pages.profile.userProfileSettings');
const button = t('common.actions.saveChanges');
```

### Generated Translation Files

**en.json**:
```json
{
  "components": {
    "pages": {
      "welcome": {
        "welcomeToOurApplication": "Welcome to our application"
      },
      "profile": {
        "userProfileSettings": "User Profile Settings"
      }
    }
  },
  "common": {
    "actions": {
      "saveChanges": "Save Changes"
    }
  }
}
```

**tr.json** (with auto-translate):
```json
{
  "components": {
    "pages": {
      "welcome": {
        "welcomeToOurApplication": "Uygulamamıza hoş geldiniz"
      },
      "profile": {
        "userProfileSettings": "Kullanıcı Profil Ayarları"
      }
    }
  },
  "common": {
    "actions": {
      "saveChanges": "Değişiklikleri Kaydet"
    }
  }
}
```

## 🌐 Supported Languages

Both platforms support 70+ languages including:

| Code | Language | Code | Language | Code | Language |
|------|----------|------|----------|------|----------|
| en | English | tr | Turkish | de | German |
| fr | French | es | Spanish | it | Italian |
| pt | Portuguese | ru | Russian | ja | Japanese |
| ko | Korean | zh | Chinese | ar | Arabic |
| hi | Hindi | nl | Dutch | sv | Swedish |
| da | Danish | no | Norwegian | fi | Finnish |

[View full language list →](intellij-ides/src/main/kotlin/com/ant/utils/LanguageCodes.kt)

## 🔧 Supported Environments

### JetBrains IDEs
- IntelliJ IDEA (Ultimate & Community)
- WebStorm
- PhpStorm
- PyCharm
- Rider
- Android Studio
- All JetBrains IDEs (2023.1+)

### Visual Studio Code
- VS Code 1.74.0+
- VS Code Insiders
- Code - OSS
- All VS Code compatible editors

## 📋 Supported File Types

- JavaScript (.js)
- TypeScript (.ts)
- JSX (.jsx)
- TSX (.tsx)
- Vue (.vue)
- PHP (.php)
- HTML (.html)
- Kotlin (.kt)
- Razor (.razor)
- And more...

## 🎯 Use Cases

### Frontend Development
- **React/Vue/Angular** applications
- **Next.js/Nuxt.js** projects
- **Mobile development** (React Native, Flutter)
- **Progressive Web Apps** (PWA)

### Backend Development
- **Node.js** applications
- **PHP** applications
- **.NET** applications (with Rider)
- **Python** applications (with PyCharm)

### Multi-platform Projects
- **Electron** applications
- **Hybrid mobile** applications
- **Desktop** applications

## 🔒 Privacy & Security

- **🔐 API Key Security**: Your OpenAI API key is stored locally and never shared
- **💻 Local Processing**: All file operations are performed locally
- **🚫 No Data Collection**: Neither plugin collects or transmits any personal data
- **📖 Open Source**: Full source code is available for review

## 📁 Project Structure

```
ant-translation-generator-plugin/
├── intellij-ides/              # JetBrains IDEs Plugin
│   ├── src/main/kotlin/        # Kotlin source code
│   ├── src/main/resources/     # Plugin resources
│   ├── build.gradle.kts        # Gradle build script
│   └── README.md              # JetBrains plugin documentation
├── visual-studio-code/        # VS Code Extension
│   ├── src/                   # TypeScript source code
│   ├── package.json           # Extension manifest
│   ├── tsconfig.json          # TypeScript configuration
│   └── README.md              # VS Code extension documentation
├── LICENSE                    # MIT License
├── CONTRIBUTING.md            # Contributing guidelines
└── README.md                  # This file
```

## 🤝 Contributing

We welcome contributions to both platforms! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

### Development Setup

**JetBrains Plugin:**
```bash
git clone https://github.com/fatihkacar/ant-translation-generator-plugin.git
cd ant-translation-generator-plugin/intellij-ides
./gradlew runIde
```

**VS Code Extension:**
```bash
git clone https://github.com/fatihkacar/ant-translation-generator-plugin.git
cd ant-translation-generator-plugin/visual-studio-code
npm install
npm run compile
# Press F5 to run extension in development mode
```

## 🐛 Issues & Support

- **📋 Report Issues**: [GitHub Issues](https://github.com/fatihkacar/ant-translation-generator-plugin/issues)
- **💬 Discussions**: [GitHub Discussions](https://github.com/fatihkacar/ant-translation-generator-plugin/discussions)
- **📧 Email**: info@fatihkacar.com
- **🌐 Website**: [fatihkacar.com](https://fatihkacar.com)

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **OpenAI** for providing GPT-4 API
- **JetBrains** for the excellent Plugin SDK
- **Microsoft** for VS Code Extension API
- **Open-source community** for inspiration and feedback

## 🌟 Show Your Support

If you find this tool helpful:

- ⭐ **Star this repository** on GitHub
- 🔄 **Share** with your developer friends
- 📝 **Rate** on [JetBrains Marketplace](https://plugins.jetbrains.com/) and [VS Code Marketplace](https://marketplace.visualstudio.com/)
- 🐛 **Report bugs** or **suggest features**

---

**Made with ❤️ by [Fatih Kacar](https://fatihkacar.com)**

*Streamlining internationalization for developers worldwide* 🌍
