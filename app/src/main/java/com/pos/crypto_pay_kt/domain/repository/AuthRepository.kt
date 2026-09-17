package com.pos.crypto_pay_kt.domain.repository

interface AuthRepository {
    suspend fun login(pin: String)
}
