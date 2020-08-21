package io.openapiprocessor.core

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class DummySpec: StringSpec({

    beforeTest {
    }

    afterTest {
    }

    "dummy test spec" {
        true shouldBe true
    }

})
