package com.ant.utils

import com.intellij.openapi.diagnostic.Logger

/**
 * Utility class for logging
 */
object LoggerUtils {
    /**
     * Gets a logger for the specified class
     */
    fun getLogger(clazz: Class<*>): Logger {
        return Logger.getInstance(clazz)
    }
} 