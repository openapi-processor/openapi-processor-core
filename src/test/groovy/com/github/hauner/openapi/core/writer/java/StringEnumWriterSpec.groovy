/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package com.github.hauner.openapi.core.writer.java

import io.openapiprocessor.core.model.datatypes.StringEnumDataType
import io.openapiprocessor.core.support.datatypes.DataTypeName
import io.openapiprocessor.core.writer.java.SimpleWriter
import io.openapiprocessor.core.writer.java.StringEnumWriter
import spock.lang.Specification

class StringEnumWriterSpec extends Specification {
    def headerWriter = Mock SimpleWriter

    def writer = new StringEnumWriter(headerWriter)
    def target = new StringWriter ()

    void "writes 'generated' comment" () {
        def dataType = new StringEnumDataType(
            new DataTypeName('Foo'), 'pkg', [], null, false)

        when:
        writer.write (target, dataType)

        then:
        1 * headerWriter.write (target)
    }

    void "writes 'package'" () {
        def pkg = 'com.github.hauner.openapi'
        def dataType = new StringEnumDataType(
            new DataTypeName('Foo'), pkg, [], null, false)

        when:
        writer.write (target, dataType)

        then:
        target.toString ().contains ("""\
package $pkg;

""")
    }

    void "writes enum class"() {
        def pkg = 'com.github.hauner.openapi'
        def dataType = new StringEnumDataType(
            new DataTypeName(id, type), pkg, [], null, false)

        when:
        writer.write (target, dataType)

        then:
        target.toString ().contains ("""\
public enum $type {
""")
        target.toString ().contains ("""\
}
""")

        where:
        id    | type
        'Foo' | 'Foo'
        'Foo' | 'FooX'
    }

    void "writes enum values"() {
        def pkg = 'com.github.hauner.openapi'
        def dataType = new StringEnumDataType(
            new DataTypeName('Foo'), pkg, ['foo', '_foo-2', 'foo-foo'], null, false)

        when:
        writer.write (target, dataType)

        then:
        target.toString ().contains ("""\
public enum Foo {
    FOO("foo"),
    FOO_2("_foo-2"),
    FOO_FOO("foo-foo");

""")
    }

    void "writes value member"() {
        def pkg = 'com.github.hauner.openapi'
        def dataType = new StringEnumDataType(
            new DataTypeName('Foo'), pkg, ['foo', '_foo-2', 'foo-foo'], null, false)

        when:
        writer.write (target, dataType)

        then:
        target.toString ().contains ("""\
    private final String value;

""")
    }

    void "writes enum constructor"() {
        def pkg = 'com.github.hauner.openapi'
        def dataType = new StringEnumDataType (
            new DataTypeName(id, type), pkg, ['foo', '_foo-2', 'foo-foo'], null, false)

        when:
        writer.write (target, dataType)

        then:
        target.toString ().contains ("""\

    $type(String value) {
        this.value = value;
    }

""")

        where:
        id    | type
        'Foo' | 'Foo'
        'Foo' | 'FooX'
    }

    void "writes @JsonValue method for serialization"() {
        def pkg = 'com.github.hauner.openapi'
        def dataType = new StringEnumDataType (
            new DataTypeName('Foo'), pkg, ['foo', '_foo-2', 'foo-foo'], null, false)

        when:
        writer.write (target, dataType)

        then:
        target.toString ().contains ("""\

    @JsonValue
    public String getValue() {
        return this.value;
    }

""")
    }

    void "writes @JsonCreator method for de-serialization"() {
        def pkg = 'com.github.hauner.openapi'
        def dataType = new StringEnumDataType (
            new DataTypeName(id, type), pkg, ['foo', '_foo-2', 'foo-foo'], null, false)

        when:
        writer.write (target, dataType)

        then:
        target.toString ().contains ("""\

    @JsonCreator
    public static $type fromValue(String value) {
        for ($type val : ${type}.values()) {
            if (val.value.equals(value)) {
                return val;
            }
        }
        throw new IllegalArgumentException(value);
    }

""")

        where:
        id    | type
        'Foo' | 'Foo'
        'Foo' | 'FooX'
    }

    void "writes jackson imports" () {
        def pkg = 'com.github.hauner.openapi'
        def dataType = new StringEnumDataType(
            new DataTypeName('Foo'), pkg, [], null, false)

        when:
        writer.write (target, dataType)

        then:
        target.toString ().contains ("""\
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

""")
    }

}



