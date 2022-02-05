/*
 * Copyright 2019-2020 the original authors
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

package io.openapiprocessor.core.converter.mapping

import java.lang.RuntimeException

/**
 * thrown when the DataTypeConverter hits an unknown data type.
 *
 * @author Martin Hauner
 */
class UnknownDataTypeException(
    val name: String?,
    val type: String?,
    var format: String?
): RuntimeException() {

    override val message: String
        get() = "unknown schema: $name of type $type${if(format != null) "/$format" else ""}"

}
