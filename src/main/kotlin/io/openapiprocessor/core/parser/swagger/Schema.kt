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

package io.openapiprocessor.core.parser.swagger

import io.openapiprocessor.core.parser.Schema as ParserSchema
import io.swagger.v3.oas.models.media.Schema as SwaggerSchema
import io.swagger.v3.oas.models.media.ArraySchema as SwaggerArraySchema
import io.swagger.v3.oas.models.media.ComposedSchema as SwaggerComposedSchema

/**
 * Swagger Schema abstraction.
 *
 * @author Martin Hauner
 */
class Schema(private val schema: SwaggerSchema<*>): ParserSchema {

    override fun getType(): String? {
        if (itemsOf () != null) {
            return "composed"
        }
        return schema.type
    }

    override fun getFormat(): String? = schema.format

    override fun getRef(): String? = schema.`$ref`

    override fun getEnum(): List<*> {
        if (schema.enum == null) {
            return emptyList<Any>()
        }
        return schema.enum
    }

    override fun getItem(): ParserSchema = Schema((schema as SwaggerArraySchema).items)

    override fun getProperties(): Map<String, ParserSchema> {
        val props = LinkedHashMap<String, ParserSchema> ()

        schema.properties?.forEach { (key: String, value: SwaggerSchema<Any>) ->
            props[key] = Schema (value)
        }

        return props
    }

    override fun getItems(): List<ParserSchema> {
        val result: MutableList<ParserSchema> = mutableListOf()

        if(schema !is SwaggerComposedSchema) {
            return result
        }

        schema.allOf?.forEach {
            result.add(Schema(it))
        }

        schema.anyOf?.forEach {
            result.add(Schema(it))
        }

        schema.oneOf?.forEach {
            result.add(Schema(it))
        }

        return result
    }

    override fun itemsOf(): String? {
        if(schema !is SwaggerComposedSchema) {
            return null
        }

        if (schema.allOf != null) {
            return "allOf"
        }

        if (schema.anyOf != null) {
            return "anyOf"
        }

        if (schema.oneOf != null) {
            return "oneOf"
        }

        return null
    }

    override fun getDefault(): Any? = schema.default

    override fun isDeprecated(): Boolean = schema.deprecated ?: false

    override fun getRequired(): List<String> = schema.required ?: emptyList()

    override fun getNullable(): Boolean = schema.nullable ?: false

    override fun getMinLength(): Int? = schema.minLength ?: 0

    override fun getMaxLength(): Int? = schema.maxLength

    override fun getMinItems(): Int? = schema.minItems ?: 0

    override fun getMaxItems(): Int? = schema.maxItems

    override fun getMaximum(): Number? = schema.maximum

    override fun isExclusiveMaximum(): Boolean = schema.exclusiveMaximum ?: false

    override fun getMinimum(): Number? = schema.minimum

    override fun isExclusiveMinimum(): Boolean = schema.exclusiveMinimum ?: false

}
