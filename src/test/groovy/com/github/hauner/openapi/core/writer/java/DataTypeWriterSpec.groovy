/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package com.github.hauner.openapi.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.datatypes.StringDataType
import io.openapiprocessor.core.support.datatypes.ListDataType
import io.openapiprocessor.core.support.datatypes.ObjectDataType
import io.openapiprocessor.core.writer.java.BeanValidationFactory
import io.openapiprocessor.core.writer.java.DataTypeWriter
import io.openapiprocessor.core.writer.java.JavaDocWriter
import io.openapiprocessor.core.writer.java.SimpleWriter
import spock.lang.Specification

import static io.openapiprocessor.core.AssertKt.extractImports

class DataTypeWriterSpec extends Specification {
    def headerWriter = Mock SimpleWriter
    def options = new ApiOptions()

    def writer = new DataTypeWriter(options, headerWriter, new BeanValidationFactory(), new JavaDocWriter())
    def target = new StringWriter ()

    void "writes 'generated' comment" () {
        def dataType = new ObjectDataType(
            'Book', '', [:], null, false, null)

        when:
        writer.write (target, dataType)

        then:
        1 * headerWriter.write (target)
    }

    void "writes 'package'" () {
        def pkg = 'com.github.hauner.openapi'
        def dataType = new ObjectDataType (
            'Book', pkg, [:], null, false, null)

        when:
        writer.write (target, dataType)

        then:
        target.toString ().contains ("""\
package $pkg;

""")
    }

    void "writes imports of nested types" () {
        def pkg = 'external'

        def dataType = new ObjectDataType ('Book', 'mine', [
            'isbn': new ObjectDataType (
                'Isbn', pkg, [:], null, false, null)
        ], null, false, null)

        when:
        writer.write (target, dataType)

        then:
        def result = extractImports (target)
        result.contains("""\
import external.Isbn;
""")
    }

    void "writes import of generic list type" () {
        def dataType = new ObjectDataType ('Book', 'mine', [
            'authors': new ListDataType (new StringDataType())
        ], null, false, null)

        when:
        writer.write (target, dataType)

        then:
        def result = extractImports (target)
        result.contains("""\
import java.util.List;
""")
    }

    void "writes properties"() {
        def pkg = 'com.github.hauner.openapi'
        def dataType = new ObjectDataType ('Book', pkg, [
            isbn: new StringDataType(),
            title: new StringDataType ()
        ], null, false, null)

        when:
        writer.write (target, dataType)

        then:
        target.toString ().contains ("""\
    private String isbn;

""")
        target.toString ().contains ("""\
    private String title;

""")
    }

    void "writes property getters & setters" () {
        def pkg = 'com.github.hauner.openapi'
        def dataType = new ObjectDataType ('Book', pkg, [
            isbn: new StringDataType(),
            title: new StringDataType ()
        ], null, false, null)

        when:
        writer.write (target, dataType)

        then:
        target.toString ().contains ("""\
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

""")
        target.toString ().contains ("""\
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

""")
    }

    void "writes deprecated class" () {
        def pkg = 'io.openapiprocessor.core'
        def dataType = new ObjectDataType (
            'Bar', pkg, [:],null, true, null)

        when:
        writer.write (target, dataType)

        then:
        target.toString ().contains ("""\
@Deprecated
public class Bar {

}
""")
    }

    void "writes deprecated property" () {
        def pkg = 'com.github.hauner.openapi'
        def dataType = new ObjectDataType ('Book', pkg, [
            isbn: new StringDataType(null, true, null)
        ], null, false, null)

        when:
        writer.write (target, dataType)

        then:
        target.toString ().contains ("""\
    @Deprecated
    @JsonProperty("isbn")
    private String isbn;
""")
    }

    void "writes deprecated property getters & setters" () {
        def pkg = 'com.github.hauner.openapi'
        def dataType = new ObjectDataType ('Book', pkg, [
            isbn: new StringDataType(null, true, null)
        ], null, false, null)

        when:
        writer.write (target, dataType)

        then:
        target.toString ().contains ("""\
    @Deprecated
    public String getIsbn() {
        return isbn;
    }

    @Deprecated
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

""")
    }

    void "writes properties with valid java identifiers" () {
        def pkg = 'com.github.hauner.openapi'

        def dataType = new ObjectDataType ('Book', pkg, [
            'a-isbn' : new StringDataType (),
            'a-title': new StringDataType ()
        ], null, false, null)

        when:
        writer.write (target, dataType)

        then:
        target.toString ().contains ("""\
    private String aIsbn;
""")

        target.toString ().contains ("""\
    private String aTitle;
""")
    }

    void "writes imports of @JsonProperty" () {
        def pkg = 'external'

        def dataType = new ObjectDataType ('Book', pkg, [
            'isbn' : new StringDataType (),
            'title': new StringDataType ()
        ], null, false, null)

        when:
        writer.write (target, dataType)

        then:
        def result = extractImports (target)
        result.contains("""\
import com.fasterxml.jackson.annotation.JsonProperty;
""")
    }

    void "writes properties with @JsonProperty annotation" () {
        def pkg = 'com.github.hauner.openapi'

        def dataType = new ObjectDataType ('Book', pkg, [
                    'a-isbn': new StringDataType (),
                    'a-title': new StringDataType ()
                ], null, false, null)

        when:
        writer.write (target, dataType)

        then:
        target.toString ().contains ("""\
    @JsonProperty("a-isbn")
    private String aIsbn;

    @JsonProperty("a-title")
    private String aTitle;

""")
    }

}
