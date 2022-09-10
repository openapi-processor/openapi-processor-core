/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import java.net.URI
import java.nio.file.Paths

/**
 * convert a source string to a valid URI.
 *
 * if the source is an uri string it converts it to a URI. If the source has no scheme it assumes
 * a local path and adds the file scheme (i.e. file:).
 *
 * @param source source path or url
 * @return a URI to the given source
 */
fun toURI(source: String): URI {
    try {
        val uri = URI(source)
        if (uri.scheme != null) {
            return uri
        }
    } catch (ignore: Exception) {
    }

    // no scheme, assume file path
    return Paths.get(source)
        .normalize()
        .toAbsolutePath()
        .toUri()
}
