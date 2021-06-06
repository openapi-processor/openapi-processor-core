/*
 * Copyright 2019-2020 the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.framework.FrameworkAnnotations
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.resultStyle
import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.Interface
import io.openapiprocessor.core.model.parameters.AdditionalParameter
import java.io.Writer

/**
 * Writer for Java interfaces.
 *
 * @author Martin Hauner
 * @author Bastian Wilhelm
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
                val responseImports: MutableSet<String> = mr.getResponseImports(
                    apiOptions.resultStyle).toMutableSet()

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
