import * as vscode from 'vscode';
import * as fs from 'fs';
import * as path from 'path';
import { TranslationSettings } from './translationSettings';
import { OpenAITranslationService } from './openaiTranslationService';
import { LanguageCodes } from './languageCodes';

export class TranslationGenerator {
    private openAIService: OpenAITranslationService;

    constructor(private settings: TranslationSettings) {
        this.openAIService = new OpenAITranslationService();
    }

    /**
     * Generate translation key from selected text
     */
    async generateTranslationKey(editor: vscode.TextEditor): Promise<void> {
        const selection = editor.selection;
        const selectedText = editor.document.getText(selection);

        if (!selectedText.trim()) {
            vscode.window.showWarningMessage('No text selected for translation');
            return;
        }

        // Check if text is already a translation key
        const translationFunction = this.settings.translationFunction;
        const keyPattern = new RegExp(`\\w+\\(['"]([^'"]*?)['"]\\)`);
        if (keyPattern.test(selectedText)) {
            vscode.window.showWarningMessage('Selected text is already a translation key');
            return;
        }

        try {
            // Generate camelCase key from selected text
            const camelCaseKey = this.toCamelCase(selectedText);
            
            // Get file path segments for hierarchical key
            const filePath = editor.document.fileName;
            const pathSegments = this.createPathSegmentsFromFilePath(filePath);
            
            // Create full key
            const fullKey = pathSegments.length > 0 
                ? `${pathSegments.join('.')}.${camelCaseKey}`
                : camelCaseKey;

            // Find and validate translation files
            const translationFiles = await this.findTranslationFiles();
            if (translationFiles.length === 0) {
                vscode.window.showWarningMessage('No translation files found. Please configure translation file paths in settings.');
                return;
            }

            // Add translations to all files
            await this.addTranslations(translationFiles, fullKey, selectedText);

            // Replace selected text with translation function call
            await editor.edit(editBuilder => {
                editBuilder.replace(selection, `${translationFunction}('${fullKey}')`);
            });

            vscode.window.showInformationMessage(`Translation key generated: ${fullKey}`);

        } catch (error) {
            vscode.window.showErrorMessage(`Error generating translation key: ${error}`);
        }
    }

    /**
     * Remove translation key and restore original text
     */
    async removeTranslationKey(editor: vscode.TextEditor): Promise<void> {
        const selection = editor.selection;
        const selectedText = editor.document.getText(selection);

        if (!selectedText.trim()) {
            vscode.window.showWarningMessage('No text selected');
            return;
        }

        try {
            // Extract key from translation function call
            const keyPattern = new RegExp(`\\w+\\(['"]([^'"]*?)['"]\\)`);
            const match = selectedText.match(keyPattern);
            
            if (!match || !match[1]) {
                vscode.window.showWarningMessage('Selected text is not a translation key');
                return;
            }

            const fullKey = match[1];
            
            // Find translation files
            const translationFiles = await this.findTranslationFiles();
            if (translationFiles.length === 0) {
                vscode.window.showWarningMessage('No translation files found');
                return;
            }

            // Get original text from translation files
            const originalText = await this.getOriginalTextFromTranslationFiles(fullKey, translationFiles);
            
            // Remove from translation files
            await this.removeTranslationFromFiles(fullKey, translationFiles);

            // Replace translation key with original text
            const textToRestore = originalText || this.getLastSegmentFromKey(fullKey);
            await editor.edit(editBuilder => {
                editBuilder.replace(selection, textToRestore);
            });

            vscode.window.showInformationMessage(`Translation key removed: ${fullKey}`);

        } catch (error) {
            vscode.window.showErrorMessage(`Error removing translation key: ${error}`);
        }
    }

    /**
     * Convert text to camelCase
     */
    private toCamelCase(text: string): string {
        // Handle PascalCase/camelCase strings (e.g., DashboardMain -> dashboardMain)
        if (/^[A-Z][a-zA-Z0-9]*$/.test(text)) {
            return text.charAt(0).toLowerCase() + text.slice(1);
        }
        
        // Handle regular text with spaces, underscores, hyphens
        return text
            .replace(/[^\w\s]/g, '') // Remove special characters except word chars and spaces
            .replace(/[-_\s]+(.)?/g, (_, char) => char ? char.toUpperCase() : '') // Convert separators to camelCase
            .replace(/^[A-Z]/, char => char.toLowerCase()); // Ensure first letter is lowercase
    }

    /**
     * Create path segments from file path for hierarchical keys
     */
    private createPathSegmentsFromFilePath(filePath: string): string[] {
        if (!vscode.workspace.workspaceFolders) {
            return ['components', 'pages'];
        }

        const workspaceRoot = vscode.workspace.workspaceFolders[0].uri.fsPath;
        const relativePath = path.relative(workspaceRoot, filePath);
        
        const segments = relativePath
            .split(path.sep)
            .filter(segment => segment !== '.' && segment !== '..')
            .map(segment => segment.replace(/\.[^/.]+$/, '')) // Remove file extension
            .filter(segment => segment.length > 0)
            .map(segment => this.toCamelCase(segment)); // Convert to camelCase

        // Always start with components.pages prefix
        const result = ['components', 'pages'];
        
        // Add the path segments (excluding common directory names)
        const excludeSegments = ['src', 'components', 'pages', 'views', 'containers'];
        const filteredSegments = segments.filter(segment => 
            !excludeSegments.includes(segment.toLowerCase())
        );
        
        result.push(...filteredSegments);
        
        return result;
    }

    /**
     * Find translation files based on settings
     */
    private async findTranslationFiles(): Promise<string[]> {
        const translationPaths = this.settings.translationFilePaths;
        const files: string[] = [];

        if (!vscode.workspace.workspaceFolders) {
            return files;
        }

        const workspaceRoot = vscode.workspace.workspaceFolders[0].uri.fsPath;

        for (const relativePath of translationPaths) {
            const absolutePath = path.join(workspaceRoot, relativePath);
            
            try {
                await fs.promises.access(absolutePath);
                files.push(absolutePath);
            } catch {
                // File doesn't exist, skip
            }
        }

        return files;
    }

    /**
     * Add translations to all translation files
     */
    private async addTranslations(files: string[], key: string, value: string): Promise<void> {
        for (const filePath of files) {
            await this.addTranslationToFile(filePath, key, value);
        }

        // Auto-translate if enabled
        if (this.settings.autoTranslateEnabled && this.settings.openAiApiKey) {
            await this.autoTranslateToOtherLanguages(key, value);
        }
    }

    /**
     * Add translation to a single file
     */
    private async addTranslationToFile(filePath: string, key: string, value: string): Promise<void> {
        try {
            let jsonContent: any = {};
            
            // Read existing content
            try {
                const content = await fs.promises.readFile(filePath, 'utf8');
                if (content.trim()) {
                    jsonContent = JSON.parse(content);
                }
            } catch {
                // File doesn't exist or is empty, start with empty object
            }

            // Set nested value
            this.setNestedValue(jsonContent, key, value);

            // Write back to file
            const formattedJson = JSON.stringify(jsonContent, null, 2);
            await fs.promises.writeFile(filePath, formattedJson, 'utf8');

        } catch (error) {
            throw new Error(`Failed to update translation file ${filePath}: ${error}`);
        }
    }

    /**
     * Set nested value in object using dot notation
     */
    private setNestedValue(obj: any, key: string, value: string): void {
        const keys = key.split('.');
        let current = obj;

        for (let i = 0; i < keys.length - 1; i++) {
            const k = keys[i];
            if (!(k in current) || typeof current[k] !== 'object') {
                current[k] = {};
            }
            current = current[k];
        }

        current[keys[keys.length - 1]] = value;
    }

    /**
     * Get original text from translation files
     */
    private async getOriginalTextFromTranslationFiles(key: string, files: string[]): Promise<string> {
        const sourceFile = this.settings.sourceLanguageFile;
        
        // If sourceLanguageFile is configured, try to get value from it first
        if (sourceFile) {
            const sourceAbsolutePath = this.settings.getAbsolutePath(sourceFile);
            if (sourceAbsolutePath && files.includes(sourceAbsolutePath)) {
                try {
                    const content = await fs.promises.readFile(sourceAbsolutePath, 'utf8');
                    const jsonContent = JSON.parse(content);
                    const value = this.getNestedValue(jsonContent, key);
                    
                    if (typeof value === 'string') {
                        console.log(`Found original text in source file (${sourceFile}):`, value);
                        return value;
                    }
                } catch (error) {
                    console.warn(`Error reading source file ${sourceFile}:`, error);
                }
            }
        }
        
        // Fallback: try other files
        for (const filePath of files) {
            // Skip source file since we already tried it
            const sourceAbsolutePath = sourceFile ? this.settings.getAbsolutePath(sourceFile) : null;
            if (sourceAbsolutePath && filePath === sourceAbsolutePath) {
                continue;
            }
            
            try {
                const content = await fs.promises.readFile(filePath, 'utf8');
                const jsonContent = JSON.parse(content);
                const value = this.getNestedValue(jsonContent, key);
                
                if (typeof value === 'string') {
                    console.log(`Found original text in fallback file (${filePath}):`, value);
                    return value;
                }
            } catch {
                // Continue to next file
            }
        }
        
        console.log(`No original text found for key: ${key}`);
        return '';
    }

    /**
     * Get nested value from object using dot notation
     */
    private getNestedValue(obj: any, key: string): any {
        const keys = key.split('.');
        let current = obj;

        for (const k of keys) {
            if (current && typeof current === 'object' && k in current) {
                current = current[k];
            } else {
                return undefined;
            }
        }

        return current;
    }

    /**
     * Remove translation from files
     */
    private async removeTranslationFromFiles(key: string, files: string[]): Promise<void> {
        for (const filePath of files) {
            await this.removeTranslationFromFile(filePath, key);
        }
    }

    /**
     * Remove translation from a single file
     */
    private async removeTranslationFromFile(filePath: string, key: string): Promise<void> {
        try {
            const content = await fs.promises.readFile(filePath, 'utf8');
            const jsonContent = JSON.parse(content);
            
            this.removeNestedValue(jsonContent, key);
            
            const formattedJson = JSON.stringify(jsonContent, null, 2);
            await fs.promises.writeFile(filePath, formattedJson, 'utf8');
        } catch (error) {
            // Ignore errors when removing
        }
    }

    /**
     * Remove nested value from object using dot notation
     */
    private removeNestedValue(obj: any, key: string): void {
        const keys = key.split('.');
        let current = obj;
        const path: any[] = [obj];

        // Navigate to parent of target key
        for (let i = 0; i < keys.length - 1; i++) {
            const k = keys[i];
            if (current && typeof current === 'object' && k in current) {
                current = current[k];
                path.push(current);
            } else {
                return; // Key doesn't exist
            }
        }

        // Remove the target key
        const lastKey = keys[keys.length - 1];
        if (current && typeof current === 'object' && lastKey in current) {
            delete current[lastKey];
        }

        // Clean up empty parent objects
        for (let i = path.length - 1; i > 0; i--) {
            const currentObj = path[i];
            const parentObj = path[i - 1];
            const currentKey = keys[i - 1];

            if (typeof currentObj === 'object' && Object.keys(currentObj).length === 0) {
                delete parentObj[currentKey];
            } else {
                break;
            }
        }
    }

    /**
     * Get last segment from key for fallback text
     */
    private getLastSegmentFromKey(key: string): string {
        const segments = key.split('.');
        return segments[segments.length - 1];
    }

    /**
     * Auto-translate to other languages using OpenAI
     */
    private async autoTranslateToOtherLanguages(key: string, sourceText: string): Promise<void> {
        try {
            const sourceFile = this.settings.sourceLanguageFile;
            const languageMapping = this.settings.languageFileLanguages;
            
            console.log('Auto-translate debug:', {
                sourceFile,
                languageMapping,
                sourceText,
                key
            });
            
            if (!sourceFile || Object.keys(languageMapping).length === 0) {
                console.log('Auto-translate skipped: No source file or language mapping');
                return;
            }

            // Get source language code
            const sourceLanguageCode = languageMapping[sourceFile];
            if (!sourceLanguageCode) {
                console.log('Auto-translate skipped: Source language code not found for file:', sourceFile);
                return;
            }
            
            console.log('Source language code found:', sourceLanguageCode);

            // Translate to other languages
            for (const [filePath, languageCode] of Object.entries(languageMapping)) {
                console.log('Processing file:', filePath, 'with language:', languageCode);
                
                if (filePath === sourceFile || languageCode === sourceLanguageCode) {
                    console.log('Skipping file (same as source):', filePath);
                    continue;
                }

                try {
                    console.log('Translating from', sourceLanguageCode, 'to', languageCode);
                    const translatedText = await this.openAIService.translateText(
                        sourceText,
                        sourceLanguageCode,
                        languageCode,
                        this.settings.openAiApiKey
                    );

                    console.log('Translation result (raw):', translatedText);

                    if (translatedText) {
                        const absolutePath = this.settings.getAbsolutePath(filePath);
                        console.log('Absolute path for', filePath, ':', absolutePath);
                        
                        if (absolutePath) {
                            await this.addTranslationToFile(absolutePath, key, translatedText);
                            console.log('Translation added to file:', absolutePath);
                        } else {
                            console.log('Could not get absolute path for:', filePath);
                        }
                    }
                } catch (error) {
                    // Continue with other languages if one fails
                    console.error('Translation error for', languageCode, ':', error);
                    
                    const errorMessage = error instanceof Error ? error.message : String(error);
                    if (errorMessage.includes('temporarily unavailable')) {
                        vscode.window.showWarningMessage(`OpenAI API temporarily unavailable. Translation to ${languageCode} skipped.`);
                    } else {
                        vscode.window.showWarningMessage(`Failed to translate to ${languageCode}: ${errorMessage}`);
                    }
                }
            }
        } catch (error) {
            vscode.window.showWarningMessage(`Auto-translation failed: ${error}`);
        }
    }
} 