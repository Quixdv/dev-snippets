package com.devsnippets.app.data.repository

import com.devsnippets.app.data.local.dao.SnippetDao
import com.devsnippets.app.data.mapper.toDomain
import com.devsnippets.app.data.mapper.toEntity
import com.devsnippets.app.domain.model.Snippet
import com.devsnippets.app.domain.repository.SnippetRepository
import com.devsnippets.app.domain.repository.SnippetStatistics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SnippetRepositoryImpl @Inject constructor(
    private val dao: SnippetDao
) : SnippetRepository {

    override fun getAllSnippets(): Flow<List<Snippet>> =
        dao.getAllSnippets().map { list -> list.map { it.toDomain() } }

    override fun getFavoriteSnippets(): Flow<List<Snippet>> =
        dao.getFavoriteSnippets().map { list -> list.map { it.toDomain() } }

    override fun getRecentSnippets(limit: Int): Flow<List<Snippet>> =
        dao.getRecentSnippets(limit).map { list -> list.map { it.toDomain() } }

    override fun getPinnedSnippets(): Flow<List<Snippet>> =
        dao.getPinnedSnippets().map { list -> list.map { it.toDomain() } }

    override fun searchSnippets(query: String): Flow<List<Snippet>> =
        dao.searchSnippets(query).map { list -> list.map { it.toDomain() } }

    override suspend fun getSnippetById(id: Long): Snippet? =
        dao.getSnippetById(id)?.toDomain()

    override suspend fun insertSnippet(snippet: Snippet): Long =
        dao.insertSnippet(snippet.toEntity())

    override suspend fun updateSnippet(snippet: Snippet) =
        dao.updateSnippet(snippet.toEntity())

    override suspend fun deleteSnippet(snippet: Snippet) =
        dao.deleteSnippet(snippet.toEntity())

    override suspend fun toggleFavorite(id: Long, isFavorite: Boolean) =
        dao.setFavorite(id, isFavorite)

    override suspend fun togglePinned(id: Long, isPinned: Boolean) =
        dao.setPinned(id, isPinned)

    override suspend fun getAllSnippetsOnce(): List<Snippet> =
        dao.getAllSnippetsOnce().map { it.toDomain() }

    override suspend fun insertAll(snippets: List<Snippet>) =
        dao.insertAll(snippets.map { it.toEntity() })

    override suspend fun deleteAll() = dao.deleteAll()

    override suspend fun getStatistics(): SnippetStatistics {
        val total = dao.getTotalCount()
        val favorites = dao.getFavoriteCount()
        val pinned = dao.getPinnedCount()
        val breakdown = dao.getLanguageBreakdown().associate { it.language to it.count }
        return SnippetStatistics(
            totalSnippets = total,
            favoriteCount = favorites,
            pinnedCount = pinned,
            languageBreakdown = breakdown
        )
    }
}
