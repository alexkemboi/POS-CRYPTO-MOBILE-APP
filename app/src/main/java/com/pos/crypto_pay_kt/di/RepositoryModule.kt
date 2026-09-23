package com.pos.crypto_pay_kt.di

import com.pos.crypto_pay_kt.data.repository.AuthRepositoryImpl
import com.pos.crypto_pay_kt.data.repository.PaymentRepositoryImpl
import com.pos.crypto_pay_kt.data.repository.SessionRepositoryImpl
import com.pos.crypto_pay_kt.data.repository.SettingsRepositoryImpl
import com.pos.crypto_pay_kt.data.repository.TransactionRepositoryImpl
import com.pos.crypto_pay_kt.domain.repository.AuthRepository
import com.pos.crypto_pay_kt.domain.repository.PaymentRepository
import com.pos.crypto_pay_kt.domain.repository.SessionRepository
import com.pos.crypto_pay_kt.domain.repository.SettingsRepository
import com.pos.crypto_pay_kt.domain.repository.TransactionRepository
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
        implementation: SessionRepositoryImpl,
    ): SessionRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        implementation: AuthRepositoryImpl,
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindPaymentRepository(
        implementation: PaymentRepositoryImpl,
    ): PaymentRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        implementation: TransactionRepositoryImpl,
    ): TransactionRepository
}
