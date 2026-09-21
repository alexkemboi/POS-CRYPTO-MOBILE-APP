package com.pos.crypto_pay_kt.presentation.refund

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos.crypto_pay_kt.domain.usecase.RefundTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class RefundViewModel @Inject constructor(
    private val refundTransaction: RefundTransactionUseCase,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(RefundUiState())
    val uiState: StateFlow<RefundUiState> = mutableUiState.asStateFlow()

    fun updateReason(reason: String) {
        mutableUiState.update { it.copy(reason = reason, errorMessage = null) }
    }

    fun submit(transactionId: String) {
        val reason = mutableUiState.value.reason.trim()
        if (reason.isBlank()) {
            mutableUiState.update { it.copy(errorMessage = "Provide a reason for the reversal.") }
            return
        }
        viewModelScope.launch {
            mutableUiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { refundTransaction(transactionId, reason) }
                .onSuccess { mutableUiState.update { it.copy(isLoading = false, isSuccess = true) } }
                .onFailure { error ->
                    mutableUiState.update { it.copy(isLoading = false, errorMessage = error.message ?: "Unable to process the reversal.") }
                }
        }
    }
}

data class RefundUiState(
    val reason: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
)
