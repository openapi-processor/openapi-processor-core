/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.swagger

import io.openapiprocessor.core.parser.ParserException
import io.openapiprocessor.core.support.toURL
import io.swagger.v3.parser.OpenAPIV3Parser
import io.swagger.v3.parser.core.models.ParseOptions
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import io.openapiprocessor.core.parser.OpenApi as ParserOpenApi

const val SCHEME_RESOURCE = "resource:"

/**
 * swagger parser.
 */
open class Parser {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    private enum class Source {URL, STRING}

    fun parse(apiPath: String): ParserOpenApi {
        try {
            return run(apiPath, Source.URL)
        } catch (ex: Exception) {
            log.error("can't read OpenAPI description!")
            throw ParserException(ex)
        }
    }

    /** test only */
    fun parseString(api: String): ParserOpenApi {
        try {
            return run(api, Source.STRING)
        } catch (ex: Exception) {
            throw ParserException(ex)
        }
    }

    private fun run(api: String, source: Source): ParserOpenApi {
        val opts = ParseOptions()
        // loads $refs to other files into #/components/schema and replaces the $refs to the
        // external files with $refs to #/components/schema.
        opts.isResolve = true

        val result = when(source) {
            Source.URL -> {
                OpenAPIV3Parser().readLocation(preparePath(api), null, opts)
            }
            Source.STRING -> {
                OpenAPIV3Parser().readContents(api, null, opts)
            }
        }

        if (result.openAPI == null) {
            result.messages?.forEach {
                log.error(it)
            }
            throw FailedException()
        }

        return OpenApi(result)
    }

    private fun preparePath(path: String): String {
        // the swagger parser only works with http(s) & file protocols.

        // If it is something different (or nothing) it tries to find the given path as-is on the
        // file system. If that fails it tries to load the path as resource.

        if (isResource(path)) {
            // strip resource: (only used by tests) to load test files from the resources
            return path.substring(SCHEME_RESOURCE.length)
        }

        return toURL(path).toString ()
    }

    private fun isResource(path: String): Boolean {
        return path.startsWith(SCHEME_RESOURCE)
    }

}
