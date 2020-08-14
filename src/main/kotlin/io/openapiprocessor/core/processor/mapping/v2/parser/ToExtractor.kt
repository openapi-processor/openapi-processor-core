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

package io.openapiprocessor.core.processor.mapping.v2.parser

/**
 * extract ToData from parser
 *
 * @author Martin Hauner
 */
class ToExtractor(val target: ToData = ToData()): ToBaseListener() {

    override fun enterAnnotationType(ctx: ToParser.AnnotationTypeContext) {
        // type string with package
        target.annotationType = ctx.type ().text
        target.annotationArguments = ctx.AnnotationAnyArguments ()?.text
    }

    override fun enterToType(ctx: ToParser.ToTypeContext) {
        // type string "{pkg.}Type"
        target.type = ctx.type().text

        // type strings of <> type arguments
        val args: MutableList<String> = mutableListOf()
        ctx.typeArguments ()
            ?.typeArgumentList ()
            ?.typeArgument ()
            ?.filter { it.type() != null }
            ?.forEach {
                args.add(it.type ().text)
            }

        target.typeArguments = args
    }

}
