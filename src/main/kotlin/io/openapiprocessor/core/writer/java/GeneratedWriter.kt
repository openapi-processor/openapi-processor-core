/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import java.io.Writer

interface GeneratedWriter {
    fun getImport():String
    fun writeUse(target: Writer)
    fun writeSource(target: Writer)
}
