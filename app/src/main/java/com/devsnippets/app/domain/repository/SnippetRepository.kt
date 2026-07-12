package com.devsnippets.app.domain.repository

import com.devsnippets.app.domain.model.Snippet
import kotlinx.coroutines.flow.Flow

/**
 * Abstraction over snippet persistence. The ViewModels depend on this
 * interface only, never on Room directly (Repository pattern).
 */
interface SnippetRepository {

    /** All snippets, pinned first, then most recently updated. */
    fun getAllSnippets(): Flow<List<Snippet>>

    /** Only favorited snippets. */
    fun getFavoriteSnippets(): Flow<List<Snippet>>

    /** Most recently created/updated snippets, limited count. */
    fun getRecentSnippets(limit: Int = 10): Flow<List<Snippet>>

    /** Pinned snippets only. */
    fun getPinnedSnippets(): Flow<List<Snippet>>

    /** Instant full-text-ish search across title, language, tags and code. */
    fun searchSnippets(query: String): Flow<List<Snippet>>

    /** Fetch a single snippet by id, or null if not found. */
    suspend fun getSnippetById(id: Long): Snippet?

    /** Insert a new snippet, returns the generated id. */
    suspend fun insertSnippet(snippet: Snippet): Long

    /** Update an existing snippet. */
    suspend fun updateSnippet(snippet: Snippet)

    /** Delete a snippet. */
    suspend fun deleteSnippet(snippet: Snippet)

    /** Toggle favorite flag for a snippet id. */
    suspend fun toggleFavorite(id: Long, isFavorite: Boolean)

    /** Toggle pinned flag for a snippet id. */
    suspend fun togglePinned(id: Long, isPinned: Boolean)

    /** Snapshot list used for JSON export. */
    suspend fun getAllSnippetsOnce(): List<Snippet>

    /** Bulk insert, used for JSON import / restore. */
    suspend fun insertAll(snippets: List<Snippet>)

    /** Wipes all data, used before a restore operation. */
    suspend fun deleteAll()

    /** Basic aggregate counts used by the Statistics screen. */
    suspend fun getStatistics(): SnippetStatistics
}

data class SnippetStatistics(
    val totalSnippets: Int,
    val favoriteCount: Int,
    val pinnedCount: Int,
    val languageBreakdown: Map<String, Int>
)
