package com.ant.model

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VfsUtil
import java.io.File

/**
 * Represents a translation file in the project
 */
data class TranslationFile(val file: File) {
    /**
     * Creates a TranslationFile from a VirtualFile
     */
    constructor(virtualFile: VirtualFile) : this(VfsUtil.virtualToIoFile(virtualFile))
} 