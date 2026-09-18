package com.pos.crypto_pay_kt.presentation.charge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos.crypto_pay_kt.domain.model.ChargeRequest
import com.pos.crypto_pay_kt.domain.model.PaymentCurrency
import com.pos.crypto_pay_kt.domain.model.PaymentMethod
import com.pos.crypto_pay_kt.domain.repository.PaymentRepository
import com.pos.crypto_pay_kt.domain.repository.SettingsRepository
import com.pos.crypto_pay_kt.domain.usecase.SubmitChargeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.withContext

@HiltViewModel
class ChargeViewModel @Inject constructor(
    private val submitCharge: SubmitChargeUseCase,
    private val paymentRepository: PaymentRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(ChargeUiState())
    val uiState: StateFlow<ChargeUiState> = mutableUiState.asStateFlow()

    private val eventChannel = Channel<ChargeEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()
    private var confirmationJob: Job? = null

    init {
        viewModelScope.launch {
            settingsRepository.isChargeIntroDismissed.collect { dismissed ->
                mutableUiState.update { it.copy(showIntro = !dismissed) }
            }
        }
    }

    fun updateAmount(value: String) {
        val sanitized = value.filter { it.isDigit() || it == '.' }
        if (sanitized.count { it == '.' } <= 1) {
            mutableUiState.update { it.copy(amount = sanitized, errorMessage = null) }
        }
    }

    fun updatePhone(value: String) {
        mutableUiState.update {
            it.copy(phone = value.filter { char -> char.isDigit() || char == '+' }.take(16), errorMessage = null)
        }
    }

    fun selectMethod(method: PaymentMethod) {
        mutableUiState.update { it.copy(paymentMethod = method, errorMessage = null) }
    }

    fun selectCurrency(currency: PaymentCurrency) {
        mutableUiState.update { it.copy(currency = currency) }
    }

    fun dismissIntro() {
        mutableUiState.update { it.copy(showIntro = false) }
        viewModelScope.launch { settingsRepository.dismissChargeIntro() }
    }

    fun submit() {
        val state = mutableUiState.value
        val amount = state.amount.toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            mutableUiState.update { it.copy(errorMessage = "Enter a valid amount.") }
            return
        }
        if (state.paymentMethod == PaymentMethod.MPESA && state.phone.isBlank()) {
            mutableUiState.update { it.copy(errorMessage = "Enter the customer phone number.") }
            return
        }
        viewModelScope.launch {
            mutableUiState.update {
                it.copy(phase = ChargePhase.SUBMITTING, statusMessage = "Preparing charge request...", errorMessage = null)
            }
            runCatching {
                submitCharge(
                    ChargeRequest(
                        amount = amount,
                        currency = state.currency,
                        paymentMethod = state.paymentMethod,
                        customerPhone = state.phone.takeIf { state.paymentMethod == PaymentMethod.MPESA },
                    ),
                )
            }.onSuccess { result ->
                val checkoutUrl = result.actionUrl.takeIf {
                    result.actionType == "webview" && !it.isNullOrBlank()
                }
                mutableUiState.update {
                    it.copy(
                        phase = ChargePhase.AWAITING,
                        transactionId = result.transactionId,
                        checkoutUrl = checkoutUrl,
                        statusMessage = if (checkoutUrl != null) {
                            "Awaiting blockchain settlement..."
                        } else {
                            "Waiting for customer approval..."
                        },
                    )
                }
                watchForCompletion(result.transactionId)
                if (checkoutUrl != null) eventChannel.send(ChargeEvent.OpenCheckout(checkoutUrl))
            }.onFailure { error ->
                mutableUiState.update {
                    it.copy(
                        phase = ChargePhase.FORM,
                        errorMessage = error.message ?: "Unable to start the charge.",
                    )
                }
            }
        }
    }

    fun checkoutOpenFailed() {
        mutableUiState.update {
            it.copy(errorMessage = "No browser is available to open secure checkout.")
        }
    }

    fun reopenCheckout() {
        mutableUiState.value.checkoutUrl?.let { eventChannel.trySend(ChargeEvent.OpenCheckout(it)) }
    }

    fun reset() {
        confirmationJob?.cancel()
        viewModelScope.launch { paymentRepository.stopWatchingPayment() }
        mutableUiState.update {
            ChargeUiState(showIntro = it.showIntro)
        }
    }

    private fun watchForCompletion(transactionId: String) {
        confirmationJob?.cancel()
        confirmationJob = viewModelScope.launch {
            val confirmation = try {
                withTimeoutOrNull(CONFIRMATION_TIMEOUT_MILLIS) {
                    var result = runCatching {
                        paymentRepository.watchPayment(transactionId).first { it.isComplete }
                    }.getOrNull()
                    while (isActive && result == null) {
                        delay(RECONNECT_DELAY_MILLIS)
                        result = runCatching {
                            paymentRepository.watchPayment(transactionId).first { it.isComplete }
                        }.getOrNull()
                    }
                    result
                }
            } finally {
                withContext(NonCancellable) { paymentRepository.stopWatchingPayment() }
            }
            if (confirmation != null) {
                mutableUiState.update {
                    it.copy(phase = ChargePhase.SUCCESS, statusMessage = "Payment confirmed")
                }
            } else {
                mutableUiState.update {
                    it.copy(
                        phase = ChargePhase.FORM,
                        errorMessage = "Payment confirmation timed out. Check transaction history before retrying.",
                    )
                }
            }
        }
    }

    private companion object {
        const val CONFIRMATION_TIMEOUT_MILLIS = 10 * 60 * 1_000L
        const val RECONNECT_DELAY_MILLIS = 5_000L
    }
}

data class ChargeUiState(
    val amount: String = "",
    val phone: String = "",
    val paymentMethod: PaymentMethod = PaymentMethod.CARD,
    val currency: PaymentCurrency = PaymentCurrency.KES,
    val phase: ChargePhase = ChargePhase.FORM,
    val statusMessage: String = "",
    val errorMessage: String? = null,
    val transactionId: String = "",
    val checkoutUrl: String? = null,
    val showIntro: Boolean = true,
)

enum class ChargePhase { FORM, SUBMITTING, AWAITING, SUCCESS }

sealed interface ChargeEvent {
    data class OpenCheckout(val url: String) : ChargeEvent
}
