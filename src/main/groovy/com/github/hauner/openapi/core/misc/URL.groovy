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

package com.github.hauner.openapi.core.misc

import java.nio.file.Paths

/**
 * helper to convert a source url/path to an URL.
 *
 * @author Martin Hauner
 */
class URL {

    /**
     * convert source to a valid URL.
     *
     * if the source is an url string it converts it to an URL
     * if the source is not an URL it assumes a local path and prefixes it with file://(//) to
     * create a valid URL.
     *
     * @param source source path or url
     * @return an URL to the given source
     */
    static java.net.URL toURL (String source) {
        try {
            return new java.net.URL (source)
        } catch (Exception ignore) {
            // catch
        }

        try {
            Paths.get (source)
                .normalize ()
                .toUri ()
                .toURL ()
        } catch (Exception e) {
            throw e
        }
    }

}
