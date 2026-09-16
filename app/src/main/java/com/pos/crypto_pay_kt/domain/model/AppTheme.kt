package com.pos.crypto_pay_kt.domain.model

enum class AppTheme(val storedValue: String) {
    LIGHT("light"),
    DARK("dark"),
    SYSTEM("system");

    companion object {
        fun fromStoredValue(value: String?): AppTheme =
            entries.firstOrNull { it.storedValue == value } ?: LIGHT
    }
}
