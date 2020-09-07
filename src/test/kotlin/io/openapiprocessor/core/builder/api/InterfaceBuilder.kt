/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.builder.api

import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.HttpMethod
import io.openapiprocessor.core.model.Interface
import io.openapiprocessor.core.builder.api.endpoint as ep

fun `interface`(
    name: String = "Foo",
    pkg: String = "io.openapiprocessor.test", init: InterfaceBuilder.() -> Unit): Interface {

    val builder = InterfaceBuilder(name, pkg)
    init(builder)
    return builder.build()
}

class InterfaceBuilder(
    private val name: String,
    private val pkg: String
) {
    private val endpoints = mutableListOf<Endpoint>()

    fun endpoint(path: String, method: HttpMethod = HttpMethod.GET, init: EndpointBuilder.() -> Unit) {
        endpoints.add(ep(path, method, init))
    }

    fun build(): Interface {
        return Interface(name, pkg, endpoints)
    }

}
