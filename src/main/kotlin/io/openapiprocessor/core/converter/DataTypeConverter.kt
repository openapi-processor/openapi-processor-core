/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.converter.mapping.*
import io.openapiprocessor.core.model.DataTypeCollector
import io.openapiprocessor.core.model.DataTypes
import io.openapiprocessor.core.model.datatypes.*
import java.util.*
import kotlin.collections.LinkedHashMap

/**
 * Converter to map OpenAPI schemas to Java data types.
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

        val result: DataType = when {
            schemaInfo.isRefObject() -> {
                createRefDataType(schemaInfo, dataTypes)
            }
            schemaInfo.isComposedObject() -> {
                createComposedDataType(schemaInfo, dataTypes)
            }
            schemaInfo.isArray () -> {
                createArrayDataType (schemaInfo, dataTypes)
            }
            schemaInfo.isObject () -> {
                createObjectDataType (schemaInfo, dataTypes)
            }
            schemaInfo.isTypeLess() -> {
                createNoDataType(schemaInfo, dataTypes)
            }
            else -> {
                createSimpleDataType(schemaInfo, dataTypes)
            }
        }

        pop()

        // result is complete, add ref what is really required
        if (current.isEmpty()) {
            DataTypeCollector(dataTypes, options.packageName).collect(result)
        }

        return result
    }

    private fun createComposedDataType(schemaInfo: SchemaInfo, dataTypes: DataTypes): DataType {
        val items: MutableList<DataType> = mutableListOf()
        schemaInfo.eachItemOf { itemSchemaInfo: SchemaInfo ->
            val itemType = convert(itemSchemaInfo, dataTypes)
            items.add (itemType)
        }

        val objectType: DataType
        val targetType = getMappedDataType(schemaInfo)
        if (targetType != null) {
            return MappedDataType(
                targetType.getName(),
                targetType.getPkg(),
                targetType.genericNames,
                null,
                schemaInfo.getDeprecated()
            )
        }

        val found = dataTypes.find(schemaInfo.getName())
        if (found != null) {
            return found
        }

        if (schemaInfo.isComposedAllOf()) {
            val filtered = items.filterNot { item -> item is NoDataType }
            if (filtered.size == 1) {
                return filtered.first()
            }

            objectType = ComposedObjectDataType(
                schemaInfo.getName(),
                listOf(options.packageName, "model").joinToString ("."),
                schemaInfo.itemOf()!!,
                items,
                null,
                schemaInfo.getDeprecated()
            )
        } else {
            objectType = CompositeObjectDataType(
                schemaInfo.getName(),
                listOf(options.packageName, "model").joinToString ("."),
                schemaInfo.itemOf()!!,
                items,
                null,
                schemaInfo.getDeprecated()
            )
        }

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
        val properties = LinkedHashMap<String, DataType>()
        schemaInfo.eachProperty { propName: String, propSchemaInfo: SchemaInfo ->
            properties[propName] = convert(propSchemaInfo, dataTypes)
        }

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
                    return MappedDataType(
                        targetType.getName(),
                        targetType.getPkg(),
                        targetType.genericNames,
                        null,
                        schemaInfo.getDeprecated()
                    )
                }
            }
        }

        val found = dataTypes.find(schemaInfo.getName())
        if (found != null) {
            return found
        }

        val constraints = DataTypeConstraints(
            nullable = schemaInfo.getNullable(),
            required = schemaInfo.getRequired()
        )

        val objectType = ObjectDataType (
            schemaInfo.getName(),
            listOf(options.packageName, "model").joinToString("."),
            properties = properties,
            constraints = constraints,
            deprecated = schemaInfo.getDeprecated()
        )

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

    private fun createStringDataType(schemaInfo: SchemaInfo, constraints: DataTypeConstraints, dataTypes: DataTypes): DataType {
        if (!schemaInfo.isEnum()) {
            return StringDataType(constraints, schemaInfo.getDeprecated())
        }

        // in case of an inline definition the name may be lowercase, make sure the enum
        // class gets an uppercase name!
        val enumName = schemaInfo.getName().capitalize ()

        val found = dataTypes.find(enumName)
        if (found != null) {
            return found
        }

        val enumType = StringEnumDataType (
            enumName,
            listOf(options.packageName, "model").joinToString("."),
            schemaInfo.getEnumValues() as List<String>,
            constraints,
            schemaInfo.getDeprecated())

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
