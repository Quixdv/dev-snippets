package com.devsnippets.app.data.mapper

import com.devsnippets.app.data.local.entity.SnippetEntity
import com.devsnippets.app.domain.model.Snippet
import com.devsnippets.app.domain.model.SnippetLanguage

/** Entity -> Domain */
fun SnippetEntity.toDomain(): Snippet = Snippet(
    id = id,
    title = title,
    code = code,
    language = SnippetLanguage.fromDisplayName(language),
    tags = if (tags.isBlank()) emptyList() else tags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
    notes = notes,
    isFavorite = isFavorite,
    isPinned = isPinned,
    createdAt = createdAt,
    updatedAt = updatedAt
)

/** Domain -> Entity */
fun Snippet.toEntity(): SnippetEntity = SnippetEntity(
    id = id,
    title = title,
    code = code,
    language = language.displayName,
    tags = tags.joinToString(","),
    notes = notes,
    isFavorite = isFavorite,
    isPinned = isPinned,
    createdAt = createdAt,
    updatedAt = updatedAt
)
