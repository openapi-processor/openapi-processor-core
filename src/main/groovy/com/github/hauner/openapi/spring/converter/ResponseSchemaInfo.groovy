/*
 * Copyright 2019 the original authors
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

package com.github.hauner.openapi.spring.converter

import io.swagger.v3.oas.models.media.Schema

/**
 * Helper for {@link DataTypeConverter}. A {@link SchemaInfo} of an endpoint response.
 *
 * @author Martin Hauner
 */
class ResponseSchemaInfo extends SchemaInfo {

    /**
     * Endpoint path.
     */
    String path

    /**
     * Response content type.
     */
    String contentType

    ResponseSchemaInfo (String path, String contentType, Schema schema, String name) {
        super (schema, name)
        this.path = path
        this.contentType = contentType
    }

}
