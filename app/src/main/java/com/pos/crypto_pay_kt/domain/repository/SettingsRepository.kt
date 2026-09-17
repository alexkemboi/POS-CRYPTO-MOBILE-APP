package com.pos.crypto_pay_kt.domain.repository

import com.pos.crypto_pay_kt.domain.model.AppTheme
import com.pos.crypto_pay_kt.domain.model.AppLanguage
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val theme: Flow<AppTheme>
    val language: Flow<AppLanguage>
    val isChargeIntroDismissed: Flow<Boolean>

    suspend fun setTheme(theme: AppTheme)
    suspend fun setLanguage(language: AppLanguage)
    suspend fun dismissChargeIntro()
}
