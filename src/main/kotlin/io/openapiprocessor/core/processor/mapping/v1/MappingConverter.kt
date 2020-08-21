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

package io.openapiprocessor.core.processor.mapping.v1

import io.openapiprocessor.core.converter.mapping.AddParameterTypeMapping
import io.openapiprocessor.core.converter.mapping.EndpointTypeMapping
import io.openapiprocessor.core.converter.mapping.ParameterTypeMapping
import io.openapiprocessor.core.converter.mapping.ResponseTypeMapping
import io.openapiprocessor.core.converter.mapping.ResultTypeMapping
import io.openapiprocessor.core.converter.mapping.TypeMapping
import io.openapiprocessor.core.converter.mapping.Mapping

import io.openapiprocessor.core.processor.mapping.v1.Mapping as MappingV1

/**
 *  Converter for the type mapping from the mapping yaml. It converts the type mapping information
 *  into the format used by {@link com.github.hauner.openapi.core.converter.DataTypeConverter}.
 *
 *  @author Martin Hauner
 */
@Deprecated("replaced by mapping.v2")
class MappingConverter {
    companion object {
        private const val SEPARATOR_FORMAT = ":"
        private val PATTERN_GENERICS = "(.+?)<(.+?)>".toPattern()
    }

    fun convert(mapping: MappingV1): List<Mapping> {
        val result = ArrayList<Mapping>()

        mapping.map.types.forEach {
            result.add(convertType(it))
        }

        if(mapping.map.result != null) {
            result.add(convertResult(mapping.map.result))
        }

        mapping.map.parameters.forEach {
            result.add (convertParameter (it))
        }

        mapping.map.responses.forEach {
            result.add (convertResponse (it))
        }

        mapping.map.paths.forEach {
            result.add(convertPath (it.key, it.value))
        }

        return result
    }

    private fun convertType(type: Type): TypeMapping {
        val matcher = PATTERN_GENERICS.matcher(type.to)

        var split = emptyList<String>()
        if (type.from != null) {
            split = type.from.split(SEPARATOR_FORMAT)
        }

        val from: String? = if (split.isNotEmpty()) split.component1() else null
        val format: String? = if (split.size >= 2) split.component2() else null

        var to: String = type.to
        var generics: List<String> = emptyList()

        // has inline generics
        if (matcher.find ()) {
            to = matcher
                .group(1)
            generics = matcher
                .group(2)
                .split(',')
                .map { it.trim() }
                .toList()

        // has explicit generic list
        } else if (!type.generics.isNullOrEmpty()) {
            generics = type.generics
        }

        return TypeMapping(from, format, to, generics)
    }

    private fun convertResult(result: Result): Mapping {
        return ResultTypeMapping (result.to)
    }

    private fun convertParameter(source: Parameter): Mapping {
        if (source is RequestParameter) {
            val name = source.name
            val mapping = convertType(Type(
                null,
                source.to,
                source.generics
            ))
            return ParameterTypeMapping(name, mapping)

        } else if (source is AdditionalParameter) {
            val name = source.add
            val mapping = convertType ( Type(
                null,
                source.to,
                source.generics
            ))
            return AddParameterTypeMapping (name, mapping)

        } else {
            throw Exception("unknown parameter mapping $source")
        }
    }

    private fun convertResponse(source: Response): ResponseTypeMapping  {
        val content = source.content
        val mapping = convertType (Type(
            null,
            source.to,
            source.generics
        ))
        return ResponseTypeMapping(content, mapping)
    }

    private fun convertPath(path: String, source: Path): EndpointTypeMapping {
        val result = mutableListOf<Mapping>()

        source.types.forEach {
            result.add(convertType(it))
        }

        if(source.result != null) {
            result.add(convertResult(source.result))
        }

        source.parameters.forEach {
            result.add (convertParameter (it))
        }

        source.responses.forEach {
            result.add (convertResponse (it))
        }

        return EndpointTypeMapping(path, result, source.exclude)
    }

}
