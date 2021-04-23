/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package com.github.hauner.openapi.core.model

import io.openapiprocessor.core.model.HttpMethod
import io.openapiprocessor.core.model.datatypes.ObjectDataType
import io.openapiprocessor.core.model.datatypes.StringDataType
import spock.lang.Specification

import static io.openapiprocessor.core.builder.api.EndpointBuilderKt.endpoint

class EndpointContentTypesSpec extends Specification {

    void "provides no consuming content types without body" () {
        def endpoint = endpoint('/foo', HttpMethod.GET) { e ->
            e.responses { r ->
                r.status ('204') {it.empty () }
            }
        }

        expect:
        endpoint.consumesContentTypes == [] as Set
    }

    void "provides distinct consuming content types" () {
        def endpoint = endpoint('/foo', HttpMethod.GET) { e ->
            e.parameters () { ps ->
                ps.body("body", "text/plain", new StringDataType())
                ps.body("body", "application/json", new StringDataType())
                ps.body("body", "text/plain", new StringDataType())
            }
            e.responses { r ->
                r.status ('204') {it.empty () }
            }
        }

        expect:
        endpoint.consumesContentTypes.sort() == ['text/plain', 'application/json'].sort ()
    }

    void "provides consuming content type for multipart/form-data" () {
        def endpoint = endpoint('/foo', HttpMethod.GET) { e ->
            e.parameters () { ps ->
                ps.body ("body", "multipart/form-data",
                    new ObjectDataType ('Foo', '', [
                        foo: new StringDataType (),
                        bar: new StringDataType ()
                    ], null, false, null))
            }
            e.responses { r ->
                r.status ('204') {it.empty () }
            }
        }

        expect:
        endpoint.consumesContentTypes == ['multipart/form-data'] as Set
    }

    void "provides no producing content types without response" () {
        def endpoint = endpoint('/foo', HttpMethod.GET) { e ->
            e.responses { r ->
                r.status ('204') {it.empty () }
            }
        }

        expect:
        endpoint.getProducesContentTypes ('204') == []
    }

    void "provides producing content types" () {
        def endpoint = endpoint('/foo', HttpMethod.GET) { e ->
            e.responses { rs ->
                rs.status ('200') { r ->
                    r.response ('text/plain', new StringDataType())
                    r.response ('application/json', new StringDataType())
                }
                rs.status ('401') { r ->
                    r.response ('text/plain', new StringDataType())
                }
            }
        }

        expect:
        endpoint.getProducesContentTypes ('200').sort () == ['text/plain', 'application/json'].sort ()
    }

}

