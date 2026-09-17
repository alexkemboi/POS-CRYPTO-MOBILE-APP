package com.pos.crypto_pay_kt.data.repository

import com.pos.crypto_pay_kt.domain.model.Transaction
import com.pos.crypto_pay_kt.domain.repository.TransactionRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay

@Singleton
class TransactionRepositoryImpl @Inject constructor() : TransactionRepository {
    private val transactions = listOf(
        Transaction(
            id = "TXN-98234710",
            type = "Received",
            status = "Completed",
            crypto = "BTC",
            amount = "+0.015 BTC",
            fiat = "+KES 0.00",
            date = "May 2, 2026, 14:30",
            address = "bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh",
        ),
        Transaction(
            id = "TXN-98234711",
            type = "Received",
            status = "Completed",
            crypto = "ETH",
            amount = "+1.2 ETH",
            fiat = "+KES 1,200.00",
            date = "May 1, 2026, 09:15",
            address = "0x32Be343B94f860124dC4fEe278FDCBD38C102D88",
        ),
    )

    override suspend fun getTransactions(): List<Transaction> {
        delay(350)
        return transactions
    }

    override suspend fun getTransaction(id: String): Transaction? =
        transactions.firstOrNull { it.id == id }
}
