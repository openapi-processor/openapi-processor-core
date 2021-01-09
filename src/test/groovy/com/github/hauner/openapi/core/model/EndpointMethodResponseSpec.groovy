/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package com.github.hauner.openapi.core.model

import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.Response
import io.openapiprocessor.core.model.datatypes.CollectionDataType
import io.openapiprocessor.core.model.datatypes.StringDataType
import io.openapiprocessor.core.model.HttpMethod
import spock.lang.Specification

class EndpointMethodResponseSpec extends Specification {

    void "creates single success/other content type groups" () {
        def endpoint = new Endpoint ('/foo', HttpMethod.GET, null, false, null, [
            '200'    : [
                new Response (
                    'application/json',
                    new CollectionDataType (new StringDataType ()),
                    null)
            ]
        ]).initEndpointResponses ()

        when:
        def result = endpoint.endpointResponses

        then:
        result.size () == 1
        result[0].main.contentType == 'application/json'
        result[0].errors as List == []
    }

    void "groups response content types to multiple success/other content type groups" () {
        def endpoint = new Endpoint ('/foo', HttpMethod.GET, null, false, null, [
            '200'    : [
                new Response ('application/json',
                    new CollectionDataType (new StringDataType ()), null),
                new Response ('application/xml',
                    new CollectionDataType (new StringDataType ()), null)
            ],
            'default': [
                new Response ('text/plain',
                    new CollectionDataType (new StringDataType ()), null)
            ]
        ]).initEndpointResponses ()

        when:
        def result = endpoint.endpointResponses

        then:
        result.size () == 2
        result[0].main.contentType == 'application/json'
        result[0].errors.collect {it.contentType} == ['text/plain']
        result[1].main.contentType == 'application/xml'
        result[1].errors.collect {it.contentType} == ['text/plain']
    }

}
