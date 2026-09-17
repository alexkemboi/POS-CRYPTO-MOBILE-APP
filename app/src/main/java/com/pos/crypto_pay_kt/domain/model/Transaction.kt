package com.pos.crypto_pay_kt.domain.model

data class Transaction(
    val id: String,
    val type: String,
    val status: String,
    val crypto: String,
    val amount: String,
    val fiat: String,
    val date: String,
    val address: String,
)
