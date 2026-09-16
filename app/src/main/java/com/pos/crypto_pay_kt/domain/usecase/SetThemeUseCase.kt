package com.pos.crypto_pay_kt.domain.usecase

import com.pos.crypto_pay_kt.domain.model.AppTheme
import com.pos.crypto_pay_kt.domain.repository.SettingsRepository
import javax.inject.Inject

class SetThemeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(theme: AppTheme) {
        settingsRepository.setTheme(theme)
    }
}
