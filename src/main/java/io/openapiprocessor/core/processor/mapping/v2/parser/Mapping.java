/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2.parser;

import java.util.List;
import java.util.Map;

public interface Mapping {
    enum Kind {
        TYPE, MAP, ANNOTATE
    }

    Mapping.Kind getKind();
    String getSourceType();
    String getSourceFormat();
    String getTargetType();
    List<String> getTargetGenericTypes();
    String getAnnotationType();
    Map<String, String> getAnnotationParameters();
}
