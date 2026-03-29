package com.focusguard.di

import com.focusguard.data.repository.BlockSessionRepositoryImpl
import com.focusguard.data.repository.SettingsRepositoryImpl
import com.focusguard.domain.repository.BlockSessionRepository
import com.focusguard.domain.repository.SettingsRepository
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
    abstract fun bindBlockSessionRepository(
        impl: BlockSessionRepositoryImpl
    ): BlockSessionRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository
}
