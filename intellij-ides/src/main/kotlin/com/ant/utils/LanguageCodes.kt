package com.ant.utils

/**
 * ISO 639-1 language codes for translation
 */
object LanguageCodes {
    val LANGUAGES = mapOf(
        "af" to "Afrikaans",
        "ar" to "Arabic",
        "az" to "Azerbaijani",
        "be" to "Belarusian",
        "bg" to "Bulgarian",
        "bn" to "Bengali",
        "bs" to "Bosnian",
        "ca" to "Catalan",
        "cs" to "Czech",
        "cy" to "Welsh",
        "da" to "Danish",
        "de" to "German",
        "el" to "Greek",
        "en" to "English",
        "es" to "Spanish",
        "et" to "Estonian",
        "eu" to "Basque",
        "fa" to "Persian",
        "fi" to "Finnish",
        "fr" to "French",
        "ga" to "Irish",
        "gl" to "Galician",
        "gu" to "Gujarati",
        "he" to "Hebrew",
        "hi" to "Hindi",
        "hr" to "Croatian",
        "hu" to "Hungarian",
        "hy" to "Armenian",
        "id" to "Indonesian",
        "is" to "Icelandic",
        "it" to "Italian",
        "ja" to "Japanese",
        "ka" to "Georgian",
        "kk" to "Kazakh",
        "km" to "Khmer",
        "kn" to "Kannada",
        "ko" to "Korean",
        "ky" to "Kyrgyz",
        "la" to "Latin",
        "lt" to "Lithuanian",
        "lv" to "Latvian",
        "mk" to "Macedonian",
        "ml" to "Malayalam",
        "mn" to "Mongolian",
        "mr" to "Marathi",
        "ms" to "Malay",
        "mt" to "Maltese",
        "my" to "Myanmar",
        "ne" to "Nepali",
        "nl" to "Dutch",
        "no" to "Norwegian",
        "pa" to "Punjabi",
        "pl" to "Polish",
        "pt" to "Portuguese",
        "ro" to "Romanian",
        "ru" to "Russian",
        "si" to "Sinhala",
        "sk" to "Slovak",
        "sl" to "Slovenian",
        "sq" to "Albanian",
        "sr" to "Serbian",
        "sv" to "Swedish",
        "sw" to "Swahili",
        "ta" to "Tamil",
        "te" to "Telugu",
        "th" to "Thai",
        "tl" to "Filipino",
        "tr" to "Turkish",
        "uk" to "Ukrainian",
        "ur" to "Urdu",
        "uz" to "Uzbek",
        "vi" to "Vietnamese",
        "zh" to "Chinese"
    )
    
    fun getLanguageName(code: String): String {
        return LANGUAGES[code] ?: code.uppercase()
    }
    
    fun getLanguageOptions(): Array<String> {
        return LANGUAGES.map { "${it.key} - ${it.value}" }.toTypedArray()
    }
    
    fun getCodeFromOption(option: String): String {
        return option.split(" - ")[0]
    }
} 