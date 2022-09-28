/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package com.github.hauner.openapi.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.framework.FrameworkAnnotations
import io.openapiprocessor.core.model.Interface
import io.openapiprocessor.core.writer.java.BeanValidationFactory
import io.openapiprocessor.core.writer.java.DefaultImportFilter
import io.openapiprocessor.core.writer.java.InterfaceWriter
import io.openapiprocessor.core.writer.java.MethodWriter
import io.openapiprocessor.core.writer.java.SimpleGeneratedWriter
import spock.lang.Specification

class InterfaceWriterSpec extends Specification {
    def apiOptions = new ApiOptions()
    def generatedWriter = new SimpleGeneratedWriter (apiOptions)
    def methodWriter = Stub MethodWriter
    def annotations = Stub (FrameworkAnnotations)

    def writer = new InterfaceWriter(
        apiOptions,
        generatedWriter,
        methodWriter,
        annotations,
        new BeanValidationFactory(),
        new DefaultImportFilter())
    def target = new StringWriter ()

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

    void "writes @Generated import" () {
        def apiItf = new Interface ("", "", [])

        when:
        writer.write (target, apiItf)

        then:
        target.toString ().contains ("""\
import io.openapiprocessor.generated.support.Generated;
""")
    }

    void "writes 'interface' block" () {
        def apiItf = new Interface ('name', 'pkg', [])

        when:
        writer.write (target, apiItf)

        then:
        def result = target.toString ().contains (
"""\
@Generated
public interface NameApi {
}
""")
    }
}
