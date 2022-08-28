/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import java.io.IOException
import kotlin.collections.Map

/**
 * deserializer for parameter sub types
 */
class ParameterDeserializer : StdDeserializer<Parameter>(Parameter::class.java) {

    @Suppress("UNCHECKED_CAST")
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Parameter {
        val props = ctxt?.readValue(p, Map::class.java)

        if (props != null && isRequestParameter(props)) {
            val name = props["name"] as String
            val generics = props["generics"] as List<String>?

            return RequestParameter(name, generics)
        }

        if (props != null && isAdditionalParameter(props)) {
            val name = props["add"] as String
            val generics = props["generics"] as List<String>?

            return AdditionalParameter(name, generics)
        }

        if (props != null && isType(props)) {
            val type = props["type"] as String
            val generics = props["generics"] as List<String>?

            return Type(type, generics)
        }

        throw IOException("unknown parameter type at: " + p?.tokenLocation.toString ())
    }

    private fun isRequestParameter(source: Map<*, *>): Boolean {
        return source.contains("name")
    }

    private fun isAdditionalParameter(source: Map<*, *>): Boolean {
        return source.contains("add")
    }

    private fun isType(source: Map<*, *>): Boolean {
        return source.contains("type")
    }
}
