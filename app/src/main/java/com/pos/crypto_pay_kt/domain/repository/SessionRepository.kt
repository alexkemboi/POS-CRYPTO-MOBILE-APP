package com.pos.crypto_pay_kt.domain.repository

import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    val isAuthenticated: Flow<Boolean>

    suspend fun updateAccessToken(accessToken: String)

    suspend fun clearSession()
}
