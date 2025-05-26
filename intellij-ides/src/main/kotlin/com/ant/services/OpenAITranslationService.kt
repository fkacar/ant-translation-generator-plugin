package com.ant.services

import com.ant.utils.LoggerUtils.getLogger
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

/**
 * OpenAI API service for translation
 */
class OpenAITranslationService(private val apiKey: String) {
    private val logger = getLogger(javaClass)
    private val httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(30))
        .build()
    private val gson = Gson()
    
    /**
     * Translate text using OpenAI GPT-4
     */
    fun translateText(text: String, sourceLanguage: String, targetLanguage: String): String? {
        try {
            if (apiKey.isBlank()) {
                logger.warn("OpenAI API key is not configured")
                return null
            }
            
            val prompt = """
                Translate the following text from $sourceLanguage to $targetLanguage.
                Only return the translated text, nothing else.
                
                Text to translate: "$text"
            """.trimIndent()
            
            val requestBody = JsonObject().apply {
                addProperty("model", "gpt-4")
                add("messages", gson.toJsonTree(listOf(
                    mapOf("role" to "user", "content" to prompt)
                )))
                addProperty("max_tokens", 150)
                addProperty("temperature", 0.3)
            }
            
            val request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer $apiKey")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestBody)))
                .timeout(Duration.ofSeconds(30))
                .build()
            
            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
            
            if (response.statusCode() == 200) {
                val responseJson = gson.fromJson(response.body(), JsonObject::class.java)
                val choices = responseJson.getAsJsonArray("choices")
                if (choices.size() > 0) {
                    val message = choices[0].asJsonObject
                        .getAsJsonObject("message")
                    val rawTranslatedText = message.get("content").asString.trim()
                    
                    // Remove surrounding quotes if present
                    val translatedText = rawTranslatedText
                        .trim()
                        .removePrefix("\"")
                        .removeSuffix("\"")
                        .removePrefix("'")
                        .removeSuffix("'")
                        .trim()
                    
                    logger.info("Translation successful: '$text' -> '$translatedText' ($sourceLanguage -> $targetLanguage)")
                    return translatedText
                }
            } else {
                logger.warn("OpenAI API error: ${response.statusCode()} - ${response.body()}")
            }
        } catch (e: Exception) {
            logger.error("Error during translation: ${e.message}", e)
        }
        
        return null
    }
    
    /**
     * Test API key validity
     */
    fun testApiKey(): Boolean {
        return try {
            val testTranslation = translateText("Hello", "English", "Spanish")
            testTranslation != null
        } catch (e: Exception) {
            logger.warn("API key test failed: ${e.message}")
            false
        }
    }
} 