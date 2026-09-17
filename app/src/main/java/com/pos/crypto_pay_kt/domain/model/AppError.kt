package com.pos.crypto_pay_kt.domain.model

class AppError(
    override val message: String,
) : Exception(message)
