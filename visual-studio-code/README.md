# Ant Translation Generator - VS Code Extension

🌍 **Powerful VS Code extension for internationalization (i18n)** - Generate translation keys instantly, manage multiple language files, and leverage AI-powered auto-translation with OpenAI GPT-4.

## ✨ Key Features

- **🚀 Instant Translation Key Generation** - Select text and generate hierarchical keys with built-in shortcuts
- **🤖 AI-Powered Auto Translation** - Integrate with OpenAI GPT-4 for automatic translations to 70+ languages
- **📁 Multi-Language File Management** - Support for multiple translation files with project-relative paths
- **⌨️ Built-in Keyboard Shortcuts** - Generate and Remove translation keys with platform-specific shortcuts
- **🎯 Smart Key Management** - Remove keys and restore original text from source language file
- **🔧 Customizable Settings** - Configure function names, file paths, and language mappings
- **🖱️ Context Menu Integration** - Right-click on selected text for quick actions
- **💡 Hover Information** - View translation values by hovering over translation keys

## 🚀 Quick Start

1. **Install the extension** from VS Code Marketplace
2. **Configure translation files** in settings (Ctrl+, → search "Ant Translation")
3. **Select text** in your code
4. **Press Cmd+Alt+T** (macOS) or **Ctrl+Alt+T** (Windows/Linux) to generate translation key
5. **Press Cmd+Alt+R** (macOS) or **Ctrl+Alt+R** (Windows/Linux) to remove translation key

## 📸 Screenshots

### Generate Translation Keys
![Generate Translation Key Demo](images/screenshots/generate-key-demo.gif)
*Select text and generate hierarchical translation keys with keyboard shortcuts*

### Modern Settings Panel
![Settings Panel](images/screenshots/settings-panel.png)
*Configure translation files, OpenAI integration, and language mappings*

### Context Menu Integration
![Context Menu](images/screenshots/context-menu.png)
*Right-click on selected text for quick translation actions*

### Auto Translation with OpenAI
![Auto Translation](images/screenshots/auto-translation.png)
*Automatic translation to 70+ languages using GPT-4*

### Hover Information
![Hover Information](images/screenshots/hover-info.png)
*View translation values by hovering over translation keys*

### Remove Translation Keys
![Remove Translation Key Demo](images/screenshots/remove-key-demo.gif)
*Remove translation keys and restore original text from source language*

## ⌨️ Keyboard Shortcuts

### Built-in Shortcuts (Ready to Use)

| Action | macOS | Windows/Linux |
|--------|-------|---------------|
| **Generate Translation Key** | `Cmd+Alt+T` | `Ctrl+Alt+T` |
| **Remove Translation Key** | `Cmd+Alt+R` | `Ctrl+Alt+R` |

These shortcuts work automatically when you install the extension - no setup required!

### Customizing Shortcuts

If you want to change the default shortcuts:

1. **Open Keyboard Shortcuts**:
   - Press `F1` → type "Preferences: Open Keyboard Shortcuts"
   - Or use `Cmd+K Cmd+S` (macOS) / `Ctrl+K Ctrl+S` (Windows/Linux)

2. **Search for "ant-translation"** in the shortcuts list

3. **Click the pencil icon** next to the command you want to change

4. **Press your desired key combination** and hit Enter

5. **Done!** Your custom shortcuts are now active

### Alternative Access Methods

- **Command Palette**: `F1` → "Ant Translation: Generate Key" or "Ant Translation: Remove Key"
- **Context Menu**: Right-click on selected text → "Generate Translation Key" or "Remove Translation Key"

## 🔧 Configuration

### Essential Settings

Open VS Code Settings (`Cmd+,` / `Ctrl+,`) and search for "Ant Translation":

#### Translation Files
```json
{
  "antTranslation.translationFilePaths": [
    "wwwroot/languages/en.json",
    "wwwroot/languages/tr.json"
  ]
}
```

#### Language File Mapping
```json
{
  "antTranslation.languageFileLanguages": {
    "wwwroot/languages/en.json": "en",
    "wwwroot/languages/tr.json": "tr"
  }
}
```

#### Source Language File (for Remove Key action)
```json
{
  "antTranslation.sourceLanguageFile": "wwwroot/languages/tr.json"
}
```

#### Translation Function
```json
{
  "antTranslation.translationFunction": "t"
}
```

### Auto Translation (OpenAI)

Enable automatic translation with OpenAI GPT-4:

```json
{
  "antTranslation.autoTranslateEnabled": true,
  "antTranslation.openAiApiKey": "sk-proj-your-api-key-here"
}
```

1. Get your API key from [OpenAI Platform](https://platform.openai.com/api-keys)
2. Enable auto-translation in settings
3. Set your source language file
4. Map files to language codes

## 🌐 Supported Languages

70+ languages with ISO 639-1 codes including:
- English (en), Turkish (tr), Spanish (es), French (fr)
- German (de), Italian (it), Portuguese (pt), Russian (ru)
- Japanese (ja), Korean (ko), Chinese (zh), Arabic (ar)
- And many more...

## 📝 Usage Examples

### Generate Translation Key

**Before:**
```javascript
const message = "Welcome to our application";
```

**Select the text and press `Cmd+Alt+T` (macOS) or `Ctrl+Alt+T` (Windows/Linux)**

**After:**
```javascript
const message = t('components.pages.public.dashboard.welcomeToOurApplication');
```

**Generated in translation files:**
```json
{
  "components": {
    "pages": {
      "public": {
        "dashboard": {
          "welcomeToOurApplication": "Welcome to our application"
        }
      }
    }
  }
}
```

### Remove Translation Key

**Before:**
```javascript
const message = t('components.pages.public.dashboard.welcomeToOurApplication');
```

**Select the translation key and press `Cmd+Alt+R` (macOS) or `Ctrl+Alt+R` (Windows/Linux)**

**After:**
```javascript
const message = "Welcome to our application";
```

The original text is restored from your source language file (configured in `sourceLanguageFile` setting).

### Auto Translation

When auto-translation is enabled, generating a key in Turkish will automatically create English translations:

**Turkish (source):**
```json
{
  "components": {
    "pages": {
      "public": {
        "dashboard": {
          "welcomeToOurApplication": "Uygulamamıza hoş geldiniz"
        }
      }
    }
  }
}
```

**English (auto-generated):**
```json
{
  "components": {
    "pages": {
      "public": {
        "dashboard": {
          "welcomeToOurApplication": "Welcome to our application"
        }
      }
    }
  }
}
```

## 💡 Hover Information

Hover over any translation key in your code to see:
- 🌍 Translation values in all configured languages
- 📁 Source file information
- 🔍 Key structure

## 🔒 Privacy & Security

- **Local API key storage** - OpenAI keys stored securely in VS Code settings
- **No data collection** - Extension doesn't collect or transmit user data
- **Open source** - Full code available for review on GitHub

## 🛠️ Development

### Prerequisites
- Node.js 16+
- VS Code 1.74+

### Setup
```bash
cd visual-studio-code
npm install
npm run compile
```

### Testing
```bash
npm run test
```

### Building
```bash
npm run vscode:prepublish
```

## 🎯 Tips & Best Practices

1. **Configure source language first** - Set `sourceLanguageFile` for proper Remove Key functionality
2. **Use descriptive text** - Better source text leads to better auto-translations
3. **Organize your files** - Use consistent file paths and language codes
4. **Test shortcuts** - Use the test command to verify everything works: `F1` → "Ant Translation: Test Command"

## 📄 License

MIT License - see [LICENSE](../LICENSE) file for details.

## 🤝 Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](../CONTRIBUTING.md) for guidelines.

## 🐛 Issues & Support

- 🐛 **Report Issues**: [GitHub Issues](https://github.com/fatihkacar/ant-translation-generator-plugin/issues)
- 📧 **Email**: info@fatihkacar.com
- 🌐 **Website**: https://fatihkacar.com
- 📱 **GitHub**: https://github.com/fatihkacar/ant-translation-generator-plugin

---

**Made with ❤️ for developers who care about internationalization**
