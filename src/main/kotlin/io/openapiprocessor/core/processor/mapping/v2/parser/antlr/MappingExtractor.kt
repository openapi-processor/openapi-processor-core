package io.openapiprocessor.core.processor.mapping.v2.parser.antlr

import io.openapiprocessor.core.processor.mapping.v2.parser.Mapping


class MappingExtractor: MappingBaseListener(), Mapping {
    private var kind: Mapping.Kind? = null
    private var sourceType: String? = null
    private var sourceFormat: String? = null
    private var annotationType: String? = null
    private var annotationParameters = LinkedHashMap<String, String>()
    private var targetType: String? = null
    private var targetGenericTypes: MutableList<String> = mutableListOf()

    override fun getKind(): Mapping.Kind? {
        return kind
    }

    override fun getSourceType(): String? {
        return sourceType
    }

    override fun getSourceFormat(): String? {
        return sourceFormat
    }

    override fun getTargetType(): String? {
        return targetType
    }

    override fun getTargetGenericTypes(): List<String> {
        return targetGenericTypes
    }

    override fun getAnnotationType(): String? {
        return annotationType
    }

    override fun getAnnotationParameters(): LinkedHashMap<String, String> {
        return annotationParameters
    }

    override fun enterType(ctx: MappingParser.TypeContext) {
        kind = Mapping.Kind.TYPE
    }

    override fun enterMap(ctx: MappingParser.MapContext) {
        kind = Mapping.Kind.MAP
    }

    override fun enterContent(ctx: MappingParser.ContentContext?) {
        kind = Mapping.Kind.MAP
    }

    override fun enterAnnotate(ctx: MappingParser.AnnotateContext?) {
        kind = Mapping.Kind.ANNOTATE
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
