/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.builder.api

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ApiModelBuilderSpec: StringSpec ({

    "sets endpoint path" {
        endpoint("/foo") {}.path shouldBe "/foo"
    }

    "sets endpoint deprecated" {
        endpoint("/any") {
            deprecated()
        }.deprecated shouldBe true
    }

})
