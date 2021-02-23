/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.openapiprocessor.core.converter.SchemaInfo
import io.openapiprocessor.core.converter.mapping.matcher.*
import io.openapiprocessor.core.model.HttpMethod

/**
 * find mappings of a given schema info in the type mapping list.
 */
class MappingFinder(private val typeMappings: List<Mapping> = emptyList()) {

    /**
     * find a matching endpoint mapping for the given schema info.
     *
     * @param info schema info of the OpenAPI schema.
     * @return the matching mapping or null if there is no match.
     * @throws AmbiguousTypeMappingException if there is more than one match.
     */
    fun findEndpointTypeMapping(info: SchemaInfo): TypeMapping? {
        // check with method
        val m = findEndpointTypeMapping(info, info.getMethod())
        if (m != null)
            return m

        // check without method, i.e. all methods
        return findEndpointTypeMapping(info, null)
    }

    private fun findEndpointTypeMapping(info: SchemaInfo, method: HttpMethod?): TypeMapping? {
        val ep = filterMappings(EndpointTypeMatcher(info.getPath(), method), typeMappings)

        val parameter = getTypeMapping(filterMappings(ParameterTypeMatcher(info), ep))
        if (parameter != null)
            return parameter

        val response = getTypeMapping(filterMappings(ResponseTypeMatcher(info), ep))
        if (response != null)
            return response

        return getTypeMapping(filterMappings(TypeMatcher(info), ep))
    }

    /**
     * find a matching (global) parameter/response (io) mapping for the given schema info.
     *
     * @param info schema info of the OpenAPI schema.
     * @return the matching mapping or null if there is no match.
     * @throws AmbiguousTypeMappingException if there is more than one match.
     */
    fun findIoTypeMapping(info: SchemaInfo): TypeMapping? {
        val parameter = getTypeMapping(filterMappings(ParameterTypeMatcher(info), typeMappings))
        if (parameter != null)
            return parameter

        val response = getTypeMapping(filterMappings(ResponseTypeMatcher(info), typeMappings))
        if (response != null)
            return response

        return null
    }

    /**
     * find a matching (global) type mapping for the given schema info.
     *
     * @param info schema info of the OpenAPI schema.
     * @return the matching mapping or null if there is no match.
     * @throws AmbiguousTypeMappingException if there is more than one match.
     */
    fun findTypeMapping(info: SchemaInfo): TypeMapping? {
        return getTypeMapping(filterMappings(TypeMatcher(info), typeMappings))
    }

    /**
     * find all (endpoint) add parameter type mappings for the given schema info.
     *
     * @param path the endpoint path
     * @return the matching mapping or null if there is no match.
     * @throws AmbiguousTypeMappingException if there is more than one match.
     */
    fun findEndpointAddParameterTypeMappings(path: String, method: HttpMethod): List<AddParameterTypeMapping> {
        return filterMappings(EndpointTypeMatcher(path, method), typeMappings)
            .filterIsInstance<AddParameterTypeMapping>()
    }

    /**
     * find (endpoint) "result" type mappings.
     *
     * @param info schema info of the OpenAPI schema.
     * @return the "result" type mappings or null if there is no match.
     */
    fun findEndpointResultTypeMapping(info: SchemaInfo): ResultTypeMapping? {
        val ep = filterMappings(EndpointTypeMatcher(info.getPath(), null/*info.getMethod()*/), typeMappings)

        val matches = filterMappings({ _: ResultTypeMapping -> true }, ep)
        if (matches.isEmpty())
            return null

        return matches.first() as ResultTypeMapping
    }

    /**
     * find (global) "result" type mappings.
     *
     * @return the "result" type mapping or null if there is no match.
     */
    fun findResultTypeMapping(): ResultTypeMapping? {
        val matches = filterMappings({ _: ResultTypeMapping -> true }, typeMappings)
        if (matches.isEmpty())
            return null

        return matches.first() as ResultTypeMapping
    }

    /**
     * find (endpoint) "single" type mappings.
     *
     * @param info schema info of the OpenAPI schema.
     * @return the "single" type mappings or null if there is no match.
     */
    fun findEndpointSingleTypeMapping(info: SchemaInfo): TypeMapping? {
        val ep = filterMappings(EndpointTypeMatcher(info.getPath(), info.getMethod()), typeMappings)

        val matches = filterMappings(SingleTypeMatcher(), ep)
        if (matches.isEmpty())
            return null

        return matches.first() as TypeMapping
    }

    /**
     * find (global) "single" type mapping.
     *
     * @return the "single" type mappings or null if there is no match.
     */
    fun findSingleTypeMapping(): TypeMapping? {
        val matches = filterMappings(SingleTypeMatcher(), typeMappings)
        if (matches.isEmpty())
            return null

        return matches.first() as TypeMapping
    }

    /**
     * find (endpoint) "multi" type mappings.
     *
     * @param info schema info of the OpenAPI schema.
     * @return the "multi" type mappings or null if there is no match.
     */
    fun findEndpointMultiTypeMapping(info: SchemaInfo): TypeMapping? {
        val ep = filterMappings(EndpointTypeMatcher(info.getPath(), info.getMethod()), typeMappings)

        val matches = filterMappings(MultiTypeMatcher(), ep)
        if (matches.isEmpty())
            return null

        return matches.first() as TypeMapping
    }

    /**
     * find (global) "multi" type mapping.
     *
     * @return the "multi" type mappings or null if there is no match.
     */
    fun findMultiTypeMapping(): TypeMapping? {
        val matches = filterMappings(MultiTypeMatcher(), typeMappings)
        if (matches.isEmpty())
            return null

        return matches.first() as TypeMapping
    }

    /**
     * check if the given endpoint should be excluded.
     *
     * @param path the endpoint path
     * @return true/false
     */
    fun isExcludedEndpoint(path: String, method: HttpMethod): Boolean {
        val ep = typeMappings
            .filterIsInstance<EndpointTypeMapping>()
            .filter(EndpointTypeMatcher(path, null/*method*/))

        if (ep.isEmpty())
            return false

        // todo check multiple matches

        return ep.first().exclude
    }

    /**
     * find (endpoint) "null" type mappings.
     *
     * @param info schema info of the OpenAPI schema.
     * @return the "null" type mappings or null if there is no match.
     */
    fun findEndpointNullTypeMapping(info: SchemaInfo): NullTypeMapping? {
        val ep = filterMappings(EndpointTypeMatcher(info.getPath(), info.getMethod()), typeMappings)
        val matches = filterMappings(NullTypeMatcher(), ep)
        if (matches.isNotEmpty())
            return matches.first() as NullTypeMapping

        val epAll = filterMappings(EndpointTypeMatcher(info.getPath(), null), typeMappings)
        val matchesAll = filterMappings(NullTypeMatcher(), epAll)
        if (matchesAll.isNotEmpty())
            return matchesAll.first() as NullTypeMapping

        return null
    }

    /**
     * find (global) "null" type mapping.
     *
     * @return the "multi" type mappings or null if there is no match.
     */
    fun findNullTypeMapping(): NullTypeMapping? {
        val matches = filterMappings(NullTypeMatcher(), typeMappings)
        if (matches.isEmpty())
            return null

        return matches.first() as NullTypeMapping
    }

    private fun getTypeMapping(mappings: List<Mapping>): TypeMapping? {
        if (mappings.isEmpty())
            return null

        if (mappings.size > 1)
            throw AmbiguousTypeMappingException(mappings.toTypeMapping())

        return mappings.first() as TypeMapping
    }

    private inline fun <reified T: Mapping> filterMappings(
        matcher: (m: T) -> Boolean, mappings: List<Mapping>): List<Mapping> {

        return mappings
            .filterIsInstance<T>()
            .filter { matcher(it) }
            .map { it.getChildMappings() }
            .flatten()
    }

}
