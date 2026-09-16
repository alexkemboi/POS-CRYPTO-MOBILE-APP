package com.pos.crypto_pay_kt.data.repository

import com.pos.crypto_pay_kt.domain.repository.SessionRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

@Singleton
class InMemorySessionRepository @Inject constructor() : SessionRepository {
    private val accessToken = MutableStateFlow<String?>(null)

    override val isAuthenticated: Flow<Boolean> = accessToken.map { token ->
        !token.isNullOrBlank()
    }

    override suspend fun updateAccessToken(accessToken: String) {
        this.accessToken.value = accessToken
    }

    override suspend fun clearSession() {
        accessToken.value = null
    }
}
