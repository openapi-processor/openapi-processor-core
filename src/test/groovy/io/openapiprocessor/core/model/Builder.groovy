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
import com.github.hauner.openapi.core.model.RequestBody
import com.github.hauner.openapi.core.model.Response
import com.github.hauner.openapi.core.model.datatypes.AnnotationDataType
import com.github.hauner.openapi.core.model.datatypes.DataType
import com.github.hauner.openapi.core.model.datatypes.NoneDataType
import com.github.hauner.openapi.core.model.datatypes.ObjectDataType
import com.github.hauner.openapi.core.model.parameters.AdditionalParameter
import com.github.hauner.openapi.core.model.parameters.MultipartParameter
import com.github.hauner.openapi.core.model.parameters.Parameter
import com.github.hauner.openapi.core.model.parameters.QueryParameter

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

    private parameters = new ArrayList<Parameter>()
    private bodies = new ArrayList<RequestBody>()
    private responses = new LinkedHashMap<String, List<Response>>()

    void get () {
        method = HttpMethod.GET
    }

    void deprecated () {
        deprecated = true
    }

    void bodies (@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = ResponsesBuilder) Closure init) {
        def builder = new BodiesBuilder()
        def code = init.rehydrate (builder, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
        bodies.addAll (builder.buildBodies ())
        parameters.addAll (builder.buildParameters ())
    }

    void parameters (@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = ParametersBuilder) Closure init) {
        def builder = new ParametersBuilder()
        def code = init.rehydrate (builder, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
        def params = builder.build ()
        parameters.addAll (params)
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
        ep.requestBodies.addAll (bodies)
        ep.parameters.addAll (parameters)
        responses.each { status, values ->
            ep.addResponses (status, values)
        }
        ep.initEndpointResponses ()
        ep
    }

}


class BodiesBuilder {
    private List<RequestBody> bodies = []
    private List<Parameter> parameters = []

    void content (String contentType, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = BodyBuilder) Closure init) {
        if (contentType == 'multipart/form-data') {
            def builder = new MultipartBuilder()
            def code = init.rehydrate (builder, this, this)
            code.resolveStrategy = Closure.DELEGATE_ONLY
            code()
            def params = builder.build ()
            parameters.addAll (params)
        } else {
            def builder = new BodyBuilder(content: contentType)
            def code = init.rehydrate (builder, this, this)
            code.resolveStrategy = Closure.DELEGATE_ONLY
            code()
            def body = builder.build ()
            bodies.add (body)
        }
    }

    List<RequestBody> buildBodies () {
        bodies
    }

    List<Parameter> buildParameters () {
        parameters
    }

}

class MultipartBuilder {
    private ObjectDataType data

    void data (ObjectDataType dataType) {
        data = dataType
    }

    List<Parameter> build () {
        data.properties.collect {
            new MultipartParameter (name: it.key, dataType: it.value)
        }
    }

}

class BodyBuilder {
    private String content
    private DataType data

    void data (DataType dataType) {
        data = dataType
    }

    RequestBody build () {
        def body = new RequestBody()
        body.contentType = content
        body.dataType = data
        body
    }

}


class ParametersBuilder {
    private List<Parameter> parameters = []

    void query (@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = QueryParameterBuilder) Closure init) {
        def builder = new QueryParameterBuilder()
        def code = init.rehydrate (builder, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
        def parameter = builder.build ()
        parameters.add (parameter)
    }

    void add (@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = QueryParameterBuilder) Closure init) {
        def builder = new AddParameterBuilder()
        def code = init.rehydrate (builder, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
        def parameter = builder.build ()
        parameters.add (parameter)
    }

    List<Parameter> build () {
        parameters
    }

}

class QueryParameterBuilder {
    private String name
    private DataType type

    void name (String name) {
        this.name = name
    }

    void type (DataType dataType) {
        this.type = dataType
    }

    QueryParameter build () {
        new QueryParameter(name: name, dataType: dataType)
    }

}

class AddParameterBuilder {
    private String name
    private DataType type
    private AnnotationDataType annotationType

    void name (String name) {
        this.name = name
    }

    void type (DataType dataType) {
        this.type = dataType
    }

    void annotation (AnnotationDataType dataType) {
        annotationType = dataType
    }

    AdditionalParameter build () {
        new AdditionalParameter (name: name, dataType: type, annotationDataType: annotationType)
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
        responses.add (rsp)
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
