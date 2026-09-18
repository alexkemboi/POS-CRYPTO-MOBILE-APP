package com.pos.crypto_pay_kt.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pos.crypto_pay_kt.domain.model.Transaction
import com.pos.crypto_pay_kt.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = mutableUiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            mutableUiState.value = HistoryUiState.Loading
            mutableUiState.value = runCatching { transactionRepository.getTransactions() }
                .fold(
                    onSuccess = { HistoryUiState.Content(it) },
                    onFailure = { HistoryUiState.Error(it.message ?: "Unable to load transactions.") },
                )
        }
    }
}

sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    data class Content(val transactions: List<Transaction>) : HistoryUiState
    data class Error(val message: String) : HistoryUiState
}
