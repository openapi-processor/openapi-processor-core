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

package io.openapiprocessor.core.model

import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.datatypes.ObjectDataType
import io.openapiprocessor.core.model.datatypes.StringEnumDataType
import java.util.function.Consumer

/**
 * Root of the internal model used to generate the api.
 *
 * @author Martin Hauner
 */
class Api(
    private /*val*/ var interfaces: List<Interface> = emptyList(),

    /**
     * named data types (i.e. $ref) used in the OpenAPI description.
     */
    private val models: DataTypes = DataTypes()  // todo rename to dataTypes
) {

    @Deprecated("only used from test")
    fun setModels(dataTypes: List<DataType>) {
        models.add(dataTypes)
    }

    fun getInterface(name: String): Interface {
        return interfaces.find { it.name.toLowerCase () == name.toLowerCase () }!!
    }

    fun forEachInterface(action: Consumer<Interface>) {
        interfaces.forEach(action)
    }

    fun forEachObjectDataType(action: Consumer<ObjectDataType>) {
        models.getObjectDataTypes().forEach(action)
    }

    fun forEachEnumDataType(action: Consumer<StringEnumDataType>) {
        models.getEnumDataTypes().forEach(action)
    }

}
