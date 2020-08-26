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

package com.github.hauner.openapi.core.framework

import io.openapiprocessor.core.model.RequestBody
import io.openapiprocessor.core.model.parameters.AdditionalParameter
import io.openapiprocessor.core.model.parameters.CookieParameter
import io.openapiprocessor.core.model.parameters.HeaderParameter
import io.openapiprocessor.core.model.parameters.MultipartParameter
import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.core.model.parameters.PathParameter
import io.openapiprocessor.core.model.parameters.QueryParameter
import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.parser.Parameter as ParserParameter

/**
 * default implementation of {@link Framework}.
 *
 * extend and override where necessary.
 *
 * @author Martin Hauner
 */
class FrameworkBase implements Framework {

    @Override
        Parameter createQueryParameter (ParserParameter parameter, DataType dataType) {
        new QueryParameter (parameter.name, dataType, parameter.required, parameter.deprecated)
    }

    @Override
    Parameter createHeaderParameter (ParserParameter parameter, DataType dataType) {
        new HeaderParameter (parameter.name, dataType, parameter.required, parameter.deprecated)
    }

    @Override
    Parameter createCookieParameter (ParserParameter parameter, DataType dataType) {
        new CookieParameter (parameter.name, dataType, parameter.required, parameter.deprecated)
    }

    @Override
    Parameter createPathParameter (ParserParameter parameter, DataType dataType) {
        new PathParameter (parameter.name, dataType, parameter.required, parameter.deprecated)
    }

    @Override
    Parameter createMultipartParameter (ParserParameter parameter, DataType dataType) {
        new MultipartParameter (parameter.name, dataType, parameter.required, parameter.deprecated)
    }

    @Override
    Parameter createAdditionalParameter (ParserParameter parameter, DataType dataType) {
        new AdditionalParameter (parameter.name, dataType, parameter.required, false)
    }

    @Override
    RequestBody createRequestBody (String contentType, boolean required, DataType dataType) {
        new RequestBody ('body', contentType, dataType, required, false)
    }

}
