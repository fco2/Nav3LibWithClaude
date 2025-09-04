package com.chuka.nav3libwithclaude.di

import android.content.Context
import androidx.room.Room
import com.chuka.nav3libwithclaude.data.HumanDatabase
import com.chuka.nav3libwithclaude.data.dao.HumanDao
import com.chuka.nav3libwithclaude.domain.repositories.HumanRepository
import com.chuka.nav3libwithclaude.domain.repositories.HumanRepositoryImpl
import com.chuka.nav3libwithclaude.presentation.navigation.NavigationManager
import com.chuka.nav3libwithclaude.presentation.navigation.NavigationManagerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HumanDatabase {
        return Room.databaseBuilder(
            context,
            HumanDatabase::class.java,
            "human_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideDao(database: HumanDatabase) = database.humanDao()

    @Provides
    @Singleton
    fun provideRepository(dao: HumanDao): HumanRepository = HumanRepositoryImpl(dao)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class NavigationModule {
    @Binds
    @Singleton
    abstract fun bindsNavigationManager(navigationManagerImpl: NavigationManagerImpl): NavigationManager
}