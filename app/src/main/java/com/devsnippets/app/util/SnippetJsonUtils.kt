package com.devsnippets.app.util

import com.devsnippets.app.domain.model.Snippet
import com.devsnippets.app.domain.model.SnippetLanguage
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/** Serializable DTO used purely for JSON export/import (backup & restore). */
@Serializable
data class SnippetDto(
    val id: Long = 0L,
    val title: String,
    val code: String,
    val language: String,
    val tags: List<String> = emptyList(),
    val notes: String = "",
    val isFavorite: Boolean = false,
    val isPinned: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
)

@Serializable
data class SnippetBackup(
    val exportedAt: Long,
    val appVersion: String = "1.0.0",
    val snippets: List<SnippetDto>
)

/** Handles converting the app's domain snippets to/from a portable JSON backup format. */
object SnippetJsonUtils {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun export(snippets: List<Snippet>): String {
        val backup = SnippetBackup(
            exportedAt = System.currentTimeMillis(),
            snippets = snippets.map {
                SnippetDto(
                    id = it.id,
                    title = it.title,
                    code = it.code,
                    language = it.language.displayName,
                    tags = it.tags,
                    notes = it.notes,
                    isFavorite = it.isFavorite,
                    isPinned = it.isPinned,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                )
            }
        )
        return json.encodeToString(backup)
    }

    /** Parses JSON text into domain snippets. Throws on malformed input. */
    fun import(jsonText: String): List<Snippet> {
        val backup = json.decodeFromString(SnippetBackup.serializer(), jsonText)
        return backup.snippets.map {
            Snippet(
                id = 0L, // Always insert as new rows to avoid id collisions.
                title = it.title,
                code = it.code,
                language = SnippetLanguage.fromDisplayName(it.language),
                tags = it.tags,
                notes = it.notes,
                isFavorite = it.isFavorite,
                isPinned = it.isPinned,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt
            )
        }
    }
}
