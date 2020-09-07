/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.builder.api

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.openapiprocessor.core.model.HttpMethod
import io.openapiprocessor.core.model.datatypes.StringDataType

class EndpointBuilderSpec: StringSpec({

    "sets endpoint path" {
        endpoint("/foo") {}.path shouldBe "/foo"
    }

    "sets endpoint deprecated" {
        endpoint("/any") {
            deprecated()
        }
        .deprecated shouldBe true
    }

    "endpoint method default to GET" {
        endpoint("/foo") {}
            .method shouldBe HttpMethod.GET
    }

    "sets endpoint method" {
        endpoint("/foo", HttpMethod.DELETE) {}
            .method shouldBe HttpMethod.DELETE
    }

    "adds endpoint response" {
        val ep = endpoint("/foo") {
            responses {
                status("200") {
                    response()
                }
            }
        }

        ep.endpointResponses.size shouldBe 1
        ep.endpointResponses.first().contentType shouldNotBe null
        ep.endpointResponses.first().responseType shouldNotBe null
    }

    "adds endpoint parameter" {
        val ep = endpoint("/foo") {
            parameters {
                query("foo", StringDataType())
            }
        }

        ep.parameters.size shouldBe 1
        ep.parameters.first().name shouldBe "foo"
    }

    "adds endpoint request bodies" {
        val ep = endpoint("/foo") {
            parameters {
                body("foo", "text/plain", StringDataType())
            }
        }

        ep.requestBodies.size shouldBe 1
        ep.requestBodies.first().name shouldBe "foo"
    }

})
