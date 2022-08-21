/*
 * Copyright © 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2.parser.antlr

/**
 * extract ToData from parser
 */
class ToExtractor: ToBaseListener() {

    lateinit var type: String
    var typeArguments = mutableListOf<String>()
    var annotationType: String? = null
    var annotationArguments: String? = null

    fun getTarget(): ToData {
        return ToData(type, typeArguments, annotationType, annotationArguments)
    }

    override fun enterAnnotationType(ctx: ToParser.AnnotationTypeContext) {
        // type string with package
        annotationType = ctx.type ().text

        // all arguments as a single string
        annotationArguments = ctx.AnnotationAnyArguments ()?.text
    }

    override fun enterToType(ctx: ToParser.ToTypeContext) {
        // type string "{pkg.}Type"
        type = ctx.type().text

        // type strings of <> type arguments
        ctx.typeArguments ()
            ?.typeArgumentList ()
            ?.typeArgument ()
            ?.forEach {
                if (it.text == "?") {
                    typeArguments.add("?")
                } else if (it.type() != null) {
                    typeArguments.add(it.type ().text)
                }
            }
    }

}
