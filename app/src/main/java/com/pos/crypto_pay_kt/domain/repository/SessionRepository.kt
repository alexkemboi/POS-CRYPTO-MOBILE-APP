package com.pos.crypto_pay_kt.domain.repository

import com.pos.crypto_pay_kt.domain.model.SessionStatus
import kotlinx.coroutines.flow.StateFlow

interface SessionRepository {
    val status: StateFlow<SessionStatus>

    suspend fun restoreSession()
    suspend fun saveSession(accessToken: String, agentName: String, role: String)
    suspend fun accessToken(): String?
    suspend fun recordBackgroundedAt(timestampMillis: Long)
    suspend fun expireIfInactive(timestampMillis: Long): Boolean
    suspend fun clearSession()
}
