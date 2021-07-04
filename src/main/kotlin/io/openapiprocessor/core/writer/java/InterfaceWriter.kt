/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.framework.FrameworkAnnotations
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.Interface
import io.openapiprocessor.core.model.parameters.AdditionalParameter
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
                if (apiOptions.beanValidation) {
                    val info = validationAnnotations.validate(p.dataType, p.required)
                    imports.addAll (info.imports)
                }

                if (p.withAnnotation) {
                    imports.add (annotations.getAnnotation (p).fullyQualifiedName)
                }

                if (p is AdditionalParameter && p.annotationDataType != null) {
                    imports.addAll (p.annotationDataType.getImports())
                }

                imports.addAll (p.dataTypeImports)
            }

            ep.requestBodies.forEach { b ->
                imports.add (annotations.getAnnotation (b).fullyQualifiedName)
                imports.addAll (b.dataTypeImports)
                if (apiOptions.beanValidation) {
                    val info = validationAnnotations.validate(b.dataType, false)
                    imports.addAll (info.imports)
                }
            }

            ep.endpointResponses.forEach { mr ->
                val responseImports: MutableSet<String> = mr.responseImports.toMutableSet()
                if (responseImports.isNotEmpty()) {
                    imports.addAll (responseImports)
                }
            }
        }

        return importFilter
            .filter(packageName, imports)
            .sorted ()
    }

}
