package com.pos.crypto_pay_kt.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos.crypto_pay_kt.domain.model.AppTheme
import com.pos.crypto_pay_kt.domain.model.AppLanguage
import com.pos.crypto_pay_kt.domain.usecase.LogoutUseCase
import com.pos.crypto_pay_kt.domain.usecase.ObserveLanguageUseCase
import com.pos.crypto_pay_kt.domain.usecase.ObserveThemeUseCase
import com.pos.crypto_pay_kt.domain.usecase.SetLanguageUseCase
import com.pos.crypto_pay_kt.domain.usecase.SetThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeTheme: ObserveThemeUseCase,
    observeLanguage: ObserveLanguageUseCase,
    private val setTheme: SetThemeUseCase,
    private val setLanguage: SetLanguageUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> = combine(
        observeTheme(),
        observeLanguage(),
    ) { theme, language ->
        SettingsUiState(selectedTheme = theme, selectedLanguage = language)
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState(),
        )

    fun selectTheme(theme: AppTheme) {
        viewModelScope.launch {
            setTheme(theme)
        }
    }

    fun selectLanguage(language: AppLanguage) {
        viewModelScope.launch { setLanguage(language) }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }
}

data class SettingsUiState(
    val selectedTheme: AppTheme = AppTheme.LIGHT,
    val selectedLanguage: AppLanguage = AppLanguage.ENGLISH,
)
