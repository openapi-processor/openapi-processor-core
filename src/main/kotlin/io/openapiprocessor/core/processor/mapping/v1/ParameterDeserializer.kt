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
package io.openapiprocessor.core.processor.mapping.v1

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import java.io.IOException
import kotlin.collections.Map

/**
 * deserializer for parameter sub types
 *
 *  @author Martin Hauner
 */
@Deprecated("replaced by mapping.v2")
class ParameterDeserializer: StdDeserializer<Parameter>(Parameter::class.java) {

    @Suppress("UNCHECKED_CAST")
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Parameter {
        val props= ctxt?.readValue (p, Map::class.java)

        if (props != null && isRequestParameter(props)) {
            val name = props["name"] as String
            val to = props["to"] as String
            val generics: List<String>? = props["generics"] as List<String>?
            return RequestParameter(name, to, generics)
        }

        if (props != null && isAdditionalParameter(props)) {
            val add = props["add"] as String
            val az = props["as"] as String
            val generics: List<String>? = props["generics"] as List<String>?
            return AdditionalParameter(add, az, generics)
        }

        throw IOException("unknown parameter type at: " + p?.tokenLocation.toString ())
    }

    private fun isRequestParameter(source: Map<*, *>): Boolean {
        return source.contains("name") && source.containsKey ("to")
    }

    private fun isAdditionalParameter(source: Map<*, *>): Boolean {
        return source.contains("add") && source.containsKey ("as")
    }

}
