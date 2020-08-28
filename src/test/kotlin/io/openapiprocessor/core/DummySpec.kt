/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

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
