/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor.mapping.v2.parser.antlr

import io.openapiprocessor.core.processor.mapping.v2.parser.Mapping


class MappingExtractor: MappingBaseListener(), Mapping {
    override var kind: Mapping.Kind? = null
    override var sourceType: String? = null
    override var sourceFormat: String? = null
    override var annotationType: String? = null
    override var annotationParameters = LinkedHashMap<String, String>()
    override var targetType: String? = null
    override var targetGenericTypes: MutableList<String> = mutableListOf()

    override fun enterType(ctx: MappingParser.TypeContext) {
        kind = Mapping.Kind.TYPE
    }

    override fun enterMap(ctx: MappingParser.MapContext) {
        kind = Mapping.Kind.MAP
    }

    override fun enterContent(ctx: MappingParser.ContentContext) {
        kind = Mapping.Kind.MAP
    }

    override fun enterAnnotate(ctx: MappingParser.AnnotateContext) {
        kind = Mapping.Kind.ANNOTATE
    }

    override fun enterPlainType(ctx: MappingParser.PlainTypeContext) {
        targetType = ctx.text
    }

    override fun enterSourceIdentifier(ctx: MappingParser.SourceIdentifierContext) {
        sourceType = ctx.text
    }

    override fun enterFormatIdentifier(ctx: MappingParser.FormatIdentifierContext) {
        sourceFormat = ctx.text
    }

    override fun enterQualifiedTargetType(ctx: MappingParser.QualifiedTargetTypeContext) {
        targetType = ctx.start.text
    }

    override fun enterContentType(ctx: MappingParser.ContentTypeContext) {
        sourceType = ctx.start.text
    }

    override fun enterGenericParameter(ctx: MappingParser.GenericParameterContext) {
        targetGenericTypes.add(ctx.text)
    }

    override fun enterAnnotationType(ctx: MappingParser.AnnotationTypeContext) {
        annotationType = ctx.start.text
    }

    override fun enterAnnotationParameterUnnamed(ctx: MappingParser.AnnotationParameterUnnamedContext) {
        annotationParameters.put("", ctx.text)
    }

    override fun enterAnnotationParameterNamed(ctx: MappingParser.AnnotationParameterNamedContext) {
        annotationParameters.put(ctx.getChild(0).text, ctx.getChild(2).text)
    }
}
