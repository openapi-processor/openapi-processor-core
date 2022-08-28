/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping


class ParameterAnnotationTypeMapping(val annotationTypeMapping: AnnotationTypeMapping)
    : AnnotationMapping by annotationTypeMapping, Mapping by annotationTypeMapping
