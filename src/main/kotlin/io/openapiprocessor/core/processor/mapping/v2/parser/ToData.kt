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

package io.openapiprocessor.core.processor.mapping.v2.parser

/**
 * extracted data from parser
 *
 * @author Martin Hauner
 */
data class ToData(
    /**
     * target type
     */
    val type: String,

    /**
     * target type generic arguments
     */
    var typeArguments: List<String> = emptyList(),

    /**
     * annotation on target type
     */
    var annotationType: String? = null,

    /**
     * arguments of annotation
     */
    var annotationArguments: String? = null

)
