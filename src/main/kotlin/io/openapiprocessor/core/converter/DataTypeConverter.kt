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

import io.openapiprocessor.core.converter.mapping.*
import io.openapiprocessor.core.model.DataTypes
import io.openapiprocessor.core.model.datatypes.*
import java.util.*

/**
 * Converter to map OpenAPI schemas to Java data types.
 *
 * @author Martin Hauner
 * @author Bastian Wilhelm
 */
class DataTypeConverter(
    private val options: ApiOptions,
    private val finder: MappingFinder = MappingFinder(options.typeMappings)
) {
    private val current: Deque<SchemaInfo> = LinkedList()

    /**
     * converts an open api type (i.e. a {@code Schema}) to a java data type including nested types.
     * Stores named objects in {@code dataTypes} for re-use. {@code dataTypeInfo} provides the type
     * name used to add it to the list of data types.
     *
     * @param schemaInfo the open api type with context information
     * @param dataTypes known object types
     * @return the resulting java data type
     */
    fun convert(schemaInfo: SchemaInfo, dataTypes: DataTypes): DataType {
        if (isLoop(schemaInfo)) {
            return LazyDataType(schemaInfo, dataTypes)
        }

        push(schemaInfo)

        val result: DataType
        when {
            schemaInfo.isRefObject() -> {
                result = createRefDataType(schemaInfo, dataTypes)
            }
            schemaInfo.isComposedObject() -> {
                result = createComposedDataType(schemaInfo, dataTypes)
            }
            schemaInfo.isArray () -> {
                result = createArrayDataType (schemaInfo, dataTypes)
            }
            schemaInfo.isObject () -> {
                result = createObjectDataType (schemaInfo, dataTypes)
            }
            schemaInfo.isTypeLess() -> {
                result = createNoDataType(schemaInfo, dataTypes)
            }
            else -> {
                result = createSimpleDataType(schemaInfo, dataTypes)
            }
        }

        pop()
        return result
    }

    private fun createComposedDataType(schemaInfo: SchemaInfo, dataTypes: DataTypes): DataType {
        val objectType: DataType

        val items: MutableList<DataType> = mutableListOf()
        schemaInfo.eachItemOf { itemSchemaInfo: SchemaInfo ->
            val itemType = convert(itemSchemaInfo, dataTypes)
            items.add (itemType)
        }

        val targetType = getMappedDataType(schemaInfo)
        if (targetType != null) {
            objectType = MappedDataType(
                targetType.getName(),
                targetType.getPkg(),
                targetType.genericNames,
                null,
                schemaInfo.getDeprecated()
            )
            return objectType
        }

        objectType = ComposedObjectDataType(
            schemaInfo.getName(),
            listOf(options.packageName, "model").joinToString ("."),
            schemaInfo.itemOf()!!,
            items,
            null,
            schemaInfo.getDeprecated()
        )

        dataTypes.add (objectType)
        return objectType
    }

    private fun createArrayDataType(schemaInfo: SchemaInfo, dataTypes: DataTypes): DataType {
        val itemSchemaInfo = schemaInfo.buildForItem()
        val item = convert(itemSchemaInfo, dataTypes)

        val targetType = getMappedDataType (schemaInfo)

        val constraints = DataTypeConstraints(
            defaultValue = schemaInfo.getDefaultValue(),
            nullable = schemaInfo.getNullable(),
            minItems = schemaInfo.getMinItems() ?: 0,
            maxItems = schemaInfo.getMaxItems()
        )

        if (targetType != null) {
            val mappedDataType = MappedCollectionDataType (
                targetType.getName(),
                targetType.getPkg(),
                item,
                constraints,
                schemaInfo.getDeprecated()
            )
            return mappedDataType
        }

        return ArrayDataType(item, constraints, schemaInfo.getDeprecated())
    }

    private fun createRefDataType (schemaInfo: SchemaInfo, dataTypes: DataTypes): DataType {
        return convert(schemaInfo.buildForRef(), dataTypes)
    }

    private fun createObjectDataType(schemaInfo: SchemaInfo, dataTypes: DataTypes): DataType {
        val targetType = getMappedDataType(schemaInfo)
        if (targetType != null) {
            when(targetType.typeName) {
                Map::class.java.name,
                "org.springframework.util.MultiValueMap" ->
                    return MappedMapDataType(
                        targetType.getName(),
                        targetType.getPkg(),
                        targetType.genericNames,
                        null,
                        schemaInfo.getDeprecated())
                else -> {
                    val objectType = MappedDataType(
                        targetType.getName(),
                        targetType.getPkg(),
                        targetType.genericNames,
                        null,
                        schemaInfo.getDeprecated()
                    )

                    // todo probably not required anymore => no switch
                    dataTypes.add (schemaInfo.getName(), objectType)
                    return objectType
                }
            }
        }

        val constraints = DataTypeConstraints(
            nullable = schemaInfo.getNullable(),
            required = schemaInfo.getRequired()
        )

        val objectType = ObjectDataType (
            schemaInfo.getName(),
            listOf(options.packageName, "model").joinToString("."),
            constraints = constraints,
            deprecated = schemaInfo.getDeprecated()
        )

        schemaInfo.eachProperty { propName: String, propDataTypeInfo: SchemaInfo ->
            val propType = convert(propDataTypeInfo, dataTypes)
            objectType.addObjectProperty(propName, propType)
        }

        dataTypes.add (objectType)
        return objectType
    }

    private fun createSimpleDataType(schemaInfo: SchemaInfo, dataTypes: DataTypes): DataType {
        val targetType = getMappedDataType(schemaInfo)
        if(targetType != null) {
            return MappedDataType (
                targetType.getName(),
                targetType.getPkg(),
                targetType.genericNames,
                null,
                schemaInfo.getDeprecated()
            )
        }

        var typeFormat = schemaInfo.getType()
        if (isSupportedFormat(schemaInfo.getFormat())) {
            typeFormat += ":" + schemaInfo.getFormat()
        }

        // todo factory method in SchemaInfo
        val constraints = DataTypeConstraints(
            schemaInfo.getDefaultValue(),
            schemaInfo.getNullable(),
            schemaInfo.getMinLength(),
            schemaInfo.getMaxLength(),
            schemaInfo.getMinimum(),
            schemaInfo.getExclusiveMinimum(),
            schemaInfo.getMaximum(),
            schemaInfo.getExclusiveMaximum()
        )

        return when(typeFormat) {
            "integer",
            "integer:int32" ->
                IntegerDataType(constraints, schemaInfo.getDeprecated())
            "integer:int64" ->
                LongDataType(constraints, schemaInfo.getDeprecated())
            "number",
            "number:float" ->
                FloatDataType(constraints, schemaInfo.getDeprecated())
            "number:double" ->
                DoubleDataType(constraints, schemaInfo.getDeprecated())
            "boolean" ->
                BooleanDataType(constraints, schemaInfo.getDeprecated())
            "string" ->
                createStringDataType(schemaInfo, constraints, dataTypes)
            "string:date" ->
                LocalDateDataType(constraints, schemaInfo.getDeprecated())
            "string:date-time" ->
                OffsetDateTimeDataType (constraints, schemaInfo.getDeprecated())
            else ->
                throw UnknownDataTypeException(schemaInfo.getName(), schemaInfo.getType(),
                    schemaInfo.getFormat())
        }
    }

    private fun createNoDataType(schemaInfo: SchemaInfo, dataTypes: DataTypes): DataType {
        val constraints = DataTypeConstraints(
            nullable = schemaInfo.getNullable(),
            required = schemaInfo.getRequired()
        )

        return NoDataType(
            schemaInfo.getName(),
            constraints = constraints,
            deprecated = schemaInfo.getDeprecated()
        )
    }

    private fun isSupportedFormat(format: String?): Boolean {
        if(format == null)
            return false

        return format in listOf(
            "int32",
            "int64",
            "float",
            "double",
            "date",
            "date-time")
    }

    private fun createStringDataType(info: SchemaInfo, constraints: DataTypeConstraints, dataTypes: DataTypes): DataType {
        if (!info.isEnum()) {
            return StringDataType(constraints, info.getDeprecated())
        }

        // in case of an inline definition the name may be lowercase, make sure the enum
        // class gets an uppercase name!
        val enumType = StringEnumDataType (
            info.getName().capitalize (),
            listOf(options.packageName, "model").joinToString("."),
            info.getEnumValues() as List<String>,
            constraints,
            info.getDeprecated())

        dataTypes.add (enumType)
        return enumType
    }

    private fun getMappedDataType(info: SchemaInfo): TargetType? {
        // check endpoint mappings
        val endpointMatches = finder.findEndpointMappings(info)

        if (endpointMatches.isNotEmpty()) {

            if (endpointMatches.size != 1) {
                throw AmbiguousTypeMappingException (endpointMatches.toTypeMapping())
            }

            val target: TargetType? = (endpointMatches.first() as TargetTypeMapping).getTargetType()
            if (target != null) {
                return target
            }
        }

        // check global io (parameter & response) mappings
        val ioMatches = finder.findIoMappings(info)
        if (!ioMatches.isEmpty()) {

            if (ioMatches.size != 1) {
                throw AmbiguousTypeMappingException(ioMatches.toTypeMapping())
            }

            val target = (ioMatches.first() as TargetTypeMapping).getTargetType()
            if (target != null) {
                return target
            }
        }

        // check global type mapping
        val typeMatches = finder.findTypeMappings(info)
        if (typeMatches.isEmpty()) {
            return null
        }

        if (typeMatches.size != 1) {
            throw AmbiguousTypeMappingException(typeMatches.toTypeMapping())
        }

        val match = typeMatches.first () as TargetTypeMapping
        return match.getTargetType()
    }

    /**
     * push the current schema info.
     *
     * Pushes the given {@code info} onto the in-progress data type stack. It is used to detect
     * $ref loops.
     *
     * @param info the schema info that is currently processed
     */
    private fun push(info: SchemaInfo) {
        current.push(info)
    }

    /**
     * pop the current schema info.
     *
     */
    private fun pop() {
        current.pop()
    }

    /**
     * detect $ref loop.
     *
     * returns true if the given {@code info} is currently processed, false otherwise. True indicates
     * a $ref loop.
     *
     * @param info the schema info that is currently processed
     * @return true if loop else false
     */
    private fun isLoop(info: SchemaInfo): Boolean {
        val found = current.find {
            // $ref and non-ref SchemaInfo have the same name.
            // We are only interested if we have seen a non-ref!
            it.getName() == info.getName() && !it.isRefObject()
        }
        return found != null
    }

}
