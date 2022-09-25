/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.networknt.schema.JsonSchemaException
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
import com.networknt.schema.ValidationMessage
//import io.openapiparser.jackson.JacksonConverter
//import io.openapiparser.reader.UriReader
//import io.openapiparser.schema.*
//import io.openapiparser.validator.Validator
//import io.openapiparser.validator.ValidatorSettings
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.InputStream
//import java.net.URI

/**
 * validate the given mapping.yaml with the mapping.yaml json schema.
 */
open class MappingValidator {
    val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

//    fun validate(mapping: String): Set<ValidationMessage> {
//        val mapper = ObjectMapper(YAMLFactory())
//        val node = mapper.readTree(mapping)
//
//        val factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7)
//        val schema = factory.getSchema(getSchema())
//
//        return schema.validate(node)
//    }

    fun validate(mapping: String, version: String): Set<ValidationMessage> {
//        val reader = UriReader()
//        val documents = DocumentStore()
//        val converter = JacksonConverter()
//        val resolver = Resolver(reader, converter, documents)
//
//        val store = SchemaStore(resolver)
//        store.addSchema(SchemaVersion.Draft7.schema, "/json-schema/draft-07/schema.json")
//
//        val schema = store.addSchema(getSchema(version))
//
//        val settings = ValidatorSettings()
//        val validator = Validator (settings)
//
//        val value = converter.convert(mapping)
//        val instance = JsonInstance(value, JsonInstanceContext(URI.create(""), ReferenceRegistry()))
//
//        val validate = validator.validate(schema, instance)
//
//        return emptySet()

        return try {
            val mapper = ObjectMapper(YAMLFactory())
            val node = mapper.readTree(mapping)

            val factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7)
            val schema = factory.getSchema(getSchema(version))

            schema.validate(node)
        } catch (ex: JsonSchemaException) {
            log.error("failed to validate mapping!", ex)
            emptySet()
        }
    }

//    private fun getSchema(): InputStream {
//        return this.javaClass.getResourceAsStream("/mapping/v2/mapping.flat.yaml.json")!!
//    }
//
    private fun getSchema(version: String): InputStream {
        return this.javaClass.getResourceAsStream("/mapping/$version/mapping.flat.yaml.json")!!
    }

//    private fun getSchema(version: String): String {
//        return "/mapping/$version/mapping.yaml.json"
//    }
}
