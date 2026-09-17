package com.pos.crypto_pay_kt.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AgentLoginRequestDto(
    val terminalId: String,
    val agentId: String,
    val pin: String,
)

@Serializable
data class AgentLoginResponseDto(
    val token: String = "",
    val agentName: String = "Agent",
    val role: String = "User",
)
