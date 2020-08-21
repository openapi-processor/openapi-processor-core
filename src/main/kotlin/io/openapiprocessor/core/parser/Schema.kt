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

package io.openapiprocessor.core.parser

import java.math.BigDecimal

/**
 * OpenAPI Schema abstraction.
 *
 * @author Martin Hauner
 */
interface Schema {

    fun getType(): String?
    fun getFormat(): String?

    // $ref
    fun getRef(): String?

    // array
    fun getItem(): Schema

    // object
    fun getProperties(): Map<String, Schema>

    // composed object
    fun getItems(): List<Schema>
    fun itemsOf(): String?

    // enum
    fun getEnum(): List<*>

    fun getDefault(): Any?
    fun isDeprecated(): Boolean?   // todo not null
    fun getNullable(): Boolean?    // todo not null
    fun getMinLength(): Int?
    fun getMaxLength(): Int?
    fun getMinItems(): Int?
    fun getMaxItems(): Int?
    fun getMaximum (): Number?
    fun isExclusiveMaximum (): Boolean?  // todo not null
    fun getMinimum (): Number?
    fun isExclusiveMinimum (): Boolean?  // todo not null

}
