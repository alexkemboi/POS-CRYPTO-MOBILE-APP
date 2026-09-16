package com.pos.crypto_pay_kt.domain.repository

import com.pos.crypto_pay_kt.domain.model.AppTheme
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val theme: Flow<AppTheme>

    suspend fun setTheme(theme: AppTheme)
}
