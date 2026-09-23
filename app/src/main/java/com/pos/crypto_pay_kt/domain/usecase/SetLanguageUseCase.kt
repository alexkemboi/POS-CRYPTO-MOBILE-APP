package com.pos.crypto_pay_kt.domain.usecase

import com.pos.crypto_pay_kt.domain.model.AppLanguage
import com.pos.crypto_pay_kt.domain.repository.SettingsRepository
import javax.inject.Inject

class SetLanguageUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(language: AppLanguage) = settingsRepository.setLanguage(language)
}
