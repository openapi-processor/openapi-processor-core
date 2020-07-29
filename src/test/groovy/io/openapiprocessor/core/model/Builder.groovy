/*
 * Copyright 2020 the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.openapiprocessor.core.model

import com.github.hauner.openapi.core.model.Endpoint
import com.github.hauner.openapi.core.model.HttpMethod
import com.github.hauner.openapi.core.model.Interface
import com.github.hauner.openapi.core.model.Response
import com.github.hauner.openapi.core.model.datatypes.DataType
import com.github.hauner.openapi.core.model.datatypes.NoneDataType

class Builder {

    static Interface intrface(String name, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = InterfaceBuilder) Closure init) {
        def builder = new InterfaceBuilder(name: name)
        def code = init.rehydrate (builder, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
        builder.build ()
    }

    static Endpoint endpoint(String path, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = EndpointBuilder) Closure init) {
        def builder = new EndpointBuilder(path: path)
        def code = init.rehydrate (builder, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
        builder.build ()
    }

}


class InterfaceBuilder {
    String name
    List<Endpoint> endpoints = []

    void endpoint(String path, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = EndpointBuilder) Closure init) {
        endpoints.add (Builder.endpoint(path, init))
    }

    Interface build() {
        def itf = new Interface(name: name)
        itf.endpoints = endpoints
        return itf
    }

}

class EndpointBuilder {
    private String path
    private HttpMethod method = HttpMethod.GET
    private boolean deprecated = false
    private responses = new LinkedHashMap<String, List<Response>>()

    void get () {
        method = HttpMethod.GET
    }

    void deprecated () {
        deprecated = true
    }

    void responses (String httpStatus, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = ResponsesBuilder) Closure init) {
        def builder = new ResponsesBuilder()
        def code = init.rehydrate (builder, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
        def rsps = builder.build ()
        responses.put (httpStatus, rsps)
    }

    Endpoint build () {
        def ep = new Endpoint(path: path)
        ep.method = method
        ep.deprecated = deprecated
        responses.each { status, values ->
            ep.addResponses (status, values)
        }
        ep.initEndpointResponses ()
        ep
    }

}

class ResponsesBuilder {
    private List<Response> responses = []

    void content (String contentType, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = ResponseBuilder) Closure init) {
        def builder = new ResponseBuilder(content: contentType)
        def code = init.rehydrate (builder, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
        def rsp = builder.build ()
        responses.put (rsp)
    }

    void empty() {
        def rp = new Response()
        rp.contentType = null
        rp.responseType = new NoneDataType()
        responses.add (rp)
    }

    List<Response> build () {
        responses
    }

}

class ResponseBuilder {
    private String content
    private DataType response

    void response (DataType dataType) {
        response = dataType
    }

    Response build () {
        def rp = new Response()
        rp.contentType = content
        rp.responseType = response
        rp
    }

}
