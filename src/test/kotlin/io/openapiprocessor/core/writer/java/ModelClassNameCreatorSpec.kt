/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ModelClassNameCreatorSpec : StringSpec({

    "adds suffix to name" {
        val creator = ModelClassNameCreator("X")
        creator.createName("Foo") shouldBe "FooX"
    }

    "ignores suffix if name already ends with the suffix" {
        val creator = ModelClassNameCreator("X")
        creator.createName("FooX") shouldBe "FooX"
    }

})
