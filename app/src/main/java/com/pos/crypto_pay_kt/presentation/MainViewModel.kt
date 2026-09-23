package com.pos.crypto_pay_kt.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos.crypto_pay_kt.domain.model.AppTheme
import com.pos.crypto_pay_kt.domain.model.SessionStatus
import com.pos.crypto_pay_kt.domain.usecase.ObserveThemeUseCase
import com.pos.crypto_pay_kt.domain.usecase.ObserveSessionUseCase
import com.pos.crypto_pay_kt.domain.usecase.RestoreSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    observeTheme: ObserveThemeUseCase,
    observeSession: ObserveSessionUseCase,
    private val restoreSession: RestoreSessionUseCase,
) : ViewModel() {
    val uiState: StateFlow<MainUiState> = combine(
        observeTheme(),
        observeSession(),
    ) { theme, sessionStatus ->
        MainUiState(theme = theme, sessionStatus = sessionStatus)
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MainUiState(),
        )

    init {
        viewModelScope.launch { restoreSession() }
    }
}

data class MainUiState(
    val theme: AppTheme = AppTheme.LIGHT,
    val sessionStatus: SessionStatus = SessionStatus.Checking,
)
