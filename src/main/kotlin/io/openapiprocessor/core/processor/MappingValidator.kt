/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
import com.networknt.schema.ValidationMessage
import java.io.InputStream

/**
 * validate the given mapping.yaml with the mapping.yaml json schema.
 */
open class MappingValidator {

    fun validate(mapping: String): Set<ValidationMessage> {
        val mapper = ObjectMapper(YAMLFactory())
        val node = mapper.readTree(mapping)

        val factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7)
        val schema = factory.getSchema(getSchema())

        return schema.validate(node)
    }

    private fun getSchema(): InputStream {
        return this.javaClass.getResourceAsStream("/mapping/v2/mapping.flat.yaml.json")!!
    }

}
