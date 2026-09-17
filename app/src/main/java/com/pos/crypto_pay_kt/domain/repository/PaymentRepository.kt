package com.pos.crypto_pay_kt.domain.repository

import com.pos.crypto_pay_kt.domain.model.ChargeRequest
import com.pos.crypto_pay_kt.domain.model.ChargeResult
import com.pos.crypto_pay_kt.domain.model.PaymentConfirmation
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {
    suspend fun createCharge(request: ChargeRequest): ChargeResult
    fun watchPayment(transactionId: String): Flow<PaymentConfirmation>
    suspend fun stopWatchingPayment()
    suspend fun refund(transactionId: String, reason: String)
}
