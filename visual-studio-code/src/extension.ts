import * as vscode from 'vscode';
import { TranslationGenerator } from './translationGenerator';
import { TranslationSettings } from './translationSettings';
import { SettingsWebviewProvider } from './settingsWebview';
import { TranslationHoverProvider } from './translationHoverProvider';

export async function activate(context: vscode.ExtensionContext) {
    console.log('Ant Translation Generator extension is now active!');

    // Initialize services
    const settings = new TranslationSettings();
    const generator = new TranslationGenerator(settings);
    const settingsProvider = new SettingsWebviewProvider(context.extensionUri, settings);
    const hoverProvider = new TranslationHoverProvider(settings);

    // Register webview provider
    context.subscriptions.push(
        vscode.window.registerWebviewViewProvider(SettingsWebviewProvider.viewType, settingsProvider)
    );

    // Register hover provider for specific file types
    context.subscriptions.push(
        vscode.languages.registerHoverProvider(['typescript', 'javascript', 'typescriptreact', 'javascriptreact', 'vue', 'html'], hoverProvider)
    );

    // Register commands
    const generateCommand = vscode.commands.registerCommand('ant-translation.generateKey', async () => {
        console.log('🔥 Generate command triggered!');
        const editor = vscode.window.activeTextEditor;
        if (!editor) {
            vscode.window.showWarningMessage('No active editor found');
            return;
        }

        const selection = editor.selection;
        if (selection.isEmpty) {
            vscode.window.showWarningMessage('Please select text to generate translation key');
            return;
        }

        console.log('🔥 About to generate translation key for:', editor.document.getText(selection));
        try {
            await generator.generateTranslationKey(editor);
        } catch (error) {
            console.error('🔥 Error in generateTranslationKey:', error);
            vscode.window.showErrorMessage(`Error generating translation key: ${error}`);
        }
    });

    const removeCommand = vscode.commands.registerCommand('ant-translation.removeKey', async () => {
        console.log('🔥 Remove command triggered!');
        const editor = vscode.window.activeTextEditor;
        if (!editor) {
            vscode.window.showWarningMessage('No active editor found');
            return;
        }

        const selection = editor.selection;
        if (selection.isEmpty) {
            vscode.window.showWarningMessage('Please select text to remove translation key');
            return;
        }

        console.log('🔥 About to remove translation key for:', editor.document.getText(selection));
        try {
            await generator.removeTranslationKey(editor);
        } catch (error) {
            console.error('🔥 Error in removeTranslationKey:', error);
            vscode.window.showErrorMessage(`Error removing translation key: ${error}`);
        }
    });

    const openSettingsCommand = vscode.commands.registerCommand('ant-translation.openSettings', () => {
        vscode.commands.executeCommand('workbench.action.openSettings', 'antTranslation');
    });

    const testCommand = vscode.commands.registerCommand('ant-translation.testCommand', async () => {
        vscode.window.showInformationMessage('🎉 Test command works! Extension is active and commands are registered.');
        console.log('🔥 Test command executed successfully');
        
        const isMac = process.platform === 'darwin';
        const generateKey = isMac ? 'Cmd+Alt+T' : 'Ctrl+Alt+T';
        const removeKey = isMac ? 'Cmd+Alt+R' : 'Ctrl+Alt+R';
        
        vscode.window.showInformationMessage(
            `🎹 Built-in Shortcuts:\n• Generate: ${generateKey}\n• Remove: ${removeKey}\n\nThese work automatically!`
        );
    });

    // Add commands to subscriptions
    context.subscriptions.push(generateCommand, removeCommand, openSettingsCommand, testCommand);

    // Show welcome message
    const isMac = process.platform === 'darwin';
    const generateKey = isMac ? 'Cmd+Alt+T' : 'Ctrl+Alt+T';
    vscode.window.showInformationMessage(
        `🌍 Ant Translation Generator is ready!\n\n` +
        `🎹 Built-in shortcut: ${generateKey}\n` +
        `Select text and use the shortcut to generate translation keys!`,
        'Test Now'
    ).then(action => {
        if (action === 'Test Now') {
            vscode.commands.executeCommand('ant-translation.testCommand');
        }
    });
}

export function deactivate() {
    console.log('Ant Translation Generator extension is now deactivated');
} 