package com.pos.crypto_pay_kt.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos.crypto_pay_kt.BuildConfig
import com.pos.crypto_pay_kt.domain.repository.SessionRepository
import com.pos.crypto_pay_kt.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val login: LoginUseCase,
    private val sessionRepository: SessionRepository,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = mutableUiState.asStateFlow()

    fun updatePin(value: String) {
        val pin = value.filter(Char::isDigit).take(PIN_LENGTH)
        mutableUiState.update { it.copy(pin = pin, errorMessage = null) }
        if (pin.length == PIN_LENGTH && !mutableUiState.value.isLoading) submit()
    }

    fun submit() {
        val pin = mutableUiState.value.pin
        if (pin.length != PIN_LENGTH || mutableUiState.value.isLoading) return
        viewModelScope.launch {
            mutableUiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                if (BuildConfig.DEBUG && pin == TEST_PIN) {
                    sessionRepository.saveSession(
                        accessToken = TEST_ACCESS_TOKEN,
                        agentName = "Test Merchant",
                        role = "UI Preview",
                    )
                } else {
                    login(pin)
                }
            }
                .onFailure { error ->
                    mutableUiState.update {
                        it.copy(
                            pin = "",
                            isLoading = false,
                            errorMessage = error.message ?: "Unable to sign in.",
                        )
                    }
                }
        }
    }

    private companion object {
        const val PIN_LENGTH = 4
        const val TEST_PIN = "1111"
        const val TEST_ACCESS_TOKEN = "debug-ui-preview-session"
    }
}

data class LoginUiState(
    val pin: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
