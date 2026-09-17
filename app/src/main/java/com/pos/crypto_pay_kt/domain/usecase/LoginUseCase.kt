package com.pos.crypto_pay_kt.domain.usecase

import com.pos.crypto_pay_kt.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(pin: String) = authRepository.login(pin)
}
