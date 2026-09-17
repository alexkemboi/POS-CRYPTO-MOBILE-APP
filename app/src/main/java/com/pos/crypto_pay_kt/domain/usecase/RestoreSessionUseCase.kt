package com.pos.crypto_pay_kt.domain.usecase

import com.pos.crypto_pay_kt.domain.repository.SessionRepository
import javax.inject.Inject

class RestoreSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke() = sessionRepository.restoreSession()
}
