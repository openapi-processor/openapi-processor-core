/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.builder.api

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class InterfaceSpec: StringSpec ({

    "sets interface name" {
        `interface`(name = "Foo") {}.name shouldBe "Foo"
    }

    "sets interface package" {
        `interface`(pkg = "package") {}.name shouldBe "Foo"
    }

    "adds endpoint to interface" {
        val itf = `interface` {
            endpoint("/foo") {

            }
        }

        itf.getEndpoint("/foo").path shouldBe "/foo"
    }

})

