/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.processor.MappingConverter
import io.openapiprocessor.core.processor.MappingReader
import io.openapiprocessor.core.processor.mapping.MappingVersion
import io.openapiprocessor.core.processor.mapping.v1.Mapping as MappingV1
import io.openapiprocessor.core.processor.mapping.v2.Mapping as MappingV2
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * creates [ApiOptions] from processor options and mapping.yaml.
 */
class OptionsConverter(private val checkObsoleteProcessorOptions: Boolean = false) {
    var log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    fun convertOptions(processorOptions: Map<String, Any>): ApiOptions {
        val options = ApiOptions()

        checkDeprecatedMapOptions(processorOptions, options)

        if (processorOptions.containsKey("targetDir")) {
            options.targetDir = processorOptions["targetDir"].toString()
        } else {
            log.warn("required option 'targetDir' is missing!")
        }

        if (processorOptions.containsKey("mapping")) {
            readMapping(processorOptions["mapping"].toString(), options)
        } else {
            log.warn("required option 'mapping' is missing!")
        }

        return options
    }

    private fun readMapping(mappingSource: String, options: ApiOptions) {
        val mapping: MappingVersion? = MappingReader().read(mappingSource)
        if (mapping == null) {
            log.warn("missing 'mapping.yaml' configuration!")
            return
        }

        when (mapping) {
            is MappingV1 -> {
                options.packageName = mapping.options.packageName
                options.beanValidation = mapping.options.beanValidation
            }
            is MappingV2 -> {
                options.packageName = mapping.options.packageName
                options.modelNameSuffix = mapping.options.modelNameSuffix
                options.beanValidation = mapping.options.beanValidation
                options.javadoc = mapping.options.javadoc
                options.oneOfInterface = mapping.options.oneOfInterface
                options.formatCode = mapping.options.formatCode
            }
        }

        if (options.packageName == "io.openapiprocessor.generated") {
            log.warn("is 'options.package-name' set in mapping? found default: '{}'.", options.packageName)
        }

        options.typeMappings = MappingConverter().convert(mapping)
    }

    private fun checkDeprecatedMapOptions(processorOptions: Map<String, *>, options: ApiOptions) {
        if (!checkObsoleteProcessorOptions)
            return

        if (processorOptions.containsKey("packageName")) {
            options.packageName = processorOptions["packageName"].toString()
            log.warn("'options.package-name' should be set in the mapping yaml!")
        }

        if (processorOptions.containsKey("beanValidation")) {
            options.beanValidation = processorOptions["beanValidation"] as Boolean
            log.warn("options.bean-validation' should be set in the mapping yaml!")
        }

        if (processorOptions.containsKey("typeMappings")) {
            readMapping(processorOptions["typeMappings"].toString(), options)
            log.warn("'typeMappings' option is deprecated, please use 'mapping'!")
        }
    }

}

