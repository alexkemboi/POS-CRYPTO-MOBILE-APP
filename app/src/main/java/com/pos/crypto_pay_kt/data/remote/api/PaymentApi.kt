package com.pos.crypto_pay_kt.data.remote.api

import com.pos.crypto_pay_kt.data.remote.dto.ChargeRequestDto
import com.pos.crypto_pay_kt.data.remote.dto.ChargeResponseDto
import com.pos.crypto_pay_kt.data.remote.dto.RefundRequestDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface PaymentApi {
    @POST("api/v1/transactions/charge")
    suspend fun createCharge(@Body request: ChargeRequestDto): Response<ChargeResponseDto>

    @POST("api/v1/transactions/{transactionId}/refund")
    suspend fun refund(
        @Path("transactionId") transactionId: String,
        @Body request: RefundRequestDto,
    ): Response<ResponseBody>
}
