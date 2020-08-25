/*
 * Copyright 2020 the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.openapiprocessor.core

import io.kotest.core.Tag
import io.kotest.core.Tags
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.TagExtension

object WindowsOnly: Tag()
object OtherOnly: Tag()

object SystemTagExtension: TagExtension {

    override fun tags(): Tags {
        return if(isWindows()) {
            Tags.include(WindowsOnly)
                .exclude(OtherOnly)
        } else {
            Tags.include(OtherOnly)
                .exclude(WindowsOnly)
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
