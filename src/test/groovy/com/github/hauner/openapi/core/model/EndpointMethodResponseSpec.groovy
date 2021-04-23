/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package com.github.hauner.openapi.core.model

import io.openapiprocessor.core.model.datatypes.CollectionDataType
import io.openapiprocessor.core.model.datatypes.StringDataType
import io.openapiprocessor.core.model.HttpMethod
import spock.lang.Specification

import static io.openapiprocessor.core.builder.api.EndpointBuilderKt.endpoint

class EndpointMethodResponseSpec extends Specification {

    void "creates single success/other content type groups" () {
        def endpoint = endpoint('/foo', HttpMethod.GET) { eb ->
            eb.responses { rs ->
                rs.status ('200') {r ->
                    r.response (
                        'application/json',
                        new CollectionDataType (new StringDataType ())) {
                    }
                }
            }
        }

        when:
        def result = endpoint.endpointResponses

        then:
        result.size () == 1
        result[0].main.contentType == 'application/json'
        result[0].errors as List == []
    }

    void "groups response content types to multiple success/other content type groups" () {
        def endpoint = endpoint('/foo', HttpMethod.GET) { eb ->
            eb.responses { rs ->
                rs.status ('200') {r ->
                    r.response ('application/json',
                        new CollectionDataType (new StringDataType ())) {
                    }
                    r.response ('application/xml',
                        new CollectionDataType (new StringDataType ())) {
                    }
                }
                rs.status ('default') {r ->
                    r.response ('text/plain',
                        new CollectionDataType (new StringDataType ())) {
                    }
                }
            }
        }

        when:
        def result = endpoint.endpointResponses

        then:
        result.size () == 2
        result[0].main.contentType == 'application/json'
        result[0].errors.collect {it.contentType} == ['text/plain']
        result[1].main.contentType == 'application/xml'
        result[1].errors.collect {it.contentType} == ['text/plain']
    }

    void "provides distinct response content type groups" () {
        def endpoint = endpoint('/foo', HttpMethod.GET) { eb ->
            eb.responses { rs ->
                rs.status ('200') {r ->
                    r.response ('application/json',
                        new CollectionDataType (new StringDataType ())) {
                    }
                }
                rs.status ('400') {r ->
                    r.response ('application/json',
                        new CollectionDataType (new StringDataType ())) {
                    }
                }
                rs.status ('401') {r ->
                    r.response ('application/json',
                        new CollectionDataType (new StringDataType ())) {
                    }
                }
            }
        }

        when:
        def result = endpoint.endpointResponses

        then:
        result.size () == 1
        result[0].contentTypes == ['application/json'] as Set

    }

}
