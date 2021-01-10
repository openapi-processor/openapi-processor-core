/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser

class NoOpenApiException : RuntimeException() {

    override val message: String
        get() = "can't find OpenAPI description. Is the option 'apiPath' set?"

}
