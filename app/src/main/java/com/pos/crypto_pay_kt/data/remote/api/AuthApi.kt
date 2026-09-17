package com.pos.crypto_pay_kt.data.remote.api

import com.pos.crypto_pay_kt.data.remote.dto.AgentLoginRequestDto
import com.pos.crypto_pay_kt.data.remote.dto.AgentLoginResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/v1/Auth/agent-login")
    suspend fun login(@Body request: AgentLoginRequestDto): Response<AgentLoginResponseDto>
}
