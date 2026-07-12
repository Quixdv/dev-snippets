package com.devsnippets.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.devsnippets.app.data.local.dao.SnippetDao
import com.devsnippets.app.data.local.entity.SnippetEntity

@Database(
    entities = [SnippetEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun snippetDao(): SnippetDao

    companion object {
        const val DATABASE_NAME = "dev_snippets_db"
    }
}
