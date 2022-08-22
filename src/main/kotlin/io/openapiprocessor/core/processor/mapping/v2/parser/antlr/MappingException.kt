/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2.parser.antlr

class MappingException(mapping: String, e: MappingParserException)
    : Exception("""failed to parse mapping: $mapping""", e)
