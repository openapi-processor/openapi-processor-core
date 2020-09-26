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

package io.openapiprocessor.core.framework

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
 * default implementation of [io.openapiprocessor.core.framework.Framework].
 *
 * extend and override where necessary.
 *
 * @author Martin Hauner
 */
open class FrameworkBase: Framework {

    override fun createQueryParameter(parameter: ParserParameter, dataType: DataType): Parameter {
        return QueryParameter(parameter.getName(), dataType,
            parameter.isRequired(), parameter.isDeprecated())
    }

    override fun createHeaderParameter(parameter: ParserParameter, dataType: DataType): Parameter {
        return HeaderParameter(parameter.getName(), dataType,
            parameter.isRequired(), parameter.isDeprecated())
    }

    override fun createCookieParameter(parameter: ParserParameter, dataType: DataType): Parameter {
        return CookieParameter(parameter.getName(), dataType,
            parameter.isRequired(), parameter.isDeprecated())
    }

    override fun createPathParameter(parameter: ParserParameter, dataType: DataType): Parameter {
        return PathParameter(parameter.getName(), dataType,
            parameter.isRequired(), parameter.isDeprecated())
    }

    override fun createMultipartParameter(parameter: ParserParameter, dataType: DataType): Parameter {
        return MultipartParameter (parameter.getName(), dataType,
            parameter.isRequired(), parameter.isDeprecated())
    }

    override fun createAdditionalParameter(parameter: ParserParameter, dataType: DataType): Parameter {
        return AdditionalParameter(parameter.getName(), dataType,
            parameter.isRequired(), false)
    }

    override fun createRequestBody(contentType: String, required: Boolean, dataType: DataType): RequestBody {
        return RequestBody("body", contentType, dataType, required, false)
    }

}
