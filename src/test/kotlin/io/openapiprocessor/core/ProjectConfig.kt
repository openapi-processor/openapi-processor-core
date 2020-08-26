/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

import io.kotest.core.Tag
import io.kotest.core.Tags
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.TagExtension

object Windows: Tag()
object NotWindows: Tag()

object SystemTagExtension: TagExtension {

    override fun tags(): Tags {

        return if(isWindows()) {
            Tags.exclude(NotWindows)
        } else {
            Tags.exclude(Windows)
        }
    }

    private fun isWindows(): Boolean {
        return System.getProperty("os.name")
            .toLowerCase()
            .contains("windows")
    }

}

object ProjectConfig: AbstractProjectConfig() {
    override fun extensions() = listOf(SystemTagExtension)
}
