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

package io.openapiprocessor.core.misc

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.NotWindows
import io.openapiprocessor.core.Windows


class PathSpec: StringSpec({

    "convert source to url on windows os".config(tags = setOf(Windows)) {
        forAll(
            row("file:////C:/somewhere/openapi-processor-samples/samples",
                "file:////C:/somewhere/openapi-processor-samples/samples"),
            row("C:\\somewhere\\openapi-processor-samples\\samples",
                "file:/C:/somewhere/openapi-processor-samples/samples")
        ) { source, url ->
            toURL(source).toString() shouldBe url
        }
    }

    "convert source to url on unix-like os".config(tags = setOf(NotWindows)) {
        forAll(
            row("file:///somewhere/openapi-processor-samples/samples",
                "file:/somewhere/openapi-processor-samples/samples"),
            row("https:///somewhere/openapi-processor-samples/samples",
                "https:/somewhere/openapi-processor-samples/samples"),
            row("/somewhere/openapi-processor-samples/samples",
                "file:/somewhere/openapi-processor-samples/samples")
        ) { source, url ->
            toURL(source).toString() shouldBe url
        }
    }

})
