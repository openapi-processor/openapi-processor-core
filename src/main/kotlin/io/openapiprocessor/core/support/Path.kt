/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import java.nio.file.Files
import java.nio.file.Path

fun Path.deleteRecursively() =
    Files.walk(this)
        .sorted(Comparator.reverseOrder())
        .forEach(Files::delete)

val Path.text: String
    get() = String(Files.readAllBytes(this))
