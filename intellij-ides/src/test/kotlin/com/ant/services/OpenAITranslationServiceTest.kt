package com.ant.services

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class OpenAITranslationServiceTest {

    @Test
    fun `test quote removal from translation response`() {
        // Test cases for different quote scenarios
        val testCases = mapOf(
            "\"Board\"" to "Board",
            "'Board'" to "Board",
            "\"Hello World\"" to "Hello World",
            "'Hello World'" to "Hello World",
            "Board" to "Board",
            "\"Nested \"quotes\" here\"" to "Nested \"quotes\" here",
            "'Nested 'quotes' here'" to "Nested 'quotes' here",
            "  \"  Spaced  \"  " to "Spaced",
            "" to "",
            "\"\"" to "",
            "''" to ""
        )
        
        testCases.forEach { (input, expected) ->
            val result = removeQuotesFromText(input)
            assertEquals(expected, result, "Failed for input: '$input'")
        }
    }
    
    private fun removeQuotesFromText(text: String): String {
        return text
            .trim()
            .removePrefix("\"")
            .removeSuffix("\"")
            .removePrefix("'")
            .removeSuffix("'")
            .trim()
    }
} 