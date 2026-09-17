package com.pos.crypto_pay_kt.domain.repository

import com.pos.crypto_pay_kt.domain.model.Transaction

interface TransactionRepository {
    suspend fun getTransactions(): List<Transaction>
    suspend fun getTransaction(id: String): Transaction?
}
