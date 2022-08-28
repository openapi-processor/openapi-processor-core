/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.converter.mapping.AnnotationTypeMapping
import io.openapiprocessor.core.converter.mapping.EndpointTypeMapping
import io.openapiprocessor.core.converter.mapping.Mapping
import io.openapiprocessor.core.converter.mapping.ParameterAnnotationTypeMapping
import io.openapiprocessor.core.model.HttpMethod

/**
 * find mappings in the type mapping list.
 */
class MappingFinder(private val typeMappings: List<Mapping> = emptyList()) {

    fun findParameterAnnotations(path: String, method: HttpMethod?, typeName: String)
    : List<AnnotationTypeMapping> {

        val epMappings = findEndpointMappings(typeMappings, path, method)
        if (epMappings.isNotEmpty()) {
            val am = findParameterAnnotations(epMappings, typeName)
            if (am.isNotEmpty())
                return am
        }

        return findParameterAnnotations(typeMappings, typeName)
    }

    private fun findEndpointMappings(typeMappings: List<Mapping>, path: String, method: HttpMethod?)
        : List<Mapping> {

        // find with method
        var epMappings = typeMappings
            .filterIsInstance<EndpointTypeMapping>()
            .filter { it.path == path && it.method == method }

        // find without method
        if (epMappings.isEmpty()) {
            epMappings = typeMappings
                .filterIsInstance<EndpointTypeMapping>()
                .filter { it.path == path && it.method == null }
        }

        return epMappings
            .map { it.getChildMappings() }
            .flatten()
    }

    private fun findParameterAnnotations(typeMappings: List<Mapping>, typeName: String)
    : List<AnnotationTypeMapping> {
        return typeMappings
            .filterIsInstance<ParameterAnnotationTypeMapping>()
            .filter { it.sourceTypeName == typeName }
            .map { it.annotationTypeMapping }
    }
}
