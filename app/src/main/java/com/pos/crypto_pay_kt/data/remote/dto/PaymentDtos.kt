package com.pos.crypto_pay_kt.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChargeRequestDto(
    val amount: Double,
    val currency: String,
    val paymentMethod: String,
    val merchantWalletAddress: String,
    val customerPhone: String?,
)

@Serializable
data class ChargeResponseDto(
    val transactionId: String = "",
    val actionType: String = "",
    val actionUrl: String? = null,
    val message: String = "",
    val isSuccess: Boolean = false,
)

@Serializable
data class RefundRequestDto(val reason: String)
