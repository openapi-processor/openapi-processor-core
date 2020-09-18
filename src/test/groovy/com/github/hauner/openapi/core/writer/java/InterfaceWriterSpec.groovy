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

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.framework.FrameworkAnnotations
import io.openapiprocessor.core.model.Interface
import io.openapiprocessor.core.writer.java.BeanValidationFactory
import io.openapiprocessor.core.writer.java.DefaultImportFilter
import io.openapiprocessor.core.writer.java.InterfaceWriter
import io.openapiprocessor.core.writer.java.MethodWriter
import io.openapiprocessor.core.writer.java.SimpleWriter
import spock.lang.Specification

import java.util.stream.Collectors

import static com.github.hauner.openapi.core.test.AssertHelper.extractImports

class InterfaceWriterSpec extends Specification {
    def headerWriter = Mock SimpleWriter
    def methodWriter = Stub MethodWriter
    def annotations = Stub (FrameworkAnnotations)
    def apiOptions = new ApiOptions()

    def writer = new InterfaceWriter(
        apiOptions,
        headerWriter,
        methodWriter,
        annotations,
        new BeanValidationFactory(),
        new DefaultImportFilter())
    def target = new StringWriter ()

    void "writes 'generated' comment" () {
        def apiItf = new Interface ("", "", [])

        when:
        writer.write (target, apiItf)

        then:
        1 * headerWriter.write (target)
    }

    void "writes 'package'" () {
        def pkg = 'com.github.hauner.openapi'
        def apiItf = new Interface ("", pkg, [])

        when:
        writer.write (target, apiItf)

        then:
        target.toString ().contains (
"""\
package $pkg;

""")
    }

    void "writes 'interface' block" () {
        def apiItf = new Interface ('name', 'pkg', [])

        when:
        writer.write (target, apiItf)

        then:
        def result = extractInterfaceBlock(target.toString ())
        result == """\
public interface NameApi {
}
"""
    }

    String extractInterfaceBlock (String source) {
        source.readLines ().stream ()
            .filter {it ==~ /public interface (.+?) \{/ || it ==~ /}/}
            .collect (Collectors.toList ())
            .join ('\n') + '\n'
    }

    String extractInterfaceBody (String source) {
        source
            .replaceFirst (/(?s)(.*?)interface (.+?) \{\n/, '')
            .replaceFirst (/(?s)}\n/, '')
    }

}
