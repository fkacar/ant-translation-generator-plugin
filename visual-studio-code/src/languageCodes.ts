export interface LanguageInfo {
    code: string;
    name: string;
    nativeName: string;
}

export class LanguageCodes {
    private static readonly languages: LanguageInfo[] = [
        { code: 'en', name: 'English', nativeName: 'English' },
        { code: 'tr', name: 'Turkish', nativeName: 'Türkçe' },
        { code: 'es', name: 'Spanish', nativeName: 'Español' },
        { code: 'fr', name: 'French', nativeName: 'Français' },
        { code: 'de', name: 'German', nativeName: 'Deutsch' },
        { code: 'it', name: 'Italian', nativeName: 'Italiano' },
        { code: 'pt', name: 'Portuguese', nativeName: 'Português' },
        { code: 'ru', name: 'Russian', nativeName: 'Русский' },
        { code: 'ja', name: 'Japanese', nativeName: '日本語' },
        { code: 'ko', name: 'Korean', nativeName: '한국어' },
        { code: 'zh', name: 'Chinese', nativeName: '中文' },
        { code: 'ar', name: 'Arabic', nativeName: 'العربية' },
        { code: 'hi', name: 'Hindi', nativeName: 'हिन्दी' },
        { code: 'nl', name: 'Dutch', nativeName: 'Nederlands' },
        { code: 'sv', name: 'Swedish', nativeName: 'Svenska' },
        { code: 'da', name: 'Danish', nativeName: 'Dansk' },
        { code: 'no', name: 'Norwegian', nativeName: 'Norsk' },
        { code: 'fi', name: 'Finnish', nativeName: 'Suomi' },
        { code: 'pl', name: 'Polish', nativeName: 'Polski' },
        { code: 'cs', name: 'Czech', nativeName: 'Čeština' },
        { code: 'sk', name: 'Slovak', nativeName: 'Slovenčina' },
        { code: 'hu', name: 'Hungarian', nativeName: 'Magyar' },
        { code: 'ro', name: 'Romanian', nativeName: 'Română' },
        { code: 'bg', name: 'Bulgarian', nativeName: 'Български' },
        { code: 'hr', name: 'Croatian', nativeName: 'Hrvatski' },
        { code: 'sr', name: 'Serbian', nativeName: 'Српски' },
        { code: 'sl', name: 'Slovenian', nativeName: 'Slovenščina' },
        { code: 'et', name: 'Estonian', nativeName: 'Eesti' },
        { code: 'lv', name: 'Latvian', nativeName: 'Latviešu' },
        { code: 'lt', name: 'Lithuanian', nativeName: 'Lietuvių' },
        { code: 'uk', name: 'Ukrainian', nativeName: 'Українська' },
        { code: 'be', name: 'Belarusian', nativeName: 'Беларуская' },
        { code: 'mk', name: 'Macedonian', nativeName: 'Македонски' },
        { code: 'mt', name: 'Maltese', nativeName: 'Malti' },
        { code: 'is', name: 'Icelandic', nativeName: 'Íslenska' },
        { code: 'ga', name: 'Irish', nativeName: 'Gaeilge' },
        { code: 'cy', name: 'Welsh', nativeName: 'Cymraeg' },
        { code: 'eu', name: 'Basque', nativeName: 'Euskera' },
        { code: 'ca', name: 'Catalan', nativeName: 'Català' },
        { code: 'gl', name: 'Galician', nativeName: 'Galego' },
        { code: 'pt-br', name: 'Brazilian Portuguese', nativeName: 'Português (Brasil)' },
        { code: 'zh-cn', name: 'Simplified Chinese', nativeName: '简体中文' },
        { code: 'zh-tw', name: 'Traditional Chinese', nativeName: '繁體中文' },
        { code: 'he', name: 'Hebrew', nativeName: 'עברית' },
        { code: 'th', name: 'Thai', nativeName: 'ไทย' },
        { code: 'vi', name: 'Vietnamese', nativeName: 'Tiếng Việt' },
        { code: 'id', name: 'Indonesian', nativeName: 'Bahasa Indonesia' },
        { code: 'ms', name: 'Malay', nativeName: 'Bahasa Melayu' },
        { code: 'tl', name: 'Filipino', nativeName: 'Filipino' },
        { code: 'sw', name: 'Swahili', nativeName: 'Kiswahili' },
        { code: 'am', name: 'Amharic', nativeName: 'አማርኛ' },
        { code: 'bn', name: 'Bengali', nativeName: 'বাংলা' },
        { code: 'gu', name: 'Gujarati', nativeName: 'ગુજરાતી' },
        { code: 'kn', name: 'Kannada', nativeName: 'ಕನ್ನಡ' },
        { code: 'ml', name: 'Malayalam', nativeName: 'മലയാളം' },
        { code: 'mr', name: 'Marathi', nativeName: 'मराठी' },
        { code: 'ne', name: 'Nepali', nativeName: 'नेपाली' },
        { code: 'or', name: 'Odia', nativeName: 'ଓଡ଼ିଆ' },
        { code: 'pa', name: 'Punjabi', nativeName: 'ਪੰਜਾਬੀ' },
        { code: 'si', name: 'Sinhala', nativeName: 'සිංහල' },
        { code: 'ta', name: 'Tamil', nativeName: 'தமிழ்' },
        { code: 'te', name: 'Telugu', nativeName: 'తెలుగు' },
        { code: 'ur', name: 'Urdu', nativeName: 'اردو' },
        { code: 'fa', name: 'Persian', nativeName: 'فارسی' },
        { code: 'ps', name: 'Pashto', nativeName: 'پښتو' },
        { code: 'sd', name: 'Sindhi', nativeName: 'سنڌي' },
        { code: 'ug', name: 'Uyghur', nativeName: 'ئۇيغۇرچە' },
        { code: 'uz', name: 'Uzbek', nativeName: 'Oʻzbekcha' },
        { code: 'kk', name: 'Kazakh', nativeName: 'Қазақша' },
        { code: 'ky', name: 'Kyrgyz', nativeName: 'Кыргызча' },
        { code: 'tg', name: 'Tajik', nativeName: 'Тоҷикӣ' },
        { code: 'tk', name: 'Turkmen', nativeName: 'Türkmençe' },
        { code: 'mn', name: 'Mongolian', nativeName: 'Монгол' },
        { code: 'my', name: 'Myanmar', nativeName: 'မြန်မာ' },
        { code: 'km', name: 'Khmer', nativeName: 'ខ្មែរ' },
        { code: 'lo', name: 'Lao', nativeName: 'ລາວ' }
    ];

    /**
     * Get all supported languages
     */
    static getAllLanguages(): LanguageInfo[] {
        return [...this.languages];
    }

    /**
     * Get language info by code
     */
    static getLanguageByCode(code: string): LanguageInfo | undefined {
        return this.languages.find(lang => lang.code.toLowerCase() === code.toLowerCase());
    }

    /**
     * Get language name by code
     */
    static getLanguageName(code: string): string {
        const language = this.getLanguageByCode(code);
        return language ? language.name : code.toUpperCase();
    }

    /**
     * Get native language name by code
     */
    static getNativeLanguageName(code: string): string {
        const language = this.getLanguageByCode(code);
        return language ? language.nativeName : code.toUpperCase();
    }

    /**
     * Check if language code is supported
     */
    static isSupported(code: string): boolean {
        return this.languages.some(lang => lang.code.toLowerCase() === code.toLowerCase());
    }

    /**
     * Get language codes only
     */
    static getLanguageCodes(): string[] {
        return this.languages.map(lang => lang.code);
    }

    /**
     * Get languages formatted for dropdown
     */
    static getLanguagesForDropdown(): Array<{ label: string; value: string }> {
        return this.languages.map(lang => ({
            label: `${lang.name} (${lang.nativeName})`,
            value: lang.code
        }));
    }

    /**
     * Search languages by name or code
     */
    static searchLanguages(query: string): LanguageInfo[] {
        const searchTerm = query.toLowerCase();
        return this.languages.filter(lang =>
            lang.code.toLowerCase().includes(searchTerm) ||
            lang.name.toLowerCase().includes(searchTerm) ||
            lang.nativeName.toLowerCase().includes(searchTerm)
        );
    }
} 