/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.framework

import io.openapiprocessor.core.model.RequestBody
import io.openapiprocessor.core.model.datatypes.AnnotationDataType
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
 */
open class FrameworkBase: Framework {

    override fun createQueryParameter(parameter: ParserParameter, dataType: DataType): Parameter {
        return QueryParameter(
            parameter.getName(),
            dataType,
            parameter.isRequired(),
            parameter.isDeprecated(),
            parameter.description
        )
    }

    override fun createHeaderParameter(parameter: ParserParameter, dataType: DataType): Parameter {
        return HeaderParameter(
            parameter.getName(),
            dataType,
            parameter.isRequired(),
            parameter.isDeprecated(),
            parameter.description)
    }

    override fun createCookieParameter(parameter: ParserParameter, dataType: DataType): Parameter {
        return CookieParameter(
            parameter.getName(),
            dataType,
            parameter.isRequired(),
            parameter.isDeprecated(),
            parameter.description)
    }

    override fun createPathParameter(parameter: ParserParameter, dataType: DataType): Parameter {
        return PathParameter(
            parameter.getName(),
            dataType,
            parameter.isRequired(),
            parameter.isDeprecated(),
            parameter.description)
    }

    override fun createMultipartParameter(parameter: ParserParameter, dataType: DataType): Parameter {
        return MultipartParameter (parameter.getName(), dataType,
            parameter.isRequired(), parameter.isDeprecated())
    }

    override fun createAdditionalParameter(parameter: ParserParameter, dataType: DataType,
       annotationDataType: AnnotationDataType?): Parameter {

        return AdditionalParameter(parameter.getName(), dataType,
            annotationDataType, parameter.isRequired(), false)
    }

    override fun createRequestBody(contentType: String, required: Boolean, dataType: DataType): RequestBody {
        return RequestBody("body", contentType, dataType, required, false)
    }

}
