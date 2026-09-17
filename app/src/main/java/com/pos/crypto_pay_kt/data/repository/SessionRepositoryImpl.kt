package com.pos.crypto_pay_kt.data.repository

import com.pos.crypto_pay_kt.data.local.security.SecureSessionStorage
import com.pos.crypto_pay_kt.domain.model.SessionStatus
import com.pos.crypto_pay_kt.domain.model.UserSession
import com.pos.crypto_pay_kt.domain.repository.SessionRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

@Singleton
class SessionRepositoryImpl @Inject constructor(
    private val storage: SecureSessionStorage,
) : SessionRepository {
    private val mutableStatus = MutableStateFlow<SessionStatus>(SessionStatus.Checking)
    override val status: StateFlow<SessionStatus> = mutableStatus.asStateFlow()

    override suspend fun restoreSession() = withContext(Dispatchers.IO) {
        if (expireIfInactive(System.currentTimeMillis())) return@withContext
        val token = storage.read(SecureSessionStorage.ACCESS_TOKEN)
        if (token.isNullOrBlank()) {
            mutableStatus.value = SessionStatus.SignedOut
            return@withContext
        }
        mutableStatus.value = SessionStatus.Authenticated(
            UserSession(
                agentName = storage.read(SecureSessionStorage.AGENT_NAME) ?: "Agent",
                role = storage.read(SecureSessionStorage.ROLE) ?: "User",
            ),
        )
    }

    override suspend fun saveSession(accessToken: String, agentName: String, role: String) =
        withContext(Dispatchers.IO) {
            storage.write(SecureSessionStorage.ACCESS_TOKEN, accessToken)
            storage.write(SecureSessionStorage.AGENT_NAME, agentName)
            storage.write(SecureSessionStorage.ROLE, role)
            storage.remove(SecureSessionStorage.BACKGROUNDED_AT)
            mutableStatus.value = SessionStatus.Authenticated(UserSession(agentName, role))
        }

    override suspend fun accessToken(): String? = withContext(Dispatchers.IO) {
        storage.read(SecureSessionStorage.ACCESS_TOKEN)
    }

    override suspend fun recordBackgroundedAt(timestampMillis: Long) = withContext(Dispatchers.IO) {
        if (mutableStatus.value is SessionStatus.Authenticated) {
            storage.write(SecureSessionStorage.BACKGROUNDED_AT, timestampMillis.toString())
        }
    }

    override suspend fun expireIfInactive(timestampMillis: Long): Boolean =
        withContext(Dispatchers.IO) {
            val backgroundedAt = storage.read(SecureSessionStorage.BACKGROUNDED_AT)
                ?.toLongOrNull()
                ?: return@withContext false
            val elapsed = timestampMillis - backgroundedAt
            if (elapsed >= SESSION_TIMEOUT_MILLIS) {
                storage.clearSession()
                mutableStatus.value = SessionStatus.SignedOut
                true
            } else {
                storage.remove(SecureSessionStorage.BACKGROUNDED_AT)
                false
            }
        }

    override suspend fun clearSession() = withContext(Dispatchers.IO) {
        storage.clearSession()
        mutableStatus.value = SessionStatus.SignedOut
    }

    private companion object {
        const val SESSION_TIMEOUT_MILLIS = 10 * 60 * 1_000L
    }
}
