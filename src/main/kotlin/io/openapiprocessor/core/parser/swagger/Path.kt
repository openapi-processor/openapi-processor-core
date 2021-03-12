/*
 * Copyright 2020 the original authors
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

package io.openapiprocessor.core.parser.swagger

import io.openapiprocessor.core.parser.Path as ParserPath
import io.openapiprocessor.core.model.HttpMethod
import io.openapiprocessor.core.parser.Operation as ParserOperation
import io.swagger.v3.oas.models.PathItem as SwaggerPath
import io.swagger.v3.oas.models.Operation as SwaggerOperation

/**
 * Swagger Path abstraction.
 *
 * @author Martin Hauner
 */
class Path(private val path: String, private val info: SwaggerPath): ParserPath {

    override fun getPath(): String = path

    override fun getOperations(): List<ParserOperation> {
        val ops: MutableList<ParserOperation> = mutableListOf()

        HttpMethod.values().map {
            val op = info.getOperation(it.method)
            if (op != null) {
                ops.add (Operation(it, op, info))
            }
        }

        return ops
    }

}

fun SwaggerPath.getOperation(method: String): SwaggerOperation? {
    return when(method) {
        HttpMethod.GET.method -> this.get
        HttpMethod.PUT.method -> this.put
        HttpMethod.POST.method -> this.post
        HttpMethod.DELETE.method -> this.delete
        HttpMethod.OPTIONS.method -> this.options
        HttpMethod.HEAD.method -> this.head
        HttpMethod.PATCH.method -> this.patch
        HttpMethod.TRACE.method -> this.trace
        else -> null
    }
}
