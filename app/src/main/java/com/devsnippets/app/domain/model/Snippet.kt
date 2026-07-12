package com.devsnippets.app.domain.model

/**
 * Core domain model representing a single code snippet.
 * This is the model used throughout the UI/ViewModel layers, independent
 * from how it is actually persisted (see data.local.entity.SnippetEntity).
 */
data class Snippet(
    val id: Long = 0L,
    val title: String,
    val code: String,
    val language: SnippetLanguage,
    val tags: List<String> = emptyList(),
    val notes: String = "",
    val isFavorite: Boolean = false,
    val isPinned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Supported snippet categories / languages.
 * "Custom" allows the user to store anything that doesn't fit a known category.
 */
enum class SnippetLanguage(val displayName: String) {
    ANDROID("Android"),
    KOTLIN("Kotlin"),
    PYTHON("Python"),
    JAVASCRIPT("JavaScript"),
    SQL("SQL"),
    HTML_CSS("HTML/CSS"),
    SHELL("Shell"),
    CUSTOM("Custom");

    companion object {
        fun fromDisplayName(name: String): SnippetLanguage =
            entries.firstOrNull { it.displayName.equals(name, ignoreCase = true) } ?: CUSTOM
    }
}
