package com.pos.crypto_pay_kt.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos.crypto_pay_kt.domain.model.AppTheme
import com.pos.crypto_pay_kt.domain.usecase.LogoutUseCase
import com.pos.crypto_pay_kt.domain.usecase.ObserveThemeUseCase
import com.pos.crypto_pay_kt.domain.usecase.SetThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeTheme: ObserveThemeUseCase,
    private val setTheme: SetThemeUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> = observeTheme()
        .map { theme -> SettingsUiState(selectedTheme = theme) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState(),
        )

    private val eventChannel = Channel<SettingsEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    fun selectTheme(theme: AppTheme) {
        viewModelScope.launch {
            setTheme(theme)
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            eventChannel.send(SettingsEvent.LoggedOut)
        }
    }
}

data class SettingsUiState(
    val selectedTheme: AppTheme = AppTheme.LIGHT,
)

sealed interface SettingsEvent {
    data object LoggedOut : SettingsEvent
}
