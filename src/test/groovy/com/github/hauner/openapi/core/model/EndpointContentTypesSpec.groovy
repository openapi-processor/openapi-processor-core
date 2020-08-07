package com.github.hauner.openapi.core.model

import com.github.hauner.openapi.core.model.datatypes.ObjectDataType
import com.github.hauner.openapi.core.model.datatypes.StringDataType
import spock.lang.Specification

import static io.openapiprocessor.core.model.Builder.endpoint

class EndpointContentTypesSpec extends Specification {

    void "provides no consuming content types without body" () {
        def endpoint = endpoint('/foo') {
            bodies {
            }
            responses ('204') {
                empty ()
            }
        }

        expect:
        endpoint.consumesContentTypes == []
    }

    void "provides consuming content types" () {
        def endpoint = endpoint('/foo') {
            bodies {
                content ('text/plain') {}
                content ('application/json') {}
                content ('text/plain') {}
            }
            responses ('204') {
                empty ()
            }
        }

        expect:
        endpoint.consumesContentTypes == ['text/plain', 'application/json'].sort ()
    }

    void "provides consuming content type for multipart/form-data" () {
        def endpoint = endpoint('/foo') {
            bodies {
                content ('multipart/form-data') {
                    data (new ObjectDataType (properties: [
                        foo: new StringDataType (),
                        bar: new StringDataType ()
                    ]))
                }
            }
            responses ('204') {
                empty ()
            }
        }

        expect:
        endpoint.consumesContentTypes == ['multipart/form-data']
    }

    void "provides no producing content types without response" () {
        def endpoint = endpoint('/foo') {
            responses ('204') {
                empty ()
            }
        }

        expect:
        endpoint.getProducesContentTypes ('204') == []
    }

    void "provides producing content types" () {
        def endpoint = endpoint('/foo') {
            responses ('200') {
                content ('text/plain') {}
                content ('application/json') {}
            }
            responses ('401') {
                content ('text/plain') {}
            }
        }

        expect:
        endpoint.getProducesContentTypes ('200') == ['text/plain', 'application/json'].sort ()
    }

}

