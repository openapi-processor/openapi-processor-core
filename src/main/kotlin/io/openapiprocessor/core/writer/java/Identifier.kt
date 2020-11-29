/*
 * Copyright 2019-2020 the original authors
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

@file:JvmName("Identifier")

package io.openapiprocessor.core.writer.java

import java.lang.Character.isJavaIdentifierPart
import java.lang.Character.isJavaIdentifierStart

/**
 * converts a source string to a valid (camel case) java identifier. One way, ie it is not
 * reversible.
 *
 * conversion rules:
 * create camel case from word breaks. A word break is any invalid character (i.e. it is not
 * allowed in a java identifier), an underscore or an upper case letter. Invalid characters
 * are dropped.
 *
 * All words are converted to lowercase and are capitalized and joined except the first word
 * that is no capitalized.
 *
 * @param src the source "string"
 * @return a valid camel case java identifier
 *
 * @author Martin Hauner
 */
fun toCamelCase(src: String): String {
    return joinCamelCase(splitAtWordBreaks(src))
}

/**
 * converts a source string to a valid (camel case) java *class* identifier. One way, ie it is
 * not reversible.
 *
 * conversion rules:
 * create camel case from word breaks. A word break is any invalid character (i.e. it is not
 * allowed in a java identifier), an underscore or an upper case letter. Invalid characters
 * are dropped.
 *
 * All words are converted to lowercase and are capitalized and joined.
 *
 * @param src the source string
 *
 * @return a valid camel case java class identifier
 *
 * @author Martin Hauner
 */
fun toClass(src: String): String {
    return toCamelCase(src).capitalize()
}

/**
 * converts a source string to a valid (all upper case) java enum identifier. One way, ie it is
 * not reversible.
 *
 * conversion rules:
 * create camel case from word breaks. A word break is any invalid character (i.e. it is not
 * allowed in a java identifier), an underscore or an upper case letter. Invalid characters
 * are dropped.
 *
 * All words are converted to uppercase and joined by an underscore.
 *
 * @param src the source "string"
 *
 * @return a valid upper case enum java identifier
 *
 * @author Martin Hauner
 */
fun toEnum(src: String): String {
    return joinEnum(splitAtWordBreaks(src))
}

/**
 * joins the given words to a single camel case string.
 *
 * The first word is lower case.
 *
 * @param words a list of words
 * @return a came case string
 *
 * @author Martin Hauner
 */
private fun joinCamelCase(words: List<String>): String {
    val sb = StringBuilder()

    words.forEachIndexed { idx, p ->
        if (idx == 0) {
            sb.append(p.toLowerCase())
        } else {
            sb.append(p.toLowerCase().capitalize())
        }
    }

    if (sb.isEmpty()) {
        return "invalid"
    }

    return sb.toString()
}

/**
 * joins the given words to a single uppercase string separated by underscore.
 *
 * @param words a list of words
 * @return an uppercase string
 *
 * @author Martin Hauner
 */
private fun joinEnum(words: List<String>): String {
    val result = words.joinToString("_") { it.toUpperCase() }

    if (result.isEmpty()) {
        return "INVALID"
    }

    return result
}

/**
 * splits the given string at the word breaks.
 *
 * @param src the source "string"
 * @return a list of split words
 *
 * @author Martin Hauner
 */
private fun splitAtWordBreaks(src: String): List<String> {
    val words = ArrayList<String>()
    val current = StringBuilder()

    val trimmed = src.trimInvalidStart()
    trimmed.forEachIndexed { idx, c ->

        if (idx == 0 || !src.isWordBreak(idx)) {
            current.append(c)
            return@forEachIndexed
        }

        if(current.isNotEmpty()) {
            words.add(current)
            current.clear()
        }

        current.appendValid(c)
    }

    if(current.isNotEmpty()) {
        words.add(current)
    }

    return words
}

private fun ArrayList<String>.add(builder: StringBuilder) {
    add(builder.toString())
}

private fun String.trimInvalidStart(): String {
    return this.trimStart {
        !isValidStart(it)
    }
}

private fun StringBuilder.appendValid(c: Char): StringBuilder {
    if (isValid(c)) {
        append(c)
    }
    return this
}

private fun String.isWordBreak(idx: Int): Boolean {
    return this.isForcedBreak(idx)
        || this.isCaseBreak(idx)
}

private fun String.isForcedBreak(idx: Int): Boolean {
    return this[idx].isWordBreak()
}

// detect existing camel case word breaks
private fun String.isCaseBreak(idx: Int): Boolean {
    if (idx == 0)
        return false

    val prev = this[idx - 1]
    val curr = this[idx]

    return prev.isLowerCase()
        && curr.isUpperCase()
}

private fun Char.isNoWordBreak(): Boolean {
    return !isWordBreak()
}

private fun Char.isWordBreak(): Boolean {
    return isWordBreakChar(this)
        || !isJavaIdentifierPart(this)
}

private val INVALID_WORD_BREAKS = listOf(' ', '-')
private val VALID_WORD_BREAKS = listOf('_')


private fun isValid(c: Char): Boolean {
    return isJavaIdentifierPart(c) && !isValidWordBreak(c)
}

private fun isValidStart(c: Char): Boolean {
    return isJavaIdentifierStart(c) && !isValidWordBreak(c)
}

private fun isWordBreakChar(c: Char): Boolean {
    return isInvalidWordBreak(c) || isValidWordBreak(c)
}

private fun isValidWordBreak(c: Char): Boolean {
    return VALID_WORD_BREAKS.contains(c)
}

private fun isInvalidWordBreak(c: Char): Boolean {
    return INVALID_WORD_BREAKS.contains(c)
}
