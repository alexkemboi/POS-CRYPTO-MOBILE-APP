package com.pos.crypto_pay_kt.domain.usecase

import com.pos.crypto_pay_kt.domain.repository.PaymentRepository
import javax.inject.Inject

class RefundTransactionUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository,
) {
    suspend operator fun invoke(transactionId: String, reason: String) =
        paymentRepository.refund(transactionId, reason)
}
