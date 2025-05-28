import * as vscode from 'vscode';
import { TranslationSettings } from './translationSettings';
import { LanguageCodes } from './languageCodes';
import { OpenAITranslationService } from './openaiTranslationService';

export class SettingsWebviewProvider implements vscode.WebviewViewProvider {
    public static readonly viewType = 'antTranslation.settingsView';

    private _view?: vscode.WebviewView;

    constructor(
        private readonly _extensionUri: vscode.Uri,
        private readonly settings: TranslationSettings
    ) {}

    public resolveWebviewView(
        webviewView: vscode.WebviewView,
        context: vscode.WebviewViewResolveContext,
        _token: vscode.CancellationToken,
    ) {
        this._view = webviewView;

        webviewView.webview.options = {
            enableScripts: true,
            localResourceRoots: [
                this._extensionUri
            ]
        };

        webviewView.webview.html = this._getHtmlForWebview(webviewView.webview);

        webviewView.webview.onDidReceiveMessage(data => {
            switch (data.type) {
                case 'addTranslationFile':
                    this._addTranslationFile();
                    break;
                case 'removeTranslationFile':
                    this._removeTranslationFile(data.index);
                    break;
                case 'updateTranslationFunction':
                    this._updateTranslationFunction(data.value);
                    break;
                case 'updateAutoTranslate':
                    this._updateAutoTranslate(data.enabled);
                    break;
                case 'updateApiKey':
                    this._updateApiKey(data.value);
                    break;
                case 'testApiKey':
                    this._testApiKey();
                    break;
                case 'updateSourceLanguageFile':
                    this._updateSourceLanguageFile(data.value);
                    break;
                case 'updateLanguageMapping':
                    this._updateLanguageMapping(data.filePath, data.languageCode);
                    break;
                case 'refresh':
                    this._refresh();
                    break;
            }
        });

        this._refresh();
    }

    private async _addTranslationFile() {
        const options: vscode.OpenDialogOptions = {
            canSelectMany: false,
            openLabel: 'Select Translation File',
            filters: {
                'JSON files': ['json']
            }
        };

        const fileUri = await vscode.window.showOpenDialog(options);
        if (fileUri && fileUri[0]) {
            const relativePath = this.settings.getWorkspaceRelativePath(fileUri[0].fsPath);
            if (relativePath) {
                const currentPaths = this.settings.translationFilePaths;
                if (!currentPaths.includes(relativePath)) {
                    currentPaths.push(relativePath);
                    await this.settings.updateTranslationFilePaths(currentPaths);
                    this._refresh();
                }
            }
        }
    }

    private async _removeTranslationFile(index: number) {
        const currentPaths = this.settings.translationFilePaths;
        if (index >= 0 && index < currentPaths.length) {
            currentPaths.splice(index, 1);
            await this.settings.updateTranslationFilePaths(currentPaths);
            this._refresh();
        }
    }

    private async _updateTranslationFunction(value: string) {
        await this.settings.updateTranslationFunction(value);
    }

    private async _updateAutoTranslate(enabled: boolean) {
        await this.settings.updateAutoTranslateEnabled(enabled);
    }

    private async _updateApiKey(value: string) {
        await this.settings.updateOpenAiApiKey(value);
    }

    private async _testApiKey() {
        const apiKey = this.settings.openAiApiKey;
        if (!apiKey) {
            vscode.window.showWarningMessage('Please enter an API key first');
            return;
        }

        const service = new OpenAITranslationService();
        try {
            const isValid = await service.testApiKey(apiKey);
            if (isValid) {
                vscode.window.showInformationMessage('API key is valid!');
            } else {
                vscode.window.showErrorMessage('API key is invalid');
            }
        } catch (error) {
            vscode.window.showErrorMessage(`Error testing API key: ${error}`);
        }
    }

    private async _updateSourceLanguageFile(value: string) {
        await this.settings.updateSourceLanguageFile(value);
    }

    private async _updateLanguageMapping(filePath: string, languageCode: string) {
        const currentMapping = this.settings.languageFileLanguages;
        if (languageCode) {
            currentMapping[filePath] = languageCode;
        } else {
            delete currentMapping[filePath];
        }
        await this.settings.updateLanguageFileLanguages(currentMapping);
    }

    private _refresh() {
        if (this._view) {
            this._view.webview.html = this._getHtmlForWebview(this._view.webview);
        }
    }

    private _getHtmlForWebview(webview: vscode.Webview) {
        const translationFiles = this.settings.translationFilePaths;
        const translationFunction = this.settings.translationFunction;
        const autoTranslateEnabled = this.settings.autoTranslateEnabled;
        const apiKey = this.settings.openAiApiKey;
        const sourceLanguageFile = this.settings.sourceLanguageFile;
        const languageMapping = this.settings.languageFileLanguages;

        const languages = LanguageCodes.getAllLanguages();
        const languageOptions = languages.map(lang => 
            `<option value="${lang.code}">${lang.name} (${lang.nativeName})</option>`
        ).join('');

        return `<!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Ant Translation Settings</title>
            <style>
                body {
                    font-family: var(--vscode-font-family);
                    font-size: var(--vscode-font-size);
                    color: var(--vscode-foreground);
                    background-color: var(--vscode-editor-background);
                    padding: 10px;
                }
                .section {
                    margin-bottom: 20px;
                    padding: 10px;
                    border: 1px solid var(--vscode-panel-border);
                    border-radius: 4px;
                }
                .section-title {
                    font-weight: bold;
                    margin-bottom: 10px;
                    color: var(--vscode-textLink-foreground);
                }
                input, select, button {
                    background-color: var(--vscode-input-background);
                    color: var(--vscode-input-foreground);
                    border: 1px solid var(--vscode-input-border);
                    padding: 4px 8px;
                    margin: 2px;
                    border-radius: 2px;
                    font-family: inherit;
                    font-size: inherit;
                }
                button {
                    background-color: var(--vscode-button-background);
                    color: var(--vscode-button-foreground);
                    cursor: pointer;
                }
                button:hover {
                    background-color: var(--vscode-button-hoverBackground);
                }
                .file-item {
                    display: flex;
                    align-items: center;
                    margin: 5px 0;
                    padding: 5px;
                    background-color: var(--vscode-list-inactiveSelectionBackground);
                    border-radius: 3px;
                }
                .file-path {
                    flex: 1;
                    margin-right: 10px;
                    font-family: monospace;
                }
                .remove-btn {
                    background-color: var(--vscode-errorForeground);
                    color: white;
                    border: none;
                    padding: 2px 6px;
                    border-radius: 2px;
                    cursor: pointer;
                    font-size: 12px;
                }
                .language-mapping {
                    display: flex;
                    align-items: center;
                    margin: 5px 0;
                }
                .language-mapping select {
                    margin-left: 10px;
                    width: 150px;
                }
                .checkbox-container {
                    display: flex;
                    align-items: center;
                    margin: 10px 0;
                }
                .checkbox-container input[type="checkbox"] {
                    margin-right: 8px;
                }
                .api-key-container {
                    display: flex;
                    align-items: center;
                    gap: 10px;
                }
                .api-key-input {
                    flex: 1;
                    min-width: 200px;
                }
            </style>
        </head>
        <body>
            <div class="section">
                <div class="section-title">📁 Translation Files</div>
                <div id="translationFiles">
                    ${translationFiles.map((file, index) => `
                        <div class="file-item">
                            <span class="file-path">${file}</span>
                            <button class="remove-btn" onclick="removeFile(${index})">Remove</button>
                        </div>
                    `).join('')}
                </div>
                <button onclick="addFile()">Add Translation File</button>
            </div>

            <div class="section">
                <div class="section-title">⚙️ Function Settings</div>
                <label>Translation Function Name:</label><br>
                <input type="text" id="translationFunction" value="${translationFunction}" 
                       onchange="updateTranslationFunction(this.value)" placeholder="t">
                <small>Function name used in code (e.g., t, i18n, $t)</small>
            </div>

            <div class="section">
                <div class="section-title">🤖 Auto Translation (OpenAI)</div>
                <div class="checkbox-container">
                    <input type="checkbox" id="autoTranslate" ${autoTranslateEnabled ? 'checked' : ''} 
                           onchange="updateAutoTranslate(this.checked)">
                    <label for="autoTranslate">Enable automatic translation with OpenAI GPT-4</label>
                </div>
                
                <div class="api-key-container">
                    <input type="password" id="apiKey" class="api-key-input" value="${apiKey}" 
                           onchange="updateApiKey(this.value)" placeholder="Enter OpenAI API Key">
                    <button onclick="testApiKey()">Test Key</button>
                </div>
                
                <label>Source Language File:</label><br>
                <select id="sourceLanguageFile" onchange="updateSourceLanguageFile(this.value)">
                    <option value="">Select source file...</option>
                    ${translationFiles.map(file => `
                        <option value="${file}" ${file === sourceLanguageFile ? 'selected' : ''}>${file}</option>
                    `).join('')}
                </select>
            </div>

            <div class="section">
                <div class="section-title">🌍 Language Mappings</div>
                ${translationFiles.map(file => `
                    <div class="language-mapping">
                        <span class="file-path">${file}</span>
                        <select onchange="updateLanguageMapping('${file}', this.value)">
                            <option value="">Select language...</option>
                            ${languageOptions}
                        </select>
                    </div>
                `).join('')}
            </div>

            <script>
                const vscode = acquireVsCodeApi();

                function addFile() {
                    vscode.postMessage({ type: 'addTranslationFile' });
                }

                function removeFile(index) {
                    vscode.postMessage({ type: 'removeTranslationFile', index: index });
                }

                function updateTranslationFunction(value) {
                    vscode.postMessage({ type: 'updateTranslationFunction', value: value });
                }

                function updateAutoTranslate(enabled) {
                    vscode.postMessage({ type: 'updateAutoTranslate', enabled: enabled });
                }

                function updateApiKey(value) {
                    vscode.postMessage({ type: 'updateApiKey', value: value });
                }

                function testApiKey() {
                    vscode.postMessage({ type: 'testApiKey' });
                }

                function updateSourceLanguageFile(value) {
                    vscode.postMessage({ type: 'updateSourceLanguageFile', value: value });
                }

                function updateLanguageMapping(filePath, languageCode) {
                    vscode.postMessage({ 
                        type: 'updateLanguageMapping', 
                        filePath: filePath, 
                        languageCode: languageCode 
                    });
                }

                // Set current language mappings
                ${Object.entries(languageMapping).map(([file, lang]) => `
                    document.querySelector('select[onchange*="${file}"]').value = '${lang}';
                `).join('')}
            </script>
        </body>
        </html>`;
    }
} 