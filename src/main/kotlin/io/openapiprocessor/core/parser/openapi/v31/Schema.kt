/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v31

import io.openapiparser.model.v31.Schema as Schema31
import io.openapiprocessor.core.parser.Schema as ParserSchema

/**
 * openapi-parser Schema abstraction.
 */
class Schema(val schema: Schema31) : ParserSchema {

    override fun getType(): String? {
        if (itemsOf () != null) {  // check ??
            return "composed"
        }

        if (schema.isRef) {
            return null
        }

        /*
            schema.type (required) throws if there is no type, this is a workaround
            for the (not valid openapi) schema-composed-allof-notype test case

            properties:
              foo:
                allOf:
                  - readOnly: true
                  - $ref: '#/components/schemas/Foo'

         */

        if (!schema.hasProperty("type")) {
            return null
        }

        return schema.type.first { it != "null" }
    }

    override fun getFormat(): String? = schema.format

    override fun getRef(): String? {
        if (!schema.isRef) {
            return null
        }

        return schema.ref
    }

    override fun getEnum(): List<*> {
        if (schema.enum == null) {
            return emptyList<Any>()
        }

        val values = schema.enum
        return values!!.toList()
    }

    override fun getItem(): ParserSchema = Schema(schema.items!!) // todo check !!

    override fun getProperties(): Map<String, ParserSchema> {
        val props = LinkedHashMap<String, ParserSchema> ()

        schema.properties.forEach { (key: String, entry: Schema31) ->
            props[key] = Schema (entry)
        }

        return props
    }

    override fun getItems(): List<ParserSchema> {
        val result: MutableList<ParserSchema> = mutableListOf()

        schema.allOf.forEach {
            result.add(Schema(it))
        }

        schema.anyOf.forEach {
            result.add(Schema(it))
        }

        schema.oneOf.forEach {
            result.add(Schema(it))
        }

        return result
    }

    override fun itemsOf(): String? {
        if (schema.allOf.isNotEmpty()) {
            return "allOf"
        }

        if (schema.anyOf.isNotEmpty()) {
            return "anyOf"
        }

        if (schema.oneOf.isNotEmpty()) {
            return "oneOf"
        }

        return null
    }

    override fun getDefault(): Any? = schema.default

    override val description: String? = schema.description

    override fun isDeprecated(): Boolean = schema.deprecated

    override fun getRequired(): List<String> = schema.required.toList()

    override fun getNullable(): Boolean {
        return schema.type.contains("null")
    }

    override fun getMinLength(): Int? = schema.minLength?.toInt() ?: 0

    override fun getMaxLength(): Int? = schema.maxLength?.toInt()

    override fun getMinItems(): Int? = schema.minItems

    override fun getMaxItems(): Int? = schema.maxItems

    override fun getMaximum (): Number? = schema.maximum

    override fun isExclusiveMaximum(): Boolean = schema.exclusiveMaximum

    override fun getMinimum(): Number? = schema.minimum

    override fun isExclusiveMinimum(): Boolean = schema.exclusiveMinimum

    override val pattern: String?
        get() = schema.pattern

    override val readOnly: Boolean
        get() = schema.readOnly

    override val writeOnly: Boolean
        get() = schema.writeOnly

}
