package com.ant.utils

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service

/**
 * Service class that manages translation cache
 */
@Service
class TranslationCacheService {
    private val cachedTranslations = mutableMapOf<String, String>()
    
    /**
     * Clears the cache
     */
    fun clearCache() {
        cachedTranslations.clear()
    }
    
    /**
     * Adds a translation to the cache
     */
    fun cacheTranslation(key: String, value: String) {
        cachedTranslations[key] = value
    }
    
    /**
     * Gets a translation from the cache
     */
    fun getTranslation(key: String): String? {
        return cachedTranslations[key]
    }
    
    companion object {
        /**
         * Gets the service instance
         */
        fun getInstance(): TranslationCacheService {
            return service()
        }
    }
} 