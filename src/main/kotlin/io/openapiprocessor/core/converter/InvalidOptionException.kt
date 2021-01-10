/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

class InvalidOptionException(private val option: String): RuntimeException() {

    override val message: String
        get() = "mandatory option '$option' is not set!"

}
