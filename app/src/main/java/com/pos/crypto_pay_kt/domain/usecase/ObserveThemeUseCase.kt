package com.pos.crypto_pay_kt.domain.usecase

import com.pos.crypto_pay_kt.domain.model.AppTheme
import com.pos.crypto_pay_kt.domain.repository.SettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveThemeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(): Flow<AppTheme> = settingsRepository.theme
}
