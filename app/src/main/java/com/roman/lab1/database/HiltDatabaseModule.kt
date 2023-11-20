package com.roman.lab1.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HiltDatabaseModule {
    @Singleton
    @Provides
    fun provideMyDatabase(
        @ApplicationContext applicationContext: Context
    ): MyDatabase {
        return Room.databaseBuilder(
            applicationContext,
            MyDatabase::class.java, "database-name"
        ).build()
    }
}