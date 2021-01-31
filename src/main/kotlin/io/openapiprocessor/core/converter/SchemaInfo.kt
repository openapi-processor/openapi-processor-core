/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.converter.mapping.MappingSchema
import io.openapiprocessor.core.parser.RefResolver as ParserRefResolver
import io.openapiprocessor.core.parser.Schema

/**
 * Helper for [DataTypeConverter]. Holds an OpenAPI schema with context information, e.g. name and
 * if this is an inline type with a generated name.
 */
class SchemaInfo(
    /**
     * Endpoint path.
     */
    private val path: String,

    /**
     * name of the type/schema or parameter name.
     */
    private val name: String,

    /**
     * response content type.
     */
    private val contentType: String = "",

    /**
     * the OpenAPI schema
     */
    private val schema: Schema?, // todo not null

    /**
     * resolver of $ref'erences
     */
    private val resolver: ParserRefResolver

): MappingSchema {

    override fun getPath(): String {
        return path
    }

    override fun getName(): String {
        return name
    }

    override fun getContentType(): String {
        return contentType
    }

    /**
     * get type of OpenAPI schema.
     *
     * @return schema type
     */
    override fun getType(): String {
       return schema?.getType()!!
    }

    /**
     * get type format from OpenAPI schema.
     *
     * @return schema type format
     */
    override fun getFormat(): String? {
        return schema?.getFormat()
    }

    /**
     * get $ref from OpenAPI schema.
     *
     * @return schema $ref
     */
    fun getRef(): String? {
        return schema?.getRef()
    }

    /**
     * get default value.
     *
     * @return default value or null
     */
    fun getDefaultValue(): Any? {
        return schema?.getDefault()
    }

    /**
     * get deprecated value
     *
     * @return true or false
     */
    fun getDeprecated(): Boolean {
        return schema?.isDeprecated()!!
    }

    /**
     * get required value
     *
     * @return true or false
     */
    fun getRequired(): List<String> {
        return schema?.getRequired()!!
    }

    /**
     * get nullable value.
     *
     * @return nullable, true or false
     */
    fun getNullable(): Boolean {
        return schema?.getNullable()!!
    }

    /**
     * get minLength value.
     *
     * @return minLength value >= 0
     */
    fun getMinLength(): Int {
        return schema?.getMinLength() ?: 0
    }

    /**
     * get maxLength value.
     *
     * @return maxLength value or null
     */
    fun getMaxLength(): Int? {
        return schema?.getMaxLength()
    }

    /**
     * get minItems value.
     *
     * @return minItems value or null
     */
    fun getMinItems(): Int? {
        return schema?.getMinItems()
    }

    /**
     * get maxItems value.
     *
     * @return maxItems value or null
     */
    fun getMaxItems(): Int? {
        return schema?.getMaxItems()
    }

    /**
     * get maximum value.
     *
     * @return maximum value or null
     */
    fun getMaximum(): Number? {
        return schema?.getMaximum()
    }

    /**
     * maximum is exclusiveMaximum value.
     *
     * @return true or false
     */
    fun getExclusiveMaximum(): Boolean {
        return schema?.isExclusiveMaximum()!!
    }

    /**
     * get minimum value.
     *
     * @return minimum value or null
     */
    fun getMinimum(): Number? {
        return schema?.getMinimum()
    }

    /**
     * minimum is exclusiveMinimum.
     *
     * @return exclusiveMinimum value or null
     */
    fun getExclusiveMinimum(): Boolean {
        return schema?.isExclusiveMinimum()!!
    }

    /**
     * iterate over properties
     */
    fun eachProperty(action: (name: String, info: SchemaInfo) -> Unit) {
        schema?.getProperties()?.forEach { (name, schema) ->
            action(name, buildForNestedType(name, schema))
        }
    }

    /**
     * iterate over composed items
     */
    fun eachItemOf(action: (info: SchemaInfo) -> Unit) {
        schema?.getItems()?.forEachIndexed { index, schema ->
            action(SchemaInfo(
                path = path,
                name = "${name}_${itemOf()!!.capitalize()}_${index}",
                schema = schema,
                resolver = resolver
            ))
        }
    }

    /**
     * allOf, oneOf, anyOf.
     */
    fun itemOf(): String? {
        return schema?.itemsOf()
    }

    /**
     * Factory method to create a {@link SchemaInfo} with the $ref name (without its "path").
     *
     * @return a new {@link SchemaInfo}
     */
    fun buildForRef(): SchemaInfo {
        return SchemaInfo(
            path = path,
            name = getRefName(schema!!),
            schema = resolver.resolve(schema),
            resolver = resolver)
    }

    /**
     * Factory method to create an inline {@link SchemaInfo} with (property) name and (property)
     * schema.
     *
     * @param nestedName the property name
     * @param nestedSchema the property schema
     * @return a new {@link SchemaInfo}
     */
    private fun buildForNestedType(nestedName: String, nestedSchema: Schema): SchemaInfo {
        return SchemaInfo(
            path = path,
            name = getNestedTypeName(nestedName),
            schema = nestedSchema,
            resolver = resolver)
    }

    /**
     * Factory method to create an {@link SchemaInfo} of the item type of an array schema.
     *
     * @return a new {@link SchemaInfo}
     */
    fun buildForItem(): SchemaInfo {
        val name = if (schema?.getItem()?.getRef() != null) {
            getRefName(schema.getItem())
        } else {
            schema?.getItem()?.getType()
        }

        return SchemaInfo(
            path = path,
            name = name!!,
            schema = schema?.getItem(),
            resolver = resolver
        )
    }

    override fun isPrimitive(): Boolean {
        return listOf("boolean", "integer", "number", "string").contains(schema?.getType())
    }

    override fun isArray(): Boolean {
        return schema?.getType().equals("array")
    }

    fun isObject(): Boolean {
        return schema?.getType().equals("object")
    }

    fun isComposedObject(): Boolean {
        return schema?.getType().equals("composed")
    }

    fun isTypeLess(): Boolean {
        return schema?.getType() == null
    }

    fun isRefObject(): Boolean {
        return schema?.getRef() != null
    }

    fun isEmpty(): Boolean {
        return schema == null
    }

    fun isEnum(): Boolean {
        return schema!!.getEnum().isNotEmpty()
    }

    fun getEnumValues(): List<*> {
        return schema!!.getEnum()
    }

    private fun getRefName(schema: Schema): String {
        return schema.getRef()!!
            .substring(schema.getRef()!!.lastIndexOf("/") + 1)
    }

    private fun getNestedTypeName(nestedName: String): String {
        return name + nestedName.capitalize()
    }

}
