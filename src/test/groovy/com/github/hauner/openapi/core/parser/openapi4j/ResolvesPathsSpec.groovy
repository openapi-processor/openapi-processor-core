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

package com.github.hauner.openapi.core.parser.openapi4j

import com.github.hauner.openapi.core.model.HttpMethod
import com.github.hauner.openapi.core.test.parser.OpenApi4jParser
import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class ResolvesPathsSpec extends Specification {

    def fs = Jimfs.newFileSystem (Configuration.unix ())

    void "resolves ref path" () {

        def main = """\
        openapi: 3.0.2
        info:
          title: API
          version: 1.0.0
        
        paths:
          /foo:
            \$ref: ref.yaml
"""
        def ref = """\
get:
  responses:
    '200':
      description: none
      content:
        application/json:
          schema:
            type: string
"""

        Path root = Files.createDirectory (fs.getPath ("source"))
        def mainYaml = root.resolve ('openapi.yaml')
        def refYaml = root.resolve ('ref.yaml')
        copy (main, mainYaml)
        copy (ref, refYaml)

        when:
        def parser = new OpenApi4jParser ()
        def api = parser.parse (mainYaml.toUri ().toString ())

        then:
        def foo = api.paths.get ('/foo')
        foo.operations[0].method == HttpMethod.GET
    }

    /**
     * write source to file system
     */
    private static void copy (String source, Path target) {
        Files.createDirectories (target.parent)
        target.text = source
    }


}
