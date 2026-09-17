package com.pos.crypto_pay_kt.data.repository

import com.pos.crypto_pay_kt.BuildConfig
import com.pos.crypto_pay_kt.data.remote.api.ApiErrorParser
import com.pos.crypto_pay_kt.data.remote.api.PaymentApi
import com.pos.crypto_pay_kt.data.remote.dto.ChargeRequestDto
import com.pos.crypto_pay_kt.data.remote.dto.RefundRequestDto
import com.pos.crypto_pay_kt.data.remote.realtime.PaymentCompletionService
import com.pos.crypto_pay_kt.domain.model.AppError
import com.pos.crypto_pay_kt.domain.model.ChargeRequest
import com.pos.crypto_pay_kt.domain.model.ChargeResult
import com.pos.crypto_pay_kt.domain.model.PaymentConfirmation
import com.pos.crypto_pay_kt.domain.repository.PaymentRepository
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart

@Singleton
class PaymentRepositoryImpl @Inject constructor(
    private val paymentApi: PaymentApi,
    private val errorParser: ApiErrorParser,
    private val completionService: PaymentCompletionService,
) : PaymentRepository {
    override suspend fun createCharge(request: ChargeRequest): ChargeResult {
        val response = try {
            paymentApi.createCharge(
                ChargeRequestDto(
                    amount = request.amount,
                    currency = request.currency.apiValue,
                    paymentMethod = request.paymentMethod.apiValue,
                    merchantWalletAddress = BuildConfig.MERCHANT_WALLET_ADDRESS,
                    customerPhone = request.customerPhone,
                ),
            )
        } catch (_: IOException) {
            throw AppError("Unable to reach the payment service. Check your connection.")
        }
        if (!response.isSuccessful) {
            throw AppError(
                errorParser.message(
                    response.errorBody(),
                    "Unable to start the charge. Please check the entered details.",
                ),
            )
        }
        val body = response.body() ?: throw AppError("The payment service returned an empty response.")
        if (body.transactionId.isBlank()) throw AppError("The payment response is missing a transaction ID.")
        return ChargeResult(
            transactionId = body.transactionId,
            actionType = body.actionType.lowercase(),
            actionUrl = body.actionUrl,
            message = body.message,
        )
    }

    override fun watchPayment(transactionId: String): Flow<PaymentConfirmation> {
        val disconnected = completionService.disconnections
            .map<Unit, PaymentConfirmation> {
                throw IOException("The payment status connection was interrupted.")
            }
        return merge(
            completionService.events.filter {
                it.transactionId.isBlank() || it.transactionId == transactionId
            },
            disconnected,
        ).onStart { completionService.start(transactionId) }
    }

    override suspend fun stopWatchingPayment() {
        completionService.stop()
    }

    override suspend fun refund(transactionId: String, reason: String) {
        val response = try {
            paymentApi.refund(transactionId, RefundRequestDto(reason))
        } catch (_: IOException) {
            throw AppError("Unable to reach the payment service. Check your connection.")
        }
        if (!response.isSuccessful) {
            val fallback = when (response.code()) {
                404 -> "Transaction not found."
                400 -> "This transaction cannot be reversed in its current state."
                403 -> "The agent reversal limit has been exceeded."
                else -> "Unable to process the reversal."
            }
            throw AppError(errorParser.message(response.errorBody(), fallback))
        }
    }
}
