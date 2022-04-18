/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

/**
 * OpenAPI schema that is *no* schema. For example the *readOnly* item in
 *
 * allOf:
 *  - readOnly: true
 *  - $ref: '#/components/schemas/Foo'
 */
class NoDataType(
    private val name: DataTypeName,
    private val pkg: String,
    override val constraints: DataTypeConstraints? = null,
    override val deprecated: Boolean = false
): DataType {

    override fun getName(): String {
        return name.id
    }

    override fun getPackageName(): String {
        return pkg
    }

    override fun getImports(): Set<String> {
        return setOf("${getPackageName()}.${getName()}")
    }

}


