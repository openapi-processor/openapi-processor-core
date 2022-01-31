/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.converter.mapping.Mapping
import io.openapiprocessor.core.converter.mapping.ResultStyleOptionMapping
import io.openapiprocessor.core.processor.mapping.v2.ResultStyle
import io.openapiprocessor.core.support.Empty

/**
 * Options of the processor.
 */
class ApiOptions {

    /**
     * the destination folder for generating interfaces & models. This is the parent of the
     * {@link #packageName} folder tree.
     */
    var targetDir: String? = null

    /**
     * the root package of the generated interfaces/model. The package folder tree will be created
     * inside {@link #targetDir}. Interfaces and models will be placed into the "api" and "model"
     * subpackages of packageName:
     * - interfaces => "${packageName}.api"
     * - models => "${packageName}.model"
     */
    var packageName = "io.openapiprocessor.generated"

    /**
     * enable Bean Validation (JSR303) annotations. Default is false (disabled)
     */
    var beanValidation = false

    /**
     * enable generation of javadoc comments based on the `description` OpenAPI property.
     *
     * *experimental*
     */
    var javadoc = false

    /**
     * suffix for model class names and enum names. Default is none, ie. an empty string.
     */
    var modelNameSuffix = String.Empty

    /**
     * enable/disable the code formatter (optional).
     */
    var formatCode = true

    /**
     * provide additional type mapping information to map OpenAPI types to java types. The list can
     * contain the following mappings:
     *
     * {@link io.openapiprocessor.core.converter.mapping.TypeMapping}: used to globally
     * override the mapping of an OpenAPI schema to a specific java type.
     *
     * {@link io.openapiprocessor.core.converter.mapping.EndpointTypeMapping}: used to
     * override parameter/response type mappings or to add additional parameters on a single
     * endpoint.
     */
    var typeMappings: List<Mapping> = emptyList()

    /**
     * validate that targetDir is set, throws if not.
     */
    fun validate() {
        if (targetDir == null) {
            throw InvalidOptionException("targetDir")
        }
    }

}

/**
 * get (global) result style option mapping value if set, otherwise [ResultStyle.SUCCESS].
 *
 * this is a shortcut to avoid the dependency on the
 * [io.openapiprocessor.core.converter.mapping.MappingFinder] for this *simple* case.
 */
val ApiOptions.resultStyle: ResultStyle
    get() {
        val matches = typeMappings
            .filterIsInstance(ResultStyleOptionMapping::class.java)

        if (matches.isEmpty())
            return ResultStyle.SUCCESS

        return matches.first().value
    }
