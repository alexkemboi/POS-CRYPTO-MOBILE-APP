package com.pos.crypto_pay_kt.data.repository

import com.pos.crypto_pay_kt.data.local.preferences.UserPreferencesDataSource
import com.pos.crypto_pay_kt.domain.model.AppTheme
import com.pos.crypto_pay_kt.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val preferencesDataSource: UserPreferencesDataSource,
) : SettingsRepository {
    override val theme: Flow<AppTheme> = preferencesDataSource.theme

    override suspend fun setTheme(theme: AppTheme) {
        preferencesDataSource.setTheme(theme)
    }
}
