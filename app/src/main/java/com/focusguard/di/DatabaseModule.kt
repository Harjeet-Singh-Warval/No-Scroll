package com.focusguard.di

import android.content.Context
import androidx.room.Room
import com.focusguard.data.local.AppDatabase
import com.focusguard.data.local.dao.BlockSessionDao
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "focusguard_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideBlockSessionDao(database: AppDatabase): BlockSessionDao {
        return database.blockSessionDao
    }
}
