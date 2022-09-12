package io.openapiprocessor.core.writer.java

import java.io.Writer

interface FileHandler {
    fun createApiWriter(packageName: String, className: String): Writer
    fun createModelWriter(packageName: String, className: String): Writer
    fun createTargetFolders()
}