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

package io.openapiprocessor.core.processor.mapping.v2

/**
 * the "map:" entry in the mapping yaml
 *
 *  @author Martin Hauner
 */
data class Map(

    /**
     * global result mapping
     */
    val result: String? = null,

    /**
     * single mapping, e.g. Mono<>
     */
    val single: String? = null,

    /**
     * multi mapping, e.g. Flux<>
     */
    val multi: String? = null,

    /**
     * null wrapper, e.g. JsonNullable<>
     */
    //val `null`: String? = null,

    /**
     * global type mappings
     */
    val types: List<Type> = emptyList(),

    /**
     * global parameter mappings
     */
    val parameters: List<Parameter> = emptyList(),

    /**
     * global response mappings
     */
    val responses: List<Response> = emptyList(),

    /**
     * endpoint mappings
     *
     * the LinkedHashMap preserves order
     */
    val paths: LinkedHashMap<String, Path> = LinkedHashMap()

)
