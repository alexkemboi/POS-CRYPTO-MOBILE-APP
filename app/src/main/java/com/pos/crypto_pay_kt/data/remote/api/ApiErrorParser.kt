package com.pos.crypto_pay_kt.data.remote.api

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.ResponseBody

@Singleton
class ApiErrorParser @Inject constructor(
    private val json: Json,
) {
    fun message(errorBody: ResponseBody?, fallback: String): String {
        val raw = runCatching { errorBody?.string() }.getOrNull().orEmpty()
        if (raw.isBlank()) return fallback
        return runCatching {
            val body = json.parseToJsonElement(raw).jsonObject
            body["error"]?.jsonPrimitive?.content
                ?.takeIf(String::isNotBlank)
                ?: body["message"]?.jsonPrimitive?.content
                    ?.takeIf(String::isNotBlank)
                ?: fallback
        }.getOrDefault(fallback)
    }
}
