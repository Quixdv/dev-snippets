package com.devsnippets.app.di

import com.devsnippets.app.data.repository.SnippetRepositoryImpl
import com.devsnippets.app.domain.repository.SnippetRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSnippetRepository(impl: SnippetRepositoryImpl): SnippetRepository
}
