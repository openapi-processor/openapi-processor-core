/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

import io.openapiprocessor.core.test.TestProcessor
import io.openapiprocessor.test.FileSupport
import spock.lang.Specification
import spock.lang.TempDir

import java.nio.file.Path

class RemoteEndToEndSpec extends Specification {
    static CORE_REPO_URL = "https://raw.githubusercontent.com/openapi-processor/openapi-processor-core/master"
    static PKG = "generated"
    static def MAPPING = """
openapi-processor-mapping: v2

options:
  package-name: $PKG
"""

    @TempDir
    public File folder

    void "processes remote openapi with \$ref's"() {
        def files = new FileSupport(getClass ())
        def processor = new TestProcessor()
        def source = "ref-into-another-file"

        when:
        processor.run ([
            apiPath: "$CORE_REPO_URL/src/testInt/resources/tests/$source/inputs/openapi.yaml",
            targetDir: folder.canonicalPath,
            parser: parser,
            mapping: MAPPING
        ])

        then:
        def packageName = PKG
        def sourcePath = "/tests/${source}"
        def expectedPath = "${sourcePath}/${packageName}"
        def generatedPath = Path.of (folder.absolutePath).resolve (packageName)

        def expectedFiles = files.collectRelativeOutputPaths (sourcePath, packageName).sort ()
        def generatedFiles = files.collectPaths (generatedPath).sort ()

        assert expectedFiles == generatedFiles

        def success = true
        expectedFiles.each {
            def expected = "${expectedPath}/$it"
            def generated = generatedPath.resolve (it)

            success &= !files.printUnifiedDiff (expected, generated)
        }

        assert success

        where:
        parser << ['OPENAPI4J', 'SWAGGER']
    }

}
