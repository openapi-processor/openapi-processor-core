/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.openapiprocessor.core.converter.SchemaInfo

/**
 * find mappings of a given schema info in the type mapping list.
 */
class MappingFinder(private val typeMappings: List<Mapping> = emptyList()) {

    /**
     * find any matching endpoint mapping for the given schema info.
     *
     * @param info schema info of the OpenAPI schema.
     * @return list of matching mappings
     */
    fun findEndpointMappings(info: SchemaInfo): List<Mapping> {
        val ep = filterMappings(EndpointMatcher(info), typeMappings)

        val io = filterMappingsOld(IoMatcherOld(info), ep)
        if (io.isNotEmpty()) {
            return io
        }

        return filterMappings (TypeMatcher(info), ep)
    }

    /**
     * find any matching (global) io mapping for the given schema info.
     *
     * @param info schema info of the OpenAPI schema.
     * @return list of matching mappings
     */
    @Deprecated("")
    fun findIoMappings(info: SchemaInfo): List<Mapping> {
        return filterMappingsOld(IoMatcherOld(info), typeMappings)
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
     * find additional parameter mappings for the given endpoint.
     *
     * @param path the endpoint path
     * @return list of matching mappings
     */
    fun findAdditionalEndpointParameter(path: String): List<Mapping> {
        val info = MappingSchemaEndpoint(path)
        val ep = filterMappingsOld(EndpointMatcherOld(info), typeMappings)

        val matcher = AddParameterMatcher(info)
        val add = ep.filter {
            it.matches (matcher)
        }

        if (add.isNotEmpty()) {
            return add
        }

        return emptyList()
    }

    /**
     * find endpoint result type mapping.
     *
     * @param info schema info of the OpenAPI schema.
     * @return the result type mapping.
     */
    fun findEndpointResultMapping(info: SchemaInfo): List<Mapping> {
        val ep = filterMappingsOld(EndpointMatcherOld(info), typeMappings)

        val matcher = ResultTypeMatcher(info)
        val result = ep.filter {
            it.matches (matcher)
        }

        if (result.isNotEmpty()) {
            return result
        }

        return emptyList()
    }

    /**
     * find (global) result type mapping.
     *
     * @param info schema info of the OpenAPI schema.
     * @return the result type mapping.
     */
    fun findResultMapping(info: SchemaInfo): List<Mapping> {
        val ep = filterMappingsOld(ResultTypeMatcher(info), typeMappings)
        if (ep.isNotEmpty()) {
            return ep
        }

        return emptyList()
    }

    /**
     * find endpoint single type mapping.
     *
     * @param info schema info of the OpenAPI schema.
     * @return the single type mapping.
     */
    fun findEndpointSingleMapping(info: SchemaInfo): List<Mapping> {
        val ep = filterMappingsOld(EndpointMatcherOld(info), typeMappings)

        val matcher = SingleTypeMatcher(info)
        val result = ep.filter {
            it.matches(matcher)
        }

        if (result.isNotEmpty()) {
            return result
        }

        return emptyList()
    }

    /**
     * find (global) single type mapping.
     *
     * @param info schema info of the OpenAPI schema.
     * @return the single type mapping.
     */
    fun findSingleMapping(info: SchemaInfo): List<Mapping> {
        val ep = filterMappingsOld(SingleTypeMatcher(info), typeMappings)
        if (ep.isNotEmpty()) {
            return ep
        }

        return emptyList()
    }

    /**
     * find endpoint multi type mapping.
     *
     * @param info schema info of the OpenAPI schema.
     * @return the multi type mapping.
     */
    fun findEndpointMultiMapping(info: SchemaInfo): List<Mapping> {
        val ep = filterMappingsOld(EndpointMatcherOld(info), typeMappings)

        val matcher = MultiTypeMatcher(info)
        val result = ep.filter {
            it.matches (matcher)
        }

        if (result.isNotEmpty()) {
            return result
        }

        return emptyList()
    }

    /**
     * find (global) multi type mapping.
     *
     * @param info schema info of the OpenAPI schema.
     * @return the multi type mapping.
     */
    fun findMultiMapping(info: SchemaInfo): List<Mapping> {
        val ep = filterMappingsOld(MultiTypeMatcher(info), typeMappings)
        if (ep.isNotEmpty()) {
            return ep
        }

        return emptyList()
    }

    /**
     * check if the given endpoint should b excluded.
     *
     * @param path the endpoint path
     * @return true/false
     */
    fun isExcludedEndpoint(path: String): Boolean {
        val info = MappingSchemaEndpoint(path)
        val matcher = EndpointMatcherOld(info)

        val ep = typeMappings.filter {
            it.matches (matcher)
        }

        if (ep.isNotEmpty()) {
            if (ep.size != 1) {
                throw AmbiguousTypeMappingException(ep.map { it as TypeMapping })
            }

            val match = ep.first () as EndpointTypeMapping
            return match.exclude
        }

        return false
    }

    @Deprecated(message = "replaced by filterMappings(matcher, mappings)")
    private fun filterMappingsOld(visitor: MappingVisitor, mappings: List<Mapping>): List<Mapping> {
        return mappings
            .filter { it.matches(visitor) }
            .map { it.getChildMappings() }
            .flatten()
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

class MappingSchemaEndpoint(private val path: String): MappingSchema {

    override fun getPath(): String {
        return path
    }

    override fun getName(): String {
        throw NotImplementedError() // return "" // null
    }

    override fun getContentType(): String {
        throw NotImplementedError() // return "" // null
    }

    override fun getType(): String {
        throw NotImplementedError() // return "" // null
    }

    override fun getFormat(): String? {
        throw NotImplementedError()// return null
    }

    override fun isPrimitive(): Boolean {
        throw NotImplementedError()
    }

    override fun isArray(): Boolean {
        throw NotImplementedError()
    }

}

class EndpointMatcher(private val schema: MappingSchema): (EndpointTypeMapping) -> Boolean {

    override fun invoke(m: EndpointTypeMapping): Boolean {
        return m.path == schema.getPath()
    }

}

@Deprecated("", replaceWith = ReplaceWith("EndpointMatcher"))
class EndpointMatcherOld(schema: MappingSchema): BaseVisitor(schema) {

    override fun match(mapping: EndpointTypeMapping): Boolean {
        return mapping.path == schema.getPath()
    }

}

@Deprecated("")
class IoMatcherOld(schema: MappingSchema): BaseVisitor(schema) {

    override fun match(mapping: ParameterTypeMapping): Boolean {
        return mapping.parameterName == schema.getName()
    }

    override fun match(mapping: ResponseTypeMapping): Boolean {
        return mapping.contentType == schema.getContentType()
    }

}

class ParameterTypeMatcher(private val schema: MappingSchema): (ParameterTypeMapping) -> Boolean {

    override fun invoke(mapping: ParameterTypeMapping): Boolean {
        return mapping.parameterName == schema.getName()
    }

}

class ResponseTypeMatcher(private val schema: MappingSchema): (ResponseTypeMapping) -> Boolean {

    override fun invoke(mapping: ResponseTypeMapping): Boolean {
        return mapping.contentType == schema.getContentType()
    }

}

class TypeMatcher(private val schema: MappingSchema): (TypeMapping) -> Boolean {

    override fun invoke(mapping: TypeMapping): Boolean {
        // try to match by name first
        // the format must match to avoid matching primitive and primitive with format, e.g.
        // string should not match string:binary
        if (matchesName(mapping) && matchesFormat(mapping)) {
            return true
        }

        return when {
            schema.isPrimitive() -> {
                matchesType(mapping) && matchesFormat(mapping)
            }
            schema.isArray() -> {
                matchesArray(mapping)
            }
            else -> {
                false // nop
            }
        }
    }

    private fun matchesName(m: TypeMapping): Boolean = m.sourceTypeName == schema.getName()
    private fun matchesFormat(m: TypeMapping): Boolean = m.sourceTypeFormat == schema.getFormat()
    private fun matchesType(m: TypeMapping): Boolean = m.sourceTypeName == schema.getType()
    private fun matchesArray(m: TypeMapping): Boolean = m.sourceTypeName == "array"
}

class ResultTypeMatcher(schema: MappingSchema): BaseVisitor(schema) {

    override fun match(mapping: ResultTypeMapping): Boolean {
        return true
    }

}

class SingleTypeMatcher(schema: MappingSchema): BaseVisitor(schema) {

    override fun match(mapping: TypeMapping): Boolean {
        return mapping.sourceTypeName == "single"
    }

}

class MultiTypeMatcher(schema: MappingSchema): BaseVisitor(schema) {

    override fun match(mapping: TypeMapping): Boolean {
        return mapping.sourceTypeName == "multi"
    }

}

class AddParameterMatcher(schema: MappingSchema): BaseVisitor(schema) {

    override fun match(mapping: AddParameterTypeMapping): Boolean {
        return true
    }

}

open class BaseVisitor(protected val schema: MappingSchema): MappingVisitor {

    override fun match(mapping: EndpointTypeMapping): Boolean {
        return false
    }

    override fun match(mapping: ParameterTypeMapping): Boolean {
        return false
    }

    override fun match(mapping: ResponseTypeMapping): Boolean {
        return false
    }

    override fun match(mapping: TypeMapping): Boolean {
        return false
    }

    override fun match(mapping: AddParameterTypeMapping): Boolean {
        return false
    }

    override fun match(mapping: ResultTypeMapping): Boolean {
        return false
    }

}
