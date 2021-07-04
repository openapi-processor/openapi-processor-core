/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.openapiprocessor.core.processor.mapping.v1.Mapping
import io.openapiprocessor.core.processor.mapping.MappingVersion
import io.openapiprocessor.core.processor.mapping.v1.Parameter
import io.openapiprocessor.core.processor.mapping.v1.ParameterDeserializer
import io.openapiprocessor.core.processor.mapping.v2.Mapping as MappingV2
import io.openapiprocessor.core.processor.mapping.v2.Parameter as ParameterV2
import io.openapiprocessor.core.processor.mapping.v2.ParameterDeserializer as ParameterDeserializerV2
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import io.openapiprocessor.core.processor.mapping.version.Mapping as VersionMapping

/**
 *  Reader for mapping yaml.
 */
class MappingReader(private val validator: MappingValidator = MappingValidator()) {
    var log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    fun read(typeMappings: String?): MappingVersion? {
        if (typeMappings.isNullOrEmpty()) {
            return null
        }

        val mapping: String = when {
            isUrl (typeMappings) -> {
                URL (typeMappings).readText()
            }
            isFileName (typeMappings) -> {
                File (typeMappings).readText()
            }
            else -> {
                typeMappings
            }
        }

        val versionMapper = createVersionParser ()
        val version = versionMapper.readValue (mapping, VersionMapping::class.java)

        if (version.isV2()) {
            if (version.isDeprecatedVersionKey ()) {
                log.warn ("the mapping version key \"openapi-processor-spring\" is deprecated, please use \"openapi-processor-mapping\"")
            }

            validate(mapping)

            val mapper = createV2Parser()
            return mapper.readValue (mapping, MappingV2::class.java)
        } else {
            // assume v1
            log.info ("please update the mapping to the latest format")
            log.info ("see https://docs.openapiprocessor.io/spring/mapping/structure.html")

            val mapper = createV1Parser ()
            return mapper.readValue (mapping, Mapping::class.java)
        }
    }

    private fun validate(mapping: String) {
        validator.validate(mapping).forEach {
            log.warn(it.message)
        }
    }

    private fun createV2Parser(): ObjectMapper {
        val module = SimpleModule()
        module.addDeserializer (ParameterV2::class.java, ParameterDeserializerV2 ())

        val kotlinModule = KotlinModule.Builder()
            .nullIsSameAsDefault (true)
            .build ()

        return ObjectMapper(YAMLFactory())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true)
            .setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE)
            .registerModules(kotlinModule, module)
    }

    private fun createV1Parser(): ObjectMapper {
        val module = SimpleModule ()
        module.addDeserializer (Parameter::class.java, ParameterDeserializer ())

        val kotlinModule = KotlinModule.Builder()
            .nullIsSameAsDefault (true)
            .build ()

        return ObjectMapper (YAMLFactory ())
            .configure (DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setPropertyNamingStrategy (PropertyNamingStrategies.KEBAB_CASE)
            .registerModules(kotlinModule, module)
    }

    private fun createVersionParser(): ObjectMapper {
        return ObjectMapper (YAMLFactory ())
            .configure (DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setPropertyNamingStrategy (PropertyNamingStrategies.KEBAB_CASE)
            .registerModule (KotlinModule ())
    }

    private fun isFileName(name: String): Boolean {
        return name.endsWith (".yaml") || name.endsWith (".yml")
    }

    private fun isUrl (source: String): Boolean {
        return try {
            URL (source)
            true
        } catch (ignore: MalformedURLException) {
            false
        }
    }

}
