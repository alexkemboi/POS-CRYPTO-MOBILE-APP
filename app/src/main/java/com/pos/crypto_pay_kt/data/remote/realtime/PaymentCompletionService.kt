package com.pos.crypto_pay_kt.data.remote.realtime

import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.TransportEnum
import com.microsoft.signalr.TypeReference
import com.pos.crypto_pay_kt.BuildConfig
import com.pos.crypto_pay_kt.data.local.security.SecureSessionStorage
import com.pos.crypto_pay_kt.domain.model.PaymentConfirmation
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext

@Singleton
class PaymentCompletionService @Inject constructor(
    private val secureSessionStorage: SecureSessionStorage,
) {
    private val mutableEvents = MutableSharedFlow<PaymentConfirmation>(extraBufferCapacity = 8)
    val events: SharedFlow<PaymentConfirmation> = mutableEvents.asSharedFlow()
    private val mutableDisconnections = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val disconnections: SharedFlow<Unit> = mutableDisconnections.asSharedFlow()

    @Volatile
    private var connection: HubConnection? = null

    @Volatile
    private var activeTransactionId: String? = null

    suspend fun start(transactionId: String) = withContext(Dispatchers.IO) {
        stop()
        activeTransactionId = transactionId
        val newConnection = HubConnectionBuilder.create(BuildConfig.SIGNALR_URL)
            .withTransport(TransportEnum.WEBSOCKETS)
            .shouldSkipNegotiate(true)
            .withAccessTokenProvider(
                Single.fromCallable {
                    secureSessionStorage.read(SecureSessionStorage.ACCESS_TOKEN).orEmpty()
                },
            )
            .build()

        val payloadType = object : TypeReference<Map<String, Any>>() {}.type
        newConnection.on<Map<String, Any>>(
            "PaymentConfirmed",
            { payload -> handleConfirmation(payload) },
            payloadType,
        )
        newConnection.onClosed {
            if (connection === newConnection) mutableDisconnections.tryEmit(Unit)
        }
        connection = newConnection
        newConnection.start().blockingAwait()
    }

    suspend fun stop() = withContext(Dispatchers.IO) {
        val current = connection
        connection = null
        activeTransactionId = null
        runCatching { current?.stop()?.blockingAwait() }
    }

    private fun handleConfirmation(values: Map<String, Any>) {
        val transactionId = sequenceOf(
            values["transactionId"],
            values["transactionID"],
            values["id"],
            values["txId"],
            values["transaction_id"],
        ).firstOrNull { !it?.toString().isNullOrBlank() }
            ?.toString()
            ?: activeTransactionId.orEmpty()
        val status = (values["status"] ?: values["paymentStatus"])
            ?.toString()
            ?.takeIf(String::isNotBlank)
            ?: "COMPLETED"
        mutableEvents.tryEmit(PaymentConfirmation(transactionId, status))
    }
}
