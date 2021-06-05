/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.openapiprocessor.core.processor.mapping.v2.ResultStyle

class ResultStyleOptionMapping(resultStyle: ResultStyle):
    OptionMapping<ResultStyle>("resultStyle", resultStyle)
