import * as vscode from 'vscode';
import * as fs from 'fs';
import * as path from 'path';
import { TranslationSettings } from './translationSettings';

export class TranslationHoverProvider implements vscode.HoverProvider {
    constructor(private settings: TranslationSettings) {}

    async provideHover(
        document: vscode.TextDocument,
        position: vscode.Position,
        token: vscode.CancellationToken
    ): Promise<vscode.Hover | undefined> {
        const range = document.getWordRangeAtPosition(position, /[a-zA-Z0-9_.]+/);
        if (!range) {
            return undefined;
        }

        const word = document.getText(range);
        
        // Check if this looks like a translation key (contains dots)
        if (!word.includes('.')) {
            return undefined;
        }

        // Get translation values from all language files
        const translations = await this.getTranslationsForKey(word);
        if (translations.length === 0) {
            return undefined;
        }

        // Create hover content
        const contents = new vscode.MarkdownString();
        contents.appendMarkdown(`**🌍 Translation Key:** \`${word}\`\n\n`);
        
        translations.forEach(({ language, value, filePath }) => {
            contents.appendMarkdown(`**${language.toUpperCase()}:** ${value}\n\n`);
        });

        contents.appendMarkdown(`---\n\n*Found in ${translations.length} language file(s)*`);

        return new vscode.Hover(contents, range);
    }

    private async getTranslationsForKey(key: string): Promise<Array<{language: string, value: string, filePath: string}>> {
        const results: Array<{language: string, value: string, filePath: string}> = [];
        const translationFiles = this.settings.translationFilePaths;
        const languageMapping = this.settings.languageFileLanguages;

        for (const filePath of translationFiles) {
            try {
                const workspaceFolder = vscode.workspace.workspaceFolders?.[0];
                if (!workspaceFolder) {
                    continue;
                }

                const fullPath = path.join(workspaceFolder.uri.fsPath, filePath);
                if (!fs.existsSync(fullPath)) {
                    continue;
                }

                const content = fs.readFileSync(fullPath, 'utf8');
                const translations = JSON.parse(content);
                
                const value = this.getNestedValue(translations, key);
                if (value) {
                    const language = languageMapping[filePath] || path.basename(filePath, '.json');
                    results.push({
                        language,
                        value,
                        filePath
                    });
                }
            } catch (error) {
                // Ignore errors for individual files
                console.warn(`Error reading translation file ${filePath}:`, error);
            }
        }

        return results;
    }

    private getNestedValue(obj: any, key: string): string | undefined {
        const keys = key.split('.');
        let current = obj;

        for (const k of keys) {
            if (current && typeof current === 'object' && k in current) {
                current = current[k];
            } else {
                return undefined;
            }
        }

        return typeof current === 'string' ? current : undefined;
    }
} 