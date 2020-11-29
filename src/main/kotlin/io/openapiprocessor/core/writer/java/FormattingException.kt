package io.openapiprocessor.core.writer.java

class FormattingException(
    private val source: String,
    cause: Throwable
): java.lang.RuntimeException(cause) {

    override val message: String
        get() = "failed to format the generated source: \n>>\n$source\n<<"

}
