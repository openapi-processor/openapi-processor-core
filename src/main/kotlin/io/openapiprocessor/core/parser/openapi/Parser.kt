/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import io.openapiprocessor.core.parser.Path
import io.openapiprocessor.core.parser.RefResolver
import io.openapiprocessor.core.support.toURL
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import io.openapiprocessor.core.parser.OpenApi as ParserOpenApi
import io.openapiprocessor.core.parser.openapi.v30.OpenApi as OpenApi30
import io.openapiprocessor.core.parser.openapi.v31.OpenApi as OpenApi31

class BadOpenApi : ParserOpenApi {

    override fun getPaths(): Map<String, Path> {
        TODO("Not yet implemented")
    }

    override fun getRefResolver(): RefResolver {
        TODO("Not yet implemented")
    }

    override fun printWarnings() {
        TODO("Not yet implemented")
    }

}

/**
 * openapi 3.0.x/3.1.x parser.
 */
open class Parser {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    fun parse(apiPath: String): ParserOpenApi {
        val factory = YAMLFactory()
        val mapper = ObjectMapper(factory)
        val api = mapper.readValue(toURL(apiPath), object : TypeReference<Map<String, Any>>() {})

        // todo validate version
        // is openapi file?

        val version = api["openapi"] as String
        when  {
            version.startsWith("3.0") -> {
                // validate 3.0
                // - must have "paths" key
                // parse as 3.0
                // return OpenApi30(.., validation errors)
                return OpenApi30()
            }
            version.startsWith("3.1") -> {
                // validate 3.1
                // - must have "paths" key
                // parse as 3.1
                // return OpenApi31(.., validation errors)
                return OpenApi31()
            }
            else -> {
                // throw, should never come here
                log.warn("unknown OpenAPI version: {}", version)
                return BadOpenApi()
            }
        }
    }

}
