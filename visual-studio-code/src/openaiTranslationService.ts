import axios from 'axios';

export class OpenAITranslationService {
    private readonly baseUrl = 'https://api.openai.com/v1/chat/completions';

    /**
     * Translate text using OpenAI GPT-4
     */
    async translateText(
        text: string,
        sourceLanguage: string,
        targetLanguage: string,
        apiKey: string
    ): Promise<string | null> {
        if (!apiKey || !text.trim()) {
            return null;
        }

        return await this.translateWithRetry(text, sourceLanguage, targetLanguage, apiKey, 3);
    }

    /**
     * Translate with retry mechanism
     */
    private async translateWithRetry(
        text: string,
        sourceLanguage: string,
        targetLanguage: string,
        apiKey: string,
        maxRetries: number
    ): Promise<string | null> {
        for (let attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                const prompt = this.createTranslationPrompt(text, sourceLanguage, targetLanguage);
                
                const response = await axios.post(
                    this.baseUrl,
                    {
                        model: 'gpt-4',
                        messages: [
                                                    {
                            role: 'system',
                            content: 'You are a professional translator. Translate accurately while preserving the EXACT formatting of the source text. CRITICAL: Match the exact capitalization pattern and punctuation of the source. If source starts lowercase, translation must start lowercase. If source has no period, translation must have no period. Return ONLY the translated text without quotes or additional formatting.'
                        },
                            {
                                role: 'user',
                                content: prompt
                            }
                        ],
                        max_tokens: 1000,
                        temperature: 0.3
                    },
                    {
                        headers: {
                            'Authorization': `Bearer ${apiKey}`,
                            'Content-Type': 'application/json'
                        },
                        timeout: 30000
                    }
                );

                let translatedText = response.data.choices[0]?.message?.content?.trim();
                
                // Clean up the response - remove quotes and escape characters
                if (translatedText) {
                    // Remove surrounding quotes if present
                    if ((translatedText.startsWith('"') && translatedText.endsWith('"')) ||
                        (translatedText.startsWith("'") && translatedText.endsWith("'"))) {
                        translatedText = translatedText.slice(1, -1);
                    }
                    
                    // Remove escape characters
                    translatedText = translatedText.replace(/\\"/g, '"').replace(/\\'/g, "'");
                }
                
                return translatedText || null;

            } catch (error) {
                console.error(`OpenAI translation error (attempt ${attempt}/${maxRetries}):`, error);
                
                if (axios.isAxiosError(error)) {
                    const status = error.response?.status;
                    
                    // Don't retry for these errors
                    if (status === 401) {
                        throw new Error('Invalid OpenAI API key');
                    }
                    
                    // Retry for these errors
                    if (status === 429 || status === 500 || status === 502 || status === 503 || status === 504) {
                        if (attempt < maxRetries) {
                            const delay = Math.min(1000 * Math.pow(2, attempt - 1), 10000); // Exponential backoff, max 10s
                            console.log(`Retrying in ${delay}ms... (attempt ${attempt + 1}/${maxRetries})`);
                            await this.sleep(delay);
                            continue;
                        } else {
                            throw new Error(`OpenAI API temporarily unavailable (${status}). Please try again later.`);
                        }
                    }
                    
                    throw new Error(`OpenAI API error: ${status || 'Unknown'}`);
                }
                
                // For non-axios errors, don't retry
                throw new Error(`Translation failed: ${error}`);
            }
        }
        
        return null;
    }

    /**
     * Sleep utility for retry delays
     */
    private sleep(ms: number): Promise<void> {
        return new Promise(resolve => setTimeout(resolve, ms));
    }

    /**
     * Create translation prompt
     */
    private createTranslationPrompt(text: string, sourceLanguage: string, targetLanguage: string): string {
        const sourceLanguageName = this.getLanguageName(sourceLanguage);
        const targetLanguageName = this.getLanguageName(targetLanguage);
        
        return `Translate the following text from ${sourceLanguageName} to ${targetLanguageName}:

${text}

CRITICAL formatting rules:
- Preserve EXACT capitalization pattern of the source text (if source starts lowercase, translation must start lowercase)
- Preserve EXACT punctuation of the source text (if source has no period, translation must have no period)
- Keep the same tone and style as the source
- If it's a UI text, make it natural for the target language while maintaining formatting
- Return ONLY the translated text without quotes or any additional formatting
- Do not add punctuation that doesn't exist in the source
- Do not change capitalization from the source pattern`;
    }

    /**
     * Get language name from language code
     */
    private getLanguageName(languageCode: string): string {
        const languageNames: { [key: string]: string } = {
            'en': 'English',
            'tr': 'Turkish',
            'es': 'Spanish',
            'fr': 'French',
            'de': 'German',
            'it': 'Italian',
            'pt': 'Portuguese',
            'ru': 'Russian',
            'ja': 'Japanese',
            'ko': 'Korean',
            'zh': 'Chinese',
            'ar': 'Arabic',
            'hi': 'Hindi',
            'nl': 'Dutch',
            'sv': 'Swedish',
            'da': 'Danish',
            'no': 'Norwegian',
            'fi': 'Finnish',
            'pl': 'Polish',
            'cs': 'Czech',
            'sk': 'Slovak',
            'hu': 'Hungarian',
            'ro': 'Romanian',
            'bg': 'Bulgarian',
            'hr': 'Croatian',
            'sr': 'Serbian',
            'sl': 'Slovenian',
            'et': 'Estonian',
            'lv': 'Latvian',
            'lt': 'Lithuanian',
            'uk': 'Ukrainian',
            'be': 'Belarusian',
            'mk': 'Macedonian',
            'mt': 'Maltese',
            'is': 'Icelandic',
            'ga': 'Irish',
            'cy': 'Welsh',
            'eu': 'Basque',
            'ca': 'Catalan',
            'gl': 'Galician',
            'pt-br': 'Brazilian Portuguese',
            'zh-cn': 'Simplified Chinese',
            'zh-tw': 'Traditional Chinese'
        };

        return languageNames[languageCode.toLowerCase()] || languageCode.toUpperCase();
    }

    /**
     * Test API key validity
     */
    async testApiKey(apiKey: string): Promise<boolean> {
        if (!apiKey) {
            return false;
        }

        try {
            const response = await axios.post(
                this.baseUrl,
                {
                    model: 'gpt-3.5-turbo',
                    messages: [
                        {
                            role: 'user',
                            content: 'Hello'
                        }
                    ],
                    max_tokens: 5
                },
                {
                    headers: {
                        'Authorization': `Bearer ${apiKey}`,
                        'Content-Type': 'application/json'
                    },
                    timeout: 10000
                }
            );

            return response.status === 200;
        } catch {
            return false;
        }
    }
} 