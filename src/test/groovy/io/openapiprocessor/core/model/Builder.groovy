/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model

import io.openapiprocessor.core.model.datatypes.AnnotationDataType
import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.datatypes.NoneDataType
import io.openapiprocessor.core.model.datatypes.ObjectDataType
import io.openapiprocessor.core.model.parameters.AdditionalParameter
import io.openapiprocessor.core.model.parameters.MultipartParameter
import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.core.model.parameters.QueryParameter

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
        new Interface(name, 'pkg', endpoints)
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
        def ep = new Endpoint(path, method, null, deprecated, null)
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
        data.objectProperties.collect {
            new MultipartParameter (it.key, it.value, false, false)
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
        new RequestBody(
            'body',
            content,
            data ?: new NoneDataType (),
            false,
            false)
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

    void add (@DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = AddParameterBuilder) Closure init) {
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
        new AdditionalParameter (name, type, annotationType, true, false)
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
        responses.add (new EmptyResponse())
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
        new Response(content, response ?: new NoneDataType ())
    }

}
