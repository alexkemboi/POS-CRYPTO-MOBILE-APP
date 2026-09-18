package com.pos.crypto_pay_kt.domain.usecase

import com.pos.crypto_pay_kt.domain.repository.SessionRepository
import javax.inject.Inject

class ObserveSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
) {
    operator fun invoke() = sessionRepository.status
}
