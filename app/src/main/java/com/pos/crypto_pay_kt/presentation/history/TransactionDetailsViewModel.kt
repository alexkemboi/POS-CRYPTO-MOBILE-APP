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
class TransactionDetailsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
) : ViewModel() {
    private val mutableTransaction = MutableStateFlow<Transaction?>(null)
    val transaction: StateFlow<Transaction?> = mutableTransaction.asStateFlow()

    fun load(id: String) {
        if (mutableTransaction.value?.id == id) return
        viewModelScope.launch { mutableTransaction.value = transactionRepository.getTransaction(id) }
    }
}
