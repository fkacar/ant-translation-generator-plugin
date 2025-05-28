# 🌍 Ant Translation Generator - IntelliJ IDEA Plugin

[![JetBrains Plugin](https://img.shields.io/badge/JetBrains-Plugin-orange.svg)](https://plugins.jetbrains.com/)
[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)](https://github.com/fatihkacar/ant-translation-generator)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](../LICENSE)

**Ant Translation Generator** is a powerful IntelliJ IDEA plugin that streamlines the internationalization (i18n) process for your applications. Generate translation keys instantly, manage multiple language files, and leverage AI-powered auto-translation with OpenAI GPT-4.

## ✨ Features

### 🚀 **Instant Translation Key Generation**
- Select any text in your code and generate translation keys with a single keystroke
- Automatically creates hierarchical keys based on your file structure
- Supports nested JSON structure for organized translations
- Customizable translation function names (t, i18n, $t, etc.)

### 🤖 **AI-Powered Auto Translation**
- **NEW!** Integrate with OpenAI GPT-4 for automatic translations
- Set a source language file (e.g., Turkish) and auto-translate to target languages
- Supports 70+ languages with ISO 639-1 language codes
- Smart fallback to original text if translation fails

### 📁 **Multi-Language File Management**
- Support for multiple translation files simultaneously
- Project-relative path configuration
- Automatic JSON validation and formatting
- Real-time file existence validation

### ⌨️ **Keyboard Shortcuts & Context Menu**
- **Generate Translation Key**: `Shift + Ctrl + T`
- **Remove Translation Key**: `Shift + Ctrl + D`
- Right-click context menu integration
- Customizable keyboard shortcuts

### 🎯 **Smart Key Management**
- Remove translation keys and restore original text
- Hierarchical key structure based on file paths
- CamelCase key generation from selected text
- Duplicate key prevention

## 🛠️ Installation

### From JetBrains Marketplace
1. Open IntelliJ IDEA/WebStorm/PhpStorm/Rider
2. Go to `File` → `Settings` → `Plugins`
3. Search for "Ant Translation Generator"
4. Click `Install` and restart your IDE

### Manual Installation
1. Download the latest release from [GitHub Releases](https://github.com/fatihkacar/ant-translation-generator-plugin/releases)
2. Go to `File` → `Settings` → `Plugins`
3. Click the gear icon → `Install Plugin from Disk`
4. Select the downloaded `.zip` file

## ⚙️ Configuration

### Basic Setup
1. Go to `File` → `Settings` → `Ant Translation Settings`
2. **Translation Files Tab**: Add your translation file paths
   ```
   wwwroot/languages/en.json
   wwwroot/languages/tr.json
   src/assets/i18n/de.json
   ```
3. **General Settings Tab**: Configure function name and shortcuts
4. Click `Apply` to save settings

### Auto Translation Setup
1. Navigate to the **Auto Translate** tab
2. Enable auto translation
3. Enter your OpenAI API key ([Get API Key](https://platform.openai.com/api-keys))
4. Test your API key with the "Test API Key" button
5. Select your source language file (e.g., `tr.json` for Turkish)
6. Configure language codes for each translation file:
   - `tr.json` → `tr - Turkish`
   - `en.json` → `en - English`
   - `de.json` → `de - German`

## 🎮 Usage

### Generate Translation Keys

#### Method 1: Keyboard Shortcut
1. Select text in your code: `"Welcome to our app"`
2. Press `Shift + Ctrl + T`
3. Text is replaced with: `t('components.pages.welcome.welcomeToOurApp')`
4. Translation files are updated automatically

#### Method 2: Context Menu
1. Select text in your code
2. Right-click → `Generate Translation Key`

### Remove Translation Keys

#### Method 1: Keyboard Shortcut
1. Select a translation key: `t('user.profile.name')`
2. Press `Shift + Ctrl + D`
3. Key is replaced with original text and removed from files

#### Method 2: Context Menu
1. Select a translation key
2. Right-click → `Remove Translation Key`

### Example Workflow

**Before:**
```javascript
const message = "Hello World";
const title = "User Profile";
```

**After generating translation keys:**
```javascript
const message = t('components.pages.home.helloWorld');
const title = t('components.pages.profile.userProfile');
```

**Generated JSON files:**

`en.json`:
```json
{
  "components": {
    "pages": {
      "home": {
        "helloWorld": "Hello World"
      },
      "profile": {
        "userProfile": "User Profile"
      }
    }
  }
}
```

`tr.json` (with auto-translate):
```json
{
  "components": {
    "pages": {
      "home": {
        "helloWorld": "Merhaba Dünya"
      },
      "profile": {
        "userProfile": "Kullanıcı Profili"
      }
    }
  }
}
```

## 🌐 Supported Languages

The plugin supports 70+ languages including:

| Code | Language | Code | Language | Code | Language |
|------|----------|------|----------|------|----------|
| en | English | tr | Turkish | de | German |
| fr | French | es | Spanish | it | Italian |
| pt | Portuguese | ru | Russian | ja | Japanese |
| ko | Korean | zh | Chinese | ar | Arabic |
| hi | Hindi | ... | ... | ... | ... |

[View full language list](src/main/kotlin/com/ant/utils/LanguageCodes.kt)

## 🔧 Supported IDEs

- IntelliJ IDEA (Ultimate & Community)
- WebStorm
- PhpStorm
- PyCharm
- Rider
- Android Studio
- All JetBrains IDEs (2023.1+)

## 📋 Supported File Types

- JavaScript (.js)
- TypeScript (.ts)
- JSX (.jsx)
- TSX (.tsx)
- Vue (.vue)
- PHP (.php)
- HTML (.html)
- Kotlin (.kt)
- And more...

## 🎯 Use Cases

### Frontend Development
- React/Vue/Angular applications
- Next.js/Nuxt.js projects
- Mobile app development (React Native, Flutter)

### Backend Development
- Node.js applications
- PHP applications
- .NET applications (with Rider)

### Multi-platform Projects
- Electron applications
- Progressive Web Apps (PWA)
- Hybrid mobile applications

## 🔒 Privacy & Security

- **API Key Security**: Your OpenAI API key is stored locally and never shared
- **Local Processing**: All file operations are performed locally
- **No Data Collection**: The plugin doesn't collect or transmit any personal data
- **Open Source**: Full source code is available for review

## 🐛 Troubleshooting

### Common Issues

**Translation files not found:**
- Ensure file paths are relative to project root
- Check file permissions
- Validate JSON syntax

**Auto-translation not working:**
- Verify OpenAI API key is valid
- Check internet connection
- Ensure sufficient API credits

**Keyboard shortcuts not working:**
- Check for conflicting shortcuts in IDE settings
- Try using context menu instead
- Restart IDE after configuration changes

### Getting Help

1. Check the [FAQ](https://github.com/fatihkacar/ant-translation-generator-plugin/wiki/FAQ)
2. Search [existing issues](https://github.com/fatihkacar/ant-translation-generator-plugin/issues)
3. Create a [new issue](https://github.com/fatihkacar/ant-translation-generator-plugin/issues/new)

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](../CONTRIBUTING.md) for details.

### Development Setup
```bash
git clone https://github.com/fatihkacar/ant-translation-generator-plugin.git
cd ant-translation-generator-plugin/intellij-ides
./gradlew runIde
```

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](../LICENSE) file for details.

## 🙏 Acknowledgments

- OpenAI for providing GPT-4 API
- JetBrains for the excellent Plugin SDK
- The open-source community for inspiration and feedback

## 📞 Support

- **Email**: info@fatihkacar.com
- **GitHub Issues**: [Report a bug](https://github.com/fatihkacar/ant-translation-generator-plugin/issues)
- **Website**: [fatihkacar.com](https://fatihkacar.com)

---

**Made with ❤️ by [Fatih Kacar](https://fatihkacar.com)**

*If you find this plugin helpful, please consider giving it a ⭐ on GitHub and rating it on the JetBrains Marketplace!* 