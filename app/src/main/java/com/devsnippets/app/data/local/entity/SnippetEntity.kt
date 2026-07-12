package com.devsnippets.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room table representing a code snippet.
 * Tags are stored as a single comma-separated string for simplicity and
 * mapped to/from List<String> at the repository boundary.
 */
@Entity(tableName = "snippets")
data class SnippetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val code: String,
    val language: String,
    val tags: String, // comma-separated
    val notes: String,
    val isFavorite: Boolean,
    val isPinned: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)
