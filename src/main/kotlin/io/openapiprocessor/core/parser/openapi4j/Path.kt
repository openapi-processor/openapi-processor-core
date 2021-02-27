/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi4j

import io.openapiprocessor.core.parser.Path as ParserPath
import io.openapiprocessor.core.model.HttpMethod
import io.openapiprocessor.core.parser.Operation as ParserOperation
import org.openapi4j.parser.model.v3.Path as Oa4jPath

/**
 * openapi4j Path abstraction.
 */
class Path(
    private val path: String,
    private val info: Oa4jPath,
    private val refResolver: RefResolverNative
): ParserPath {

    override fun getPath(): String = path

    override fun getOperations(): List<ParserOperation> {
        val ops: MutableList<ParserOperation> = mutableListOf()

        HttpMethod.values().map {
            val op = info.getOperation(it.method)
            if (op != null) {
                ops.add (Operation(it, op, info, refResolver))
            }
        }

        return ops
    }

}
