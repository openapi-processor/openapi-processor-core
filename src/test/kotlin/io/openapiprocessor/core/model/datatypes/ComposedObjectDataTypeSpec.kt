/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.support.datatypes.ObjectDataType

class ComposedObjectDataTypeSpec : StringSpec({

    "loop properties of allOf objects as if it was a single object" {
        val composed = AllOfObjectDataType("Foo", "pkg", "allOf", listOf(
            ObjectDataType("Foo", "pkg", linkedMapOf(
                Pair("foo", StringDataType()),
                Pair("foobar", StringDataType())
            )),
            ObjectDataType("Bar", "pkg", linkedMapOf(
                Pair("bar", StringDataType()),
                Pair("barfoo", StringDataType())
            ))
        ))

        val properties = composed.getProperties()

        properties.keys shouldBe linkedSetOf("foo", "foobar", "bar", "barfoo")
    }

})
