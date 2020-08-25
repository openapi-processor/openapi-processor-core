package com.github.hauner.openapi.core.learn

import io.openapiprocessor.core.model.datatypes.ObjectDataType
import io.openapiprocessor.core.model.datatypes.DataType
import spock.lang.Specification

class PropertyNotationSpec extends Specification {

    interface Foo {
        String getProp()
    }

    class Bar implements Foo {

        String propValue

        @Override
        String getProp () {
            return propValue
        }
    }

    void "property notation can be used with interfaces" () {
        def bar = new Bar(propValue: 'property')

        expect:
        bar.prop != null
        bar.prop == bar.getProp ()
    }

    void "property notation can be used on data type interface" () {
        // CompositeDataType must not implement a getProperty() method. It overrides groovy's
        // getProperty() and that breaks the groovy property notation.

        def object = new ObjectDataType ('Type', 'pkg', [:], null, false)
        DataType typed = new ObjectDataType ('Typed', 'pkg', [:],null, false)

        expect:
        object.name != null
        object.name == object.getName ()
        typed.name != null
        typed.name == typed.getName ()
    }
}
