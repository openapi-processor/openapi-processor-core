/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import java.net.URL
import java.nio.file.Paths

/**
 * convert a source string to a valid URL.
 *
 * if the source is an url string it converts it to a URL
 *
 * @param source source path or url
 * @return a URL to the given source
 */

fun toURL(source: String): URL {
    try {
        return URL(source)
    } catch (ignore: Exception) {
        // catch
    }

    try {
        return Paths.get(source)
            .normalize()
            .toUri()
            .toURL()
    } catch (e: Exception) {
        throw e
    }
}
