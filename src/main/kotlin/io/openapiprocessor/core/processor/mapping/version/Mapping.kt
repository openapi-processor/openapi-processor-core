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

package io.openapiprocessor.core.processor.mapping.version

import com.fasterxml.jackson.annotation.JsonProperty

data class Mapping(
        @JsonProperty("openapi-processor-mapping")
        val version: String? = null,

        @JsonProperty("openapi-processor-spring")
        val versionObsolete: String? = null
) {

    fun isV2(): Boolean {
        return getSafeVersion().startsWith("v2")
    }

    fun isDeprecatedVersionKey (): Boolean {
        return versionObsolete != null
    }

    private fun getSafeVersion(): String {
        if (version != null)
            return version

        if (versionObsolete != null)
            return versionObsolete

        return "no version"
    }

}
