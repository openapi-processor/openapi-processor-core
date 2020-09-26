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

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.converter.mapping.MappingSchema
import io.openapiprocessor.core.parser.RefResolver as ParserRefResolver
import io.openapiprocessor.core.parser.Schema

/**
 * Helper for {@link DataTypeConverter}. Holds an OpenAPI
 * schema with context information, i.e. name and if this is an inline type with a generated name.
 *
 * @author Martin Hauner
 * @author Bastian Wilhelm
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
    private val schema: Schema?,

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
     * iterate over items
     */
    fun eachItemOf(action: (info: SchemaInfo) -> Unit) {
        schema?.getItems()?.forEachIndexed { index, schema ->
            action(SchemaInfo(
                path = path,
                name = "${name}-of-${index}",
                schema = schema,
                resolver = resolver
            ))
        }
    }

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
        return SchemaInfo(
            path = path,
            name = schema?.getItem()?.getRef() ?: schema?.getItem()?.getType()!!,
            schema = schema?.getItem(),
            resolver = resolver
        )

    }

    override fun isPrimitive(): Boolean {
        return listOf("boolean", "int", "number", "string").contains(schema?.getType())
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
