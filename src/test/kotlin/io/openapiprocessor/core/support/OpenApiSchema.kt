/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import io.openapiprocessor.core.converter.SchemaInfo
import io.openapiprocessor.core.model.HttpMethod
import io.openapiprocessor.core.parser.OpenApi
import io.openapiprocessor.core.parser.Schema


fun OpenApi.getParameterSchema(path: String, method: HttpMethod, name: String): Schema {
    val endpoint = this.getPaths()[path]
    if (endpoint == null) {
        println("unknown path '$path' !")
    }

    val operation = endpoint?.getOperations()?.find { it.getMethod() == method }
    if (operation == null) {
        println("unknown method '$method' ($path)!")
    }

    val parameter = operation?.getParameters()?.find { it.getName() == name }
    if (parameter == null) {
        println("unknown parameter '$name' ($path)!")
    }

    return parameter?.getSchema()!!
}

/**
 * extracts a specific response Schema from an [OpenApi] object created by [parse()][parse].
 *
 * @param path the endpoint path
 * @param method the http method
 * @param status the http response status
 * @return the [Schema]
 */
fun OpenApi.getSchema(path: String, method: HttpMethod, status: String, mediaType: String)
: Schema {
    val endpoint = this.getPaths()[path]
    if (endpoint == null) {
        println("unknown path '$path' !")
    }

    val operation = endpoint?.getOperations()?.find { it.getMethod() == method }
    if (operation == null) {
        println("unknown method '$method' ($path)!")
    }

    val response = operation?.getResponses()?.get(status)
    if (response == null) {
        println("unknown response status '$status' ($path $method)!")
    }

    val media = response?.getContent()?.get(mediaType)
    if (media == null) {
        println("unknown media type '$mediaType' ($path $method $status)!")
    }

    return media?.getSchema()!!
}

/**
 * extracts a specific request body Schema from an [OpenApi] object created by [parse()][parse].
 *
 * @param path the endpoint path
 * @param method the http method
 * @return the [Schema]
 */
fun OpenApi.getBodySchema(path: String, method: HttpMethod, mediaType: String): Schema {
    val endpoint = this.getPaths()[path]
    if (endpoint == null) {
        println("unknown path '$path' !")
    }

    val operation = endpoint?.getOperations()?.find { it.getMethod() == method }
    if (operation == null) {
        println("unknown method '$method' ($path)!")
    }

    val body = operation?.getRequestBody()
    if (body == null) {
        println("no body ($path $method)!")
    }

    val media = body?.getContent()?.get(mediaType)
    if (media == null) {
        println("unknown media type '$mediaType' ($path $method)!")
    }

    return media?.getSchema()!!
}

fun OpenApi.getParameterSchemaInfo(path: String, method: HttpMethod, name: String) : SchemaInfo {
    val schema = getParameterSchema(path, method, name)
    return SchemaInfo(SchemaInfo.Endpoint(path, method), name, "", schema, getRefResolver())
}

/**
 * extracts a specific response Schema from an [OpenApi] object created by [parse()][parse] and
 * creates a [SchemaInfo] for the schema.
 *
 * @param name name of schema info, i.e. the datatype name
 * @param path the endpoint path
 * @param method the http method
 * @param status the http response status
 * @return the [SchemaInfo]
 */
fun OpenApi.getSchemaInfo(name: String, path: String, method: HttpMethod, status: String,
          mediaType: String): SchemaInfo {
    val schema = getSchema(path, method, status, mediaType)
    return SchemaInfo(SchemaInfo.Endpoint(path, method), name, mediaType, schema, getRefResolver())
}

/**
 * extracts a specific body Schema from an [OpenApi] object created by [parse()][parse] and
 * creates a [SchemaInfo] for the schema.
 *
 * @param name name of schema info, i.e. the datatype name
 * @param path the endpoint path
 * @param method the http method
 * @return the [SchemaInfo]
 */
@Suppress("UNUSED_PARAMETER")
fun OpenApi.getBodySchemaInfo(name: String, path: String, method: HttpMethod, mediaType: String)
: SchemaInfo {
    val schema = getBodySchema(path, method, mediaType)
    return SchemaInfo(SchemaInfo.Endpoint(path, method),
        path.substring(1).replaceFirstChar { it.uppercase() } + "RequestBody",
        mediaType, schema, getRefResolver())
}
