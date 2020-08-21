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

import spock.lang.IgnoreIf
import spock.lang.Requires
import spock.lang.Specification
import spock.lang.Unroll

import static io.openapiprocessor.core.misc.URLKt.toURL

class PathSpec extends Specification {

    @Unroll
    @Requires({ os.windows })
    void "convert source to url on windows" () {
        def resultUrl = toURL (source)

        expect:
        resultUrl.toString () == url

        where:
        source                                                     | url
        "file:////C:/somewhere/openapi-processor-samples/samples"  | "file:////C:/somewhere/openapi-processor-samples/samples"
        "C:\\somewhere\\openapi-processor-samples\\samples"        | "file:/C:/somewhere/openapi-processor-samples/samples"
    }

    @Unroll
    @IgnoreIf({ os.windows })
    void "convert source to url on unix (#source)" () {
        def resultUrl = toURL (source)

        expect:
        resultUrl.toString () == url

        where:
        source                                                 | url
        "file:///somewhere/openapi-processor-samples/samples"  | "file:/somewhere/openapi-processor-samples/samples"
        "https:///somewhere/openapi-processor-samples/samples" | "https:/somewhere/openapi-processor-samples/samples"
        "/somewhere/openapi-processor-samples/samples"         | "file:/somewhere/openapi-processor-samples/samples"
    }

}
