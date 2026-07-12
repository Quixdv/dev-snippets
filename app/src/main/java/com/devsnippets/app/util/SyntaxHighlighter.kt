package com.devsnippets.app.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.devsnippets.app.domain.model.SnippetLanguage
import com.devsnippets.app.ui.theme.SyntaxColors

/**
 * A small, dependency-free regex based syntax highlighter.
 * It is not a full parser, but covers the common cases (keywords, strings,
 * comments, numbers) well enough for a snippet manager's preview needs.
 */
object SyntaxHighlighter {

    private val keywordsByLanguage: Map<SnippetLanguage, Set<String>> = mapOf(
        SnippetLanguage.KOTLIN to setOf(
            "fun", "val", "var", "class", "object", "interface", "if", "else", "when", "for", "while",
            "return", "import", "package", "private", "public", "protected", "internal", "override",
            "suspend", "companion", "data", "sealed", "enum", "is", "in", "as", "null", "true", "false",
            "try", "catch", "finally", "throw", "this", "super", "init", "constructor", "typealias"
        ),
        SnippetLanguage.ANDROID to setOf(
            "fun", "val", "var", "class", "object", "interface", "if", "else", "when", "for", "while",
            "return", "import", "package", "private", "public", "override", "suspend", "companion"
        ),
        SnippetLanguage.PYTHON to setOf(
            "def", "class", "if", "elif", "else", "for", "while", "return", "import", "from", "as",
            "try", "except", "finally", "with", "lambda", "None", "True", "False", "in", "is", "not",
            "and", "or", "yield", "pass", "break", "continue", "self", "async", "await"
        ),
        SnippetLanguage.JAVASCRIPT to setOf(
            "function", "const", "let", "var", "if", "else", "for", "while", "return", "import", "export",
            "class", "extends", "new", "this", "try", "catch", "finally", "throw", "async", "await",
            "typeof", "null", "undefined", "true", "false", "default", "from"
        ),
        SnippetLanguage.SQL to setOf(
            "SELECT", "FROM", "WHERE", "INSERT", "INTO", "VALUES", "UPDATE", "SET", "DELETE", "CREATE",
            "TABLE", "ALTER", "DROP", "JOIN", "LEFT", "RIGHT", "INNER", "OUTER", "ON", "GROUP", "BY",
            "ORDER", "HAVING", "AND", "OR", "NOT", "NULL", "AS", "DISTINCT", "LIMIT", "PRIMARY", "KEY",
            "select", "from", "where", "insert", "into", "values", "update", "set", "delete", "create",
            "table", "alter", "drop", "join", "left", "right", "inner", "outer", "on", "group", "by",
            "order", "having", "and", "or", "not", "null", "as", "distinct", "limit", "primary", "key"
        ),
        SnippetLanguage.SHELL to setOf(
            "if", "then", "else", "fi", "for", "do", "done", "while", "case", "esac", "function",
            "echo", "export", "return", "exit", "in"
        ),
        SnippetLanguage.HTML_CSS to setOf(
            "div", "span", "html", "head", "body", "class", "id", "style", "script", "href", "src"
        ),
        SnippetLanguage.CUSTOM to emptySet()
    )

    private val commentPrefixes = mapOf(
        SnippetLanguage.KOTLIN to "//",
        SnippetLanguage.ANDROID to "//",
        SnippetLanguage.JAVASCRIPT to "//",
        SnippetLanguage.PYTHON to "#",
        SnippetLanguage.SHELL to "#",
        SnippetLanguage.SQL to "--",
        SnippetLanguage.HTML_CSS to "<!--",
        SnippetLanguage.CUSTOM to "//"
    )

    private val numberRegex = Regex("\\b\\d+(\\.\\d+)?\\b")
    private val stringRegex = Regex("(\"[^\"]*\"|'[^']*')")

    fun highlight(code: String, language: SnippetLanguage): AnnotatedString {
        val keywords = keywordsByLanguage[language].orEmpty()
        val commentPrefix = commentPrefixes[language] ?: "//"

        return buildAnnotatedString {
            val lines = code.split("\n")
            lines.forEachIndexed { index, line ->
                appendHighlightedLine(line, keywords, commentPrefix)
                if (index != lines.lastIndex) append("\n")
            }
        }
    }

    private fun androidx.compose.ui.text.AnnotatedString.Builder.appendHighlightedLine(
        line: String,
        keywords: Set<String>,
        commentPrefix: String
    ) {
        val commentIndex = line.indexOf(commentPrefix)
        val codePart = if (commentIndex >= 0) line.substring(0, commentIndex) else line
        val commentPart = if (commentIndex >= 0) line.substring(commentIndex) else null

        appendTokenizedCode(codePart, keywords)

        if (commentPart != null) {
            withStyle(SpanStyle(color = SyntaxColors.Comment)) {
                append(commentPart)
            }
        }
    }

    private fun androidx.compose.ui.text.AnnotatedString.Builder.appendTokenizedCode(
        code: String,
        keywords: Set<String>
    ) {
        // Find all string literal ranges first so we don't double-highlight inside them.
        val stringMatches = stringRegex.findAll(code).toList()
        var cursor = 0

        for (match in stringMatches) {
            if (match.range.first > cursor) {
                appendWordsWithKeywordsAndNumbers(code.substring(cursor, match.range.first), keywords)
            }
            withStyle(SpanStyle(color = SyntaxColors.StringLiteral)) {
                append(match.value)
            }
            cursor = match.range.last + 1
        }
        if (cursor < code.length) {
            appendWordsWithKeywordsAndNumbers(code.substring(cursor), keywords)
        }
    }

    private fun androidx.compose.ui.text.AnnotatedString.Builder.appendWordsWithKeywordsAndNumbers(
        text: String,
        keywords: Set<String>
    ) {
        val wordRegex = Regex("[A-Za-z_][A-Za-z0-9_]*|\\d+(\\.\\d+)?|.")
        for (match in wordRegex.findAll(text)) {
            val token = match.value
            when {
                token in keywords -> withStyle(SpanStyle(color = SyntaxColors.Keyword)) { append(token) }
                numberRegex.matches(token) -> withStyle(SpanStyle(color = SyntaxColors.Number)) { append(token) }
                token.startsWith("@") -> withStyle(SpanStyle(color = SyntaxColors.Annotation)) { append(token) }
                else -> withStyle(SpanStyle(color = SyntaxColors.Default)) { append(token) }
            }
        }
    }
}
