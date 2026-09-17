package com.pos.crypto_pay_kt.domain.model

enum class PaymentMethod(val apiValue: String) {
    CARD("CARD"),
    MPESA("MPESA"),
}

enum class PaymentCurrency(val apiValue: String) {
    KES("KES"),
    USD("USD"),
}

data class ChargeRequest(
    val amount: Double,
    val currency: PaymentCurrency,
    val paymentMethod: PaymentMethod,
    val customerPhone: String?,
)

data class ChargeResult(
    val transactionId: String,
    val actionType: String,
    val actionUrl: String?,
    val message: String,
)

data class PaymentConfirmation(
    val transactionId: String,
    val status: String,
) {
    val isComplete: Boolean
        get() = status.uppercase() in setOf("COMPLETED", "CONFIRMED", "SUCCESS", "SETTLED")
}
