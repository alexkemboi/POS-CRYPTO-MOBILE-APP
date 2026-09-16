package com.pos.crypto_pay_kt.di

import com.pos.crypto_pay_kt.data.repository.InMemorySessionRepository
import com.pos.crypto_pay_kt.data.repository.SettingsRepositoryImpl
import com.pos.crypto_pay_kt.domain.repository.SessionRepository
import com.pos.crypto_pay_kt.domain.repository.SettingsRepository
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
    abstract fun bindSettingsRepository(
        implementation: SettingsRepositoryImpl,
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindSessionRepository(
        implementation: InMemorySessionRepository,
    ): SessionRepository
}
