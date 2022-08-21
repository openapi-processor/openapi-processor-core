/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2

import io.openapiprocessor.core.converter.mapping.*
import io.openapiprocessor.core.model.HttpMethod
import io.openapiprocessor.core.processor.mapping.v2.parser.Mapping.Kind.ANNOTATE
import io.openapiprocessor.core.processor.mapping.v2.parser.antlr.parseMapping
import io.openapiprocessor.core.processor.mapping.v2.Mapping as MappingV2

/**
 *  Converter for the type mapping from the mapping yaml. It converts the type mapping information
 *  into the format used by [io.openapiprocessor.core.converter.DataTypeConverter].
 */
class MappingConverter(val mapping: MappingV2) {

    fun convert(): List<Mapping> {
        val result = ArrayList<Mapping>()

        if(mapping.map.result != null) {
            result.add(convertResult(mapping.map.result))
        }

        if(mapping.map.resultStyle != null) {
            result.add(convertResultStyleOption(mapping.map.resultStyle))
        }

        if(mapping.map.single != null) {
            result.add(convertType("single" , mapping.map.single))
        }

        if(mapping.map.multi != null) {
            result.add(convertType("multi", mapping.map.multi))
        }

        //if(mapping.map.`null` != null) {
        //    result.add(convertNull(mapping.map.`null`))
        //}

        mapping.map.types.forEach {
            result.add(convertType(it))
        }

        mapping.map.parameters.forEach {
            result.add (convertParameter (it))
        }

        mapping.map.responses.forEach {
            result.add (convertResponse (it))
        }

        mapping.map.paths.forEach {
            result.add(convertPath (it.key, it.value))
            result.addAll(convertPathMethods(it.key, it.value))
        }

        return result
    }

    private fun convertResultStyleOption(value: ResultStyle): Mapping {
        return ResultStyleOptionMapping(value)
    }

    private fun convertResult (result: String): Mapping {
        val mapping = parseMapping(result)
        return ResultTypeMapping(resolvePackageVariable(mapping.targetType))
    }

    private fun convertNull(value: String): Mapping {
        val split = value
                .split(" = ")
                .map { it.trim() }

        val type = split.component1()
        var init: String? = null
        if (split.size == 2)
            init = split.component2()

        return NullTypeMapping("null", type, init)
    }

    private fun convertType (from: String, to: String): Mapping {
        val mapping = parseMapping(to)
        return TypeMapping(from, resolvePackageVariable(mapping.targetType))
    }

    private fun convertType(source: Type): Mapping {
        val mapping = parseMapping(source.type)

        val targetGenericTypes = mapping.targetGenericTypes
        if (targetGenericTypes.isEmpty() && source.generics != null) {
            targetGenericTypes.addAll(source.generics)
        }

        return if (mapping.kind == ANNOTATE) {
            AnnotationTypeMapping(
                mapping.sourceType,
                mapping.sourceFormat,
                Annotation(mapping.annotationType, null, mapping.annotationParameters)
            )
        } else {
            TypeMapping(
                mapping.sourceType,
                mapping.sourceFormat,
                resolvePackageVariable(mapping.targetType),
                resolvePackageVariable(targetGenericTypes)
            )
        }
    }

    private fun convertParameter(source: Parameter): Mapping {
        return when (source) {
            is RequestParameter -> {
                createParameterTypeMapping(source)
            }
            is AdditionalParameter -> {
                createAddParameterTypeMapping(source)
            }
            else -> {
                throw Exception("unknown parameter mapping $source")
            }
        }
    }

    private fun createParameterTypeMapping(source: RequestParameter): ParameterTypeMapping {
        val mapping = parseMapping(source.name)

        val targetGenericTypes = mapping.targetGenericTypes
        if (targetGenericTypes.isEmpty() && source.generics != null) {
            targetGenericTypes.addAll(source.generics)
        }

        val typeMapping = TypeMapping(
            null,
            null,
            resolvePackageVariable(mapping.targetType),
            resolvePackageVariable(targetGenericTypes)
        )

        return ParameterTypeMapping(mapping.sourceType, typeMapping)
    }

    private fun createAddParameterTypeMapping(source: AdditionalParameter): AddParameterTypeMapping {
        val mapping = parseMapping(source.add)

        val targetGenericTypes = mapping.targetGenericTypes
        if (targetGenericTypes.isEmpty() && source.generics != null) {
            targetGenericTypes.addAll(source.generics)
        }

        val typeMapping = TypeMapping(
            null,
            null,
            resolvePackageVariable(mapping.targetType),
            resolvePackageVariable(targetGenericTypes)
        )

        var annotation: io.openapiprocessor.core.converter.mapping.Annotation? = null
        if(mapping.annotationType != null) {
            annotation = Annotation(
                mapping.annotationType, null, mapping.annotationParameters)
        }

        return AddParameterTypeMapping(mapping.sourceType, typeMapping, annotation)
    }

    private fun convertResponse(source: Response): Mapping {
        val mapping = parseMapping(source.content)

        val targetGenericTypes = mapping.targetGenericTypes
        if (targetGenericTypes.isEmpty() && source.generics != null) {
            targetGenericTypes.addAll(source.generics)
        }

        val typeMapping = TypeMapping(
            null,
            null,
            resolvePackageVariable(mapping.targetType),
            resolvePackageVariable(targetGenericTypes)
        )

        return ResponseTypeMapping (mapping.sourceType, typeMapping)
    }

    private fun convertPath(path: String, source: Path): Mapping {
        val result = ArrayList<Mapping>()

        if(source.result != null) {
            result.add(convertResult(source.result))
        }

        if(source.single != null) {
            result.add(convertType("single" , source.single))
        }

        if(source.multi != null) {
            result.add(convertType("multi", source.multi))
        }

        if(source.`null` != null) {
            result.add(convertNull(source.`null`))
        }

        source.types.forEach {
            result.add(convertType(it))
        }

        source.parameters.forEach {
            result.add (convertParameter (it))
        }

        source.responses.forEach {
            result.add (convertResponse (it))
        }

        return EndpointTypeMapping(path, null, result, source.exclude)
    }

    private fun convertPathMethods(path: String, source: Path): List<Mapping> {
        val result = ArrayList<Mapping>()

        if (source.get != null) {
            result.add(convertPathMethod(path, HttpMethod.GET, source.get))
        }

        if (source.put != null) {
            result.add(convertPathMethod(path, HttpMethod.PUT, source.put))
        }

        if (source.post != null) {
            result.add(convertPathMethod(path, HttpMethod.POST, source.post))
        }

        if (source.delete != null) {
            result.add(convertPathMethod(path, HttpMethod.DELETE, source.delete))
        }

        if (source.options != null) {
            result.add(convertPathMethod(path, HttpMethod.OPTIONS, source.options))
        }

        if (source.head != null) {
            result.add(convertPathMethod(path, HttpMethod.HEAD, source.head))
        }

        if (source.patch != null) {
            result.add(convertPathMethod(path, HttpMethod.PATCH, source.patch))
        }

        if (source.trace != null) {
            result.add(convertPathMethod(path, HttpMethod.TRACE, source.trace))
        }

        return result
    }

    private fun convertPathMethod(path: String, method: HttpMethod, source: PathMethod): Mapping {
        val result = ArrayList<Mapping>()

        if(source.result != null) {
            result.add(convertResult(source.result))
        }

        if(source.single != null) {
            result.add(convertType("single" , source.single))
        }

        if(source.multi != null) {
            result.add(convertType("multi", source.multi))
        }

        if(source.`null` != null) {
            result.add(convertNull(source.`null`))
        }

        source.types.forEach {
            result.add(convertType(it))
        }

        source.parameters.forEach {
            result.add (convertParameter (it))
        }

        source.responses.forEach {
            result.add (convertResponse (it))
        }

        return EndpointTypeMapping(path, method, result, source.exclude)
    }

    private fun resolvePackageVariable(source: List<String>): List<String> {
        return source.map {
            resolvePackageVariable(it)
        }
    }

    private fun resolvePackageVariable(source: String): String {
        return source.replace("{package-name}", mapping.options.packageName)
    }
}
