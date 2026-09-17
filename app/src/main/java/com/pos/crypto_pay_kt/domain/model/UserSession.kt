package com.pos.crypto_pay_kt.domain.model

data class UserSession(
    val agentName: String,
    val role: String,
)

sealed interface SessionStatus {
    data object Checking : SessionStatus
    data object SignedOut : SessionStatus
    data class Authenticated(val session: UserSession) : SessionStatus
}
