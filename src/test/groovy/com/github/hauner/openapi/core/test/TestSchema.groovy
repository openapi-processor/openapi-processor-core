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

package com.github.hauner.openapi.core.test

import io.openapiprocessor.core.parser.Schema

/**
 * simple Schema implementation for testing
 */
class TestSchema implements Schema {
    String type
    String format

    String ref

    @Override
    List<Schema> getItems () {
        return null
    }

    @Override
    String itemsOf () {
        return null
    }

    @Override
    List<?> getEnum () {
        return enumValues
    }

    def defaultValue
    boolean deprecated = false
    boolean nullable = false
    Integer minLength
    Integer maxLength
    Integer minItems
    Integer maxItems
    BigDecimal maximum
    boolean exclusiveMaximum = false
    BigDecimal minimum
    boolean exclusiveMinimum = false
    String pattern

    Schema item
    Map<String, Schema> properties
    List<?> enumValues = []

    String description

    def getDefault() {
        defaultValue
    }

    @Override
    List<String> getRequired () {
        []
    }

}
