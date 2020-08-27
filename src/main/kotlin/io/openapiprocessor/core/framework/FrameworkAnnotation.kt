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

package io.openapiprocessor.core.framework

/**
 * details of an annotation.
 *
 * @author Martin Hauner
 */
open class FrameworkAnnotation(

    /**
     * The plain name of the annotation of this parameter (ie. without the @).
     */
    private val name: String,

    private val pkg: String

) {

    /**
     * The fully qualified class name of the annotation.
     */
    val fullyQualifiedName: String
        get() = "${pkg}.${name}"

    /**
     * The full annotation name with a leading @.
     */
    val annotationName: String
        get() = "@${name}"

}
