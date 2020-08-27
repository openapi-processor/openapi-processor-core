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

package com.github.hauner.openapi.core.model.datatypes

import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.datatypes.DataTypeConstraints

/**
 * temporary helper to migrate to kotlin, implements interface default methods
 *
 * @author Martin Hauner
 */
@Deprecated
abstract class DataTypeBase implements DataType {

    boolean deprecated = false

    @Override
    boolean isDeprecated () {
        deprecated
    }

    @Override
    boolean isComposed () {
        false
    }

    @Override
    DataTypeConstraints getConstraints () {
        return null
    }

}
