package com.devsnippets.app.data.local.dao

import androidx.room.*
import com.devsnippets.app.data.local.entity.SnippetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SnippetDao {

    @Query("SELECT * FROM snippets ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllSnippets(): Flow<List<SnippetEntity>>

    @Query("SELECT * FROM snippets WHERE isFavorite = 1 ORDER BY isPinned DESC, updatedAt DESC")
    fun getFavoriteSnippets(): Flow<List<SnippetEntity>>

    @Query("SELECT * FROM snippets WHERE isPinned = 1 ORDER BY updatedAt DESC")
    fun getPinnedSnippets(): Flow<List<SnippetEntity>>

    @Query("SELECT * FROM snippets ORDER BY updatedAt DESC LIMIT :limit")
    fun getRecentSnippets(limit: Int): Flow<List<SnippetEntity>>

    @Query(
        """
        SELECT * FROM snippets 
        WHERE title LIKE '%' || :query || '%' 
           OR language LIKE '%' || :query || '%' 
           OR tags LIKE '%' || :query || '%' 
           OR code LIKE '%' || :query || '%'
        ORDER BY isPinned DESC, updatedAt DESC
        """
    )
    fun searchSnippets(query: String): Flow<List<SnippetEntity>>

    @Query("SELECT * FROM snippets WHERE id = :id LIMIT 1")
    suspend fun getSnippetById(id: Long): SnippetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnippet(entity: SnippetEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<SnippetEntity>)

    @Update
    suspend fun updateSnippet(entity: SnippetEntity)

    @Delete
    suspend fun deleteSnippet(entity: SnippetEntity)

    @Query("UPDATE snippets SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)

    @Query("UPDATE snippets SET isPinned = :isPinned WHERE id = :id")
    suspend fun setPinned(id: Long, isPinned: Boolean)

    @Query("SELECT * FROM snippets")
    suspend fun getAllSnippetsOnce(): List<SnippetEntity>

    @Query("DELETE FROM snippets")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM snippets")
    suspend fun getTotalCount(): Int

    @Query("SELECT COUNT(*) FROM snippets WHERE isFavorite = 1")
    suspend fun getFavoriteCount(): Int

    @Query("SELECT COUNT(*) FROM snippets WHERE isPinned = 1")
    suspend fun getPinnedCount(): Int

    @Query("SELECT language, COUNT(*) as count FROM snippets GROUP BY language")
    suspend fun getLanguageBreakdown(): List<LanguageCount>
}

data class LanguageCount(
    val language: String,
    val count: Int
)
