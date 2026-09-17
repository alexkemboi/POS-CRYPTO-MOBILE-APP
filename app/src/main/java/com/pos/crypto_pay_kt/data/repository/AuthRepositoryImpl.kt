package com.pos.crypto_pay_kt.data.repository

import com.pos.crypto_pay_kt.BuildConfig
import com.pos.crypto_pay_kt.data.remote.api.ApiErrorParser
import com.pos.crypto_pay_kt.data.remote.api.AuthApi
import com.pos.crypto_pay_kt.data.remote.dto.AgentLoginRequestDto
import com.pos.crypto_pay_kt.domain.model.AppError
import com.pos.crypto_pay_kt.domain.repository.AuthRepository
import com.pos.crypto_pay_kt.domain.repository.SessionRepository
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val errorParser: ApiErrorParser,
    private val sessionRepository: SessionRepository,
) : AuthRepository {
    override suspend fun login(pin: String) {
        val response = try {
            authApi.login(
                AgentLoginRequestDto(
                    terminalId = BuildConfig.TERMINAL_ID,
                    agentId = BuildConfig.AGENT_ID,
                    pin = pin,
                ),
            )
        } catch (_: IOException) {
            throw AppError("Unable to reach the server. Check your connection and try again.")
        }

        if (!response.isSuccessful) {
            val fallback = when (response.code()) {
                401 -> "Invalid credentials."
                403 -> "This agent account is suspended."
                404 -> "This terminal is not registered."
                else -> "Unable to sign in. Please try again."
            }
            throw AppError(errorParser.message(response.errorBody(), fallback))
        }

        val body = response.body() ?: throw AppError("The server returned an empty login response.")
        if (body.token.isBlank()) throw AppError("The server did not return an access token.")
        sessionRepository.saveSession(body.token, body.agentName, body.role)
    }
}
