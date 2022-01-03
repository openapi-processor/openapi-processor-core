/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

import io.kotest.core.Tag
import io.kotest.core.TagExpression
import io.kotest.core.TestConfiguration
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.TagExtension
import io.openapiprocessor.core.support.deleteRecursively
import java.nio.file.Files
import java.nio.file.Path

object Windows: Tag()
object NotWindows: Tag()

object SystemTagExtension: TagExtension {

    override fun tags(): TagExpression {

        return if(isWindows()) {
            TagExpression.exclude(NotWindows)
        } else {
            TagExpression.exclude(Windows)
        }
    }

    private fun isWindows(): Boolean {
        return System.getProperty("os.name")
            .lowercase()
            .contains("windows")
    }

}

/**
 * kotest config
 */
object ProjectConfig: AbstractProjectConfig() {
    override fun extensions() = listOf(SystemTagExtension)
}


fun TestConfiguration.tempFolder(prefix: String? = "oap-"): Path {
   val folder = Files.createTempDirectory(prefix)
   afterSpec {
       folder.deleteRecursively()
   }
   return folder
}

