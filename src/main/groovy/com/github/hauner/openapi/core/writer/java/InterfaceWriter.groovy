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

package com.github.hauner.openapi.core.writer.java

import com.github.hauner.openapi.core.framework.FrameworkAnnotations
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.Endpoint
import com.github.hauner.openapi.core.model.Interface
import io.openapiprocessor.core.writer.java.DefaultImportFilter
import io.openapiprocessor.core.writer.java.ImportFilter

/**
 * Writer for Java interfaces.
 *
 * @author Martin Hauner
 * @author Bastian Wilhelm
 */
class InterfaceWriter {
    ApiOptions apiOptions
    SimpleWriter headerWriter
    MethodWriter methodWriter
    BeanValidationFactory beanValidationFactory
    FrameworkAnnotations annotations
    ImportFilter importFilter = new DefaultImportFilter()

    void write (Writer target, Interface itf) {
        headerWriter.write (target)
        target.write ("package ${itf.packageName};\n\n")

        List<String> imports = collectImports (itf.packageName, itf.endpoints)
        imports.each {
            target.write ("import ${it};\n")
        }
        target.write ("\n")

        target.write ("public interface ${itf.interfaceName} {\n\n")

        itf.endpoints.each { ep ->
            ep.endpointResponses.each { er ->
                methodWriter.write(target, ep, er)
                target.write ("\n")
            }
        }

        target.write ("}\n")
    }

    List<String> collectImports (String packageName, List<Endpoint> endpoints) {
        Set<String> imports = []

        endpoints.each { ep ->
            imports.add (annotations.getAnnotation (ep.method).fullyQualifiedName)

            if (ep.deprecated) {
                imports.add (Deprecated.canonicalName)
            }

            ep.parameters.each { p ->
                if (apiOptions.beanValidation) {
                    imports.addAll (beanValidationFactory.collectImports (p.dataType))
                }

                if (p.withAnnotation) {
                    imports.add (annotations.getAnnotation (p).fullyQualifiedName)
                }

                imports.addAll (p.dataTypeImports)
            }

            ep.requestBodies.each { b ->
                imports.add (annotations.getAnnotation (b).fullyQualifiedName)
                imports.addAll (b.dataTypeImports)
                if (apiOptions.beanValidation) {
                    imports.addAll (beanValidationFactory.collectImports (b.dataType))
                }
            }

            ep.endpointResponses.each { mr ->
                def responseImports = mr.responseImports
                if (!responseImports.empty) {
                    imports.addAll (responseImports)
                }
            }
        }

        importFilter
            .filter (packageName, imports)
            .sort ()
    }

}
