/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.converter.mapping.*
import io.openapiprocessor.core.converter.wrapper.NullDataTypeWrapper
import io.openapiprocessor.core.model.DataTypeCollector
import io.openapiprocessor.core.model.DataTypes
import io.openapiprocessor.core.model.Documentation
import io.openapiprocessor.core.model.datatypes.*
import io.openapiprocessor.core.writer.java.ModelClassNameCreator
import java.util.*
import kotlin.collections.LinkedHashMap

/**
 * Converter to map OpenAPI schemas to Java data types.
 */
class DataTypeConverter(
    private val options: ApiOptions,
    private val finder: MappingFinder = MappingFinder(options.typeMappings),
    private val nullWrapper: NullDataTypeWrapper = NullDataTypeWrapper(options, finder)
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

            objectType = AllOfObjectDataType(
                DataTypeName(schemaInfo.getName(), getTypeNameWithSuffix(schemaInfo.getName())),
                listOf(options.packageName, "model").joinToString ("."),
                items,
                schemaInfo.getDeprecated()
            )
        } else {
            objectType = AnyOneOfObjectDataType(
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
            return MappedCollectionDataType(
                targetType.getName(),
                targetType.getPkg(),
                item,
                constraints,
                schemaInfo.getDeprecated()
            )
        }

        return ArrayDataType(item, constraints, schemaInfo.getDeprecated())
    }

    private fun createRefDataType (schemaInfo: SchemaInfo, dataTypes: DataTypes): DataType {
        return convert(schemaInfo.buildForRef(), dataTypes)
    }

    private fun createObjectDataType(schemaInfo: SchemaInfo, dataTypes: DataTypes): DataType {
        val properties = LinkedHashMap<String, DataType>()
        schemaInfo.eachProperty { propName: String, propSchemaInfo: SchemaInfo ->
            var propDataType = convert(propSchemaInfo, dataTypes)

            if (propSchemaInfo.getNullable()) {
                propDataType = nullWrapper.wrap(propDataType, schemaInfo)
            }
            properties[propName] = propDataType
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
            DataTypeName(schemaInfo.getName(), getTypeNameWithSuffix(schemaInfo.getName())),
            listOf(options.packageName, "model").joinToString("."),
            properties = properties,
            constraints = constraints,
            deprecated = schemaInfo.getDeprecated(),
            documentation = Documentation(description = schemaInfo.description)
        )

        dataTypes.add (schemaInfo.getName(), objectType)
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
                schemaInfo.getDeprecated(),
                true
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
            schemaInfo.getExclusiveMaximum(),
            pattern = schemaInfo.pattern
        )

        return when(typeFormat) {
            "integer",
            "integer:int32" ->
                IntegerDataType(constraints, schemaInfo.getDeprecated(),
                    Documentation(description = schemaInfo.description))
            "integer:int64" ->
                LongDataType(constraints, schemaInfo.getDeprecated(),
                    Documentation(description = schemaInfo.description))
            "number",
            "number:float" ->
                FloatDataType(constraints, schemaInfo.getDeprecated(),
                    Documentation(description = schemaInfo.description))
            "number:double" ->
                DoubleDataType(constraints, schemaInfo.getDeprecated(),
                    Documentation(description = schemaInfo.description))
            "boolean" ->
                BooleanDataType(constraints, schemaInfo.getDeprecated(),
                    Documentation(description = schemaInfo.description))
            "string" ->
                createStringDataType(schemaInfo, constraints, dataTypes)
            "string:date" ->
                LocalDateDataType(constraints, schemaInfo.getDeprecated(),
                    Documentation(description = schemaInfo.description))
            "string:date-time" ->
                OffsetDateTimeDataType (constraints, schemaInfo.getDeprecated(),
                    Documentation(description = schemaInfo.description))
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

    private fun createStringDataType(schemaInfo: SchemaInfo, constraints: DataTypeConstraints,
        dataTypes: DataTypes): DataType {

        if (!schemaInfo.isEnum()) {
            return StringDataType(constraints, schemaInfo.getDeprecated(),
                Documentation(description = schemaInfo.description))
        }

        // in case of an inline definition the name may be lowercase, make sure the enum
        // class gets an uppercase name!
        val enumName = schemaInfo.getName().capitalize ()

        val found = dataTypes.find(enumName)
        if (found != null) {
            return found
        }

        val enumType = StringEnumDataType (
            DataTypeName(enumName, getTypeNameWithSuffix(enumName)),
            listOf(options.packageName, "model").joinToString("."),
            schemaInfo.getEnumValues() as List<String>,
            constraints,
            schemaInfo.getDeprecated())

        dataTypes.add (enumName, enumType)
        return enumType
    }

    /**
     * the mappings are checked in the following order and the first match wins:
     *
     * - endpoint io (parameter/response)
     * - endpoint type
     * - global io (parameter/response)
     * - global type
     */
    private fun getMappedDataType(info: SchemaInfo): TargetType? {
        // check endpoint mappings
        val epMatch = finder.findEndpointTypeMapping(info)
        if (epMatch != null) {
            return epMatch.getTargetType()
        }

        // check global io (parameter & response) mappings
        val ioMatch = finder.findIoTypeMapping(info)
        if (ioMatch != null)
            return ioMatch.getTargetType()

        // check global type mapping
        val typeMatch = finder.findTypeMapping(info)
        if (typeMatch != null)
            return typeMatch.getTargetType()

        return null
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

    private fun getTypeNameWithSuffix(name: String): String {
        return ModelClassNameCreator(options.modelNameSuffix).createName(name)
    }

}
