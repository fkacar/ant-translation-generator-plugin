import * as vscode from 'vscode';

export interface LanguageFileMapping {
    [filePath: string]: string; // file path -> language code
}

export class TranslationSettings {
    private static readonly CONFIG_SECTION = 'antTranslation';

    get translationFilePaths(): string[] {
        return vscode.workspace.getConfiguration(TranslationSettings.CONFIG_SECTION)
            .get<string[]>('translationFilePaths', []);
    }

    get translationFunction(): string {
        return vscode.workspace.getConfiguration(TranslationSettings.CONFIG_SECTION)
            .get<string>('translationFunction', 't');
    }

    get autoTranslateEnabled(): boolean {
        return vscode.workspace.getConfiguration(TranslationSettings.CONFIG_SECTION)
            .get<boolean>('autoTranslateEnabled', false);
    }

    get openAiApiKey(): string {
        return vscode.workspace.getConfiguration(TranslationSettings.CONFIG_SECTION)
            .get<string>('openAiApiKey', '');
    }

    get sourceLanguageFile(): string {
        return vscode.workspace.getConfiguration(TranslationSettings.CONFIG_SECTION)
            .get<string>('sourceLanguageFile', '');
    }

    get languageFileLanguages(): LanguageFileMapping {
        return vscode.workspace.getConfiguration(TranslationSettings.CONFIG_SECTION)
            .get<LanguageFileMapping>('languageFileLanguages', {});
    }

    async updateTranslationFilePaths(paths: string[]): Promise<void> {
        await vscode.workspace.getConfiguration(TranslationSettings.CONFIG_SECTION)
            .update('translationFilePaths', paths, vscode.ConfigurationTarget.Workspace);
    }

    async updateTranslationFunction(functionName: string): Promise<void> {
        await vscode.workspace.getConfiguration(TranslationSettings.CONFIG_SECTION)
            .update('translationFunction', functionName, vscode.ConfigurationTarget.Workspace);
    }

    async updateAutoTranslateEnabled(enabled: boolean): Promise<void> {
        await vscode.workspace.getConfiguration(TranslationSettings.CONFIG_SECTION)
            .update('autoTranslateEnabled', enabled, vscode.ConfigurationTarget.Workspace);
    }

    async updateOpenAiApiKey(apiKey: string): Promise<void> {
        await vscode.workspace.getConfiguration(TranslationSettings.CONFIG_SECTION)
            .update('openAiApiKey', apiKey, vscode.ConfigurationTarget.Global);
    }

    async updateSourceLanguageFile(filePath: string): Promise<void> {
        await vscode.workspace.getConfiguration(TranslationSettings.CONFIG_SECTION)
            .update('sourceLanguageFile', filePath, vscode.ConfigurationTarget.Workspace);
    }

    async updateLanguageFileLanguages(mapping: LanguageFileMapping): Promise<void> {
        await vscode.workspace.getConfiguration(TranslationSettings.CONFIG_SECTION)
            .update('languageFileLanguages', mapping, vscode.ConfigurationTarget.Workspace);
    }

    /**
     * Validate if translation files exist
     */
    async validateTranslationFiles(): Promise<{ valid: string[], invalid: string[] }> {
        const valid: string[] = [];
        const invalid: string[] = [];

        for (const filePath of this.translationFilePaths) {
            try {
                const uri = vscode.Uri.joinPath(vscode.workspace.workspaceFolders![0].uri, filePath);
                await vscode.workspace.fs.stat(uri);
                valid.push(filePath);
            } catch {
                invalid.push(filePath);
            }
        }

        return { valid, invalid };
    }

    /**
     * Get workspace relative path from absolute path
     */
    getWorkspaceRelativePath(absolutePath: string): string | null {
        if (!vscode.workspace.workspaceFolders) {
            return null;
        }

        const workspaceRoot = vscode.workspace.workspaceFolders[0].uri.fsPath;
        if (absolutePath.startsWith(workspaceRoot)) {
            return absolutePath.substring(workspaceRoot.length + 1).replace(/\\/g, '/');
        }

        return null;
    }

    /**
     * Get absolute path from workspace relative path
     */
    getAbsolutePath(relativePath: string): string | null {
        if (!vscode.workspace.workspaceFolders) {
            return null;
        }

        return vscode.Uri.joinPath(vscode.workspace.workspaceFolders[0].uri, relativePath).fsPath;
    }
} 