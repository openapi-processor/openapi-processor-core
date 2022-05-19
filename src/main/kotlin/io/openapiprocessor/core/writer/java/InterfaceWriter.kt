/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.framework.FrameworkAnnotations
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.resultStyle
import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.EndpointResponse
import io.openapiprocessor.core.model.Interface
import io.openapiprocessor.core.model.RequestBody
import io.openapiprocessor.core.model.parameters.AdditionalParameter
import io.openapiprocessor.core.model.parameters.Parameter
import java.io.Writer

/**
 * Writer for Java interfaces.
 */
class InterfaceWriter(

    private val apiOptions: ApiOptions,
    private val headerWriter: SimpleWriter,
    private val methodWriter: MethodWriter,
    private val annotations: FrameworkAnnotations,
    private val validationAnnotations: BeanValidationFactory = BeanValidationFactory(),
    private val importFilter: ImportFilter = DefaultImportFilter()

) {

    fun write(target: Writer, itf: Interface) {
        headerWriter.write (target)
        target.write ("package ${itf.getPackageName()};\n\n")

        val imports: List<String> = collectImports (itf.getPackageName(), itf.endpoints)
        imports.forEach {
            target.write("import ${it};\n")
        }
        target.write("\n")

        target.write("public interface ${itf.getInterfaceName()} {\n\n")

        itf.endpoints.forEach { ep ->
            ep.endpointResponses.forEach { er ->
                methodWriter.write(target, ep, er)
                target.write("\n")
            }
        }

        target.write ("}\n")
    }

    private fun collectImports(packageName: String, endpoints: List<Endpoint>): List<String> {
        val imports: MutableSet<String> = mutableSetOf()

        endpoints.forEach { ep ->
            imports.add(annotations.getAnnotation (ep.method).fullyQualifiedName)

            if (ep.deprecated) {
                imports.add (java.lang.Deprecated::class.java.canonicalName)
            }

            ep.parameters.forEach { p ->
                addImports(p, imports)
            }

            ep.requestBodies.forEach { b ->
                addImports(b, imports)
            }

            ep.endpointResponses.forEach { r ->
                addImports(r, imports)
            }
        }

        return importFilter
            .filter(packageName, imports)
            .sorted ()
    }

    private fun addImports(parameter: Parameter, imports: MutableSet<String>) {
        if (apiOptions.beanValidation) {
            val info = validationAnnotations.validate(parameter.dataType, parameter.required)
            imports.addAll(info.inout.imports)
        }

        if (parameter.withAnnotation) {
            imports.add(annotations.getAnnotation(parameter).fullyQualifiedName)
        }

        if (parameter is AdditionalParameter && parameter.annotationDataType != null) {
            imports.addAll(parameter.annotationDataType.getImports())
        }

        imports.addAll(parameter.dataTypeImports)
    }

    private fun addImports(body: RequestBody, imports: MutableSet<String>) {
        imports.add(annotations.getAnnotation(body).fullyQualifiedName)
        imports.addAll(body.dataTypeImports)

        if (apiOptions.beanValidation) {
            val info = validationAnnotations.validate(body.dataType, body.required)
            val io = info.inout
            imports.addAll(io.imports)
        }
    }

    private fun addImports(response: EndpointResponse, imports: MutableSet<String>) {
        val responseImports: MutableSet<String> = response.getResponseImports(
                    apiOptions.resultStyle).toMutableSet()

        if (responseImports.isNotEmpty()) {
            imports.addAll(responseImports)
        }
    }

}
