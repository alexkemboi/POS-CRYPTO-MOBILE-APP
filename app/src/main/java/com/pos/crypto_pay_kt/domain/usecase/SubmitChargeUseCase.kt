package com.pos.crypto_pay_kt.domain.usecase

import com.pos.crypto_pay_kt.domain.model.ChargeRequest
import com.pos.crypto_pay_kt.domain.repository.PaymentRepository
import javax.inject.Inject

class SubmitChargeUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository,
) {
    suspend operator fun invoke(request: ChargeRequest) = paymentRepository.createCharge(request)
}
