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

package io.openapiprocessor.core.parser.openapi4j

import io.openapiprocessor.core.parser.Schema as ParserSchema
import org.openapi4j.parser.model.v3.Schema as O4jSchema

/**
 * openapi4j Schema abstraction.
 *
 * @author Martin Hauner
 */
class Schema(val schema: O4jSchema) : ParserSchema {

    override fun getType(): String? {
        if (itemsOf () != null) {
            return "composed"
        }
        return schema.type
    }

    override fun getFormat(): String? = schema.format

    override fun getRef(): String? = schema.ref

    override fun getEnum(): List<*> {
        if (schema.enums == null) {
            return emptyList<Any>()
        }
        return schema.enums
    }

    override fun getItem(): ParserSchema = Schema(schema.itemsSchema)

    override fun getProperties(): Map<String, ParserSchema> {
        val props = LinkedHashMap<String, ParserSchema> ()

        schema.properties?.forEach { (key: String, entry: O4jSchema) ->
            props[key] = Schema (entry)
        }

        return props
    }

    override fun getItems(): List<ParserSchema> {
        val result: MutableList<ParserSchema> = mutableListOf()

        schema.allOfSchemas?.forEach {
            result.add(Schema(it))
        }

        schema.anyOfSchemas?.forEach {
            result.add(Schema(it))
        }

        schema.oneOfSchemas?.forEach {
            result.add(Schema(it))
        }

        return result
    }

    override fun itemsOf(): String? {
        if (schema.allOfSchemas != null) {
            return "allOf"
        }

        if (schema.anyOfSchemas != null) {
            return "anyOf"
        }

        if (schema.oneOfSchemas != null) {
            return "oneOf"
        }

        return null
    }

    override fun getDefault(): Any? = schema.default

    override fun isDeprecated(): Boolean = schema.deprecated ?: false

    override fun getRequired(): List<String> = schema.requiredFields ?: emptyList()

    override fun getNullable(): Boolean = schema.nullable ?: false

    override fun getMinLength(): Int? = schema.minLength ?: 0

    override fun getMaxLength(): Int? = schema.maxLength

    override fun getMinItems(): Int? = schema.minItems ?: 0

    override fun getMaxItems(): Int? = schema.maxItems

    override fun getMaximum (): Number? = schema.maximum

    override fun isExclusiveMaximum(): Boolean = schema.exclusiveMaximum ?: false

    override fun getMinimum(): Number? = schema.minimum

    override fun isExclusiveMinimum(): Boolean = schema.exclusiveMinimum ?: false

}
