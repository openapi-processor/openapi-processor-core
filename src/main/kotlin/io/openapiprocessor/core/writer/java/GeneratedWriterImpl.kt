/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import java.io.StringWriter
import java.io.Writer

class GeneratedWriterImpl(private val info: GeneratedInfo, private val options: ApiOptions)
    : GeneratedWriter {

    override fun getImport():String {
        return "${options.packageName}.support.Generated"
    }

    override fun writeUse(target: Writer) {
        target.write(create())
    }

    override fun writeSource(target: Writer) {
        target.write("""
            package ${options.packageName}.support;

            import java.lang.annotation.*;
            import static java.lang.annotation.ElementType.*;
            import static java.lang.annotation.RetentionPolicy.*;

            @Documented
            @Retention(CLASS)
            @Target({TYPE, METHOD})
            ${create()}
            public @interface Generated {
                /**
                 * The name of the source code generator, i.e. openapi-processor-*.
                 *
                 * @return name of the generator
                 */
                String value();

                /**
                 * @return version of the generator
                 */
                String version();

                /**
                 * The date & time of generation (ISO 8601).
                 *                 
                 * @return date of generation
                 */
                String date() default null;
                                
                /**
                 * The url of the generator.
                 *
                 * @return url of generator
                 */
                String url() default null;
            }
            """.trimIndent())
    }

    private fun create(): String {
        val writer = StringWriter()
        writer.write("@Generated")
        writer.write("(")
        writer.write("""value = "${info.generator}"""")
        writer.write(", ")
        writer.write("""version = "${info.version}"""")
        if (info.date != null) {
            writer.write(", ")
            writer.write("""date = "${info.date}"""")
        }
        if (info.url != null) {
            writer.write(", ")
            writer.write("""url = "${info.url}"""")
        }
        writer.write(")")
        return writer.toString()
    }
}
