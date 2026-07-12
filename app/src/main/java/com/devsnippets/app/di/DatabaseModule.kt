package com.devsnippets.app.di

import android.content.Context
import androidx.room.Room
import com.devsnippets.app.data.local.AppDatabase
import com.devsnippets.app.data.local.UserPreferences
import com.devsnippets.app.data.local.dao.SnippetDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideSnippetDao(database: AppDatabase): SnippetDao = database.snippetDao()
}
