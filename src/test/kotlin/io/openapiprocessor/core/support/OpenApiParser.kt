/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import com.github.hauner.openapi.test.streamhandler.Memory
import io.openapiprocessor.core.parser.OpenApi as ParserOpenApi
import io.openapiprocessor.core.parser.ParserType
import io.openapiprocessor.core.parser.openapi4j.OpenApi as O4jOpenApi
import io.openapiprocessor.core.parser.swagger.OpenApi as SwaggerOpenApi
import io.swagger.v3.parser.OpenAPIV3Parser
import org.openapi4j.parser.OpenApi3Parser
import org.openapi4j.parser.validation.v3.OpenApi3Validator
import java.net.URL

/**
 * OpenAPI parser to read yaml from memory (swagger or openapi4j).
 */
fun parse(apiYaml: String, parserType: ParserType = ParserType.SWAGGER): ParserOpenApi {
    return when (parserType) {
        ParserType.SWAGGER -> parseWithSwagger(apiYaml)
        ParserType.OPENAPI4J -> parseWithOpenApi4j(apiYaml)
    }
}

fun parseWithOpenApi4j(yaml: String): ParserOpenApi {
    Memory.add("openapi.yaml", yaml)

    val api = OpenApi3Parser()
        .parse(URL("memory:openapi.yaml"), true)

    val results = OpenApi3Validator
        .instance()
        .validate(api)

    return O4jOpenApi(api, results)
}

fun parseWithSwagger(yaml: String): ParserOpenApi {
    val result = OpenAPIV3Parser()
        .readContents (yaml)

    return SwaggerOpenApi(result)
}

fun printWarnings(warnings: List<String>) {
    if (warnings.isEmpty()) {
        return
    }

    println("OpenAPI warnings:")
    warnings.forEach {
        println(it)
    }
}
