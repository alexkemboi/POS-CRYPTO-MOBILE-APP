package com.pos.crypto_pay_kt.domain.usecase

import com.pos.crypto_pay_kt.domain.repository.SettingsRepository
import javax.inject.Inject

class ObserveLanguageUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke() = settingsRepository.language
}
