package com.pos.crypto_pay_kt.domain.model

enum class AppLanguage(val languageTag: String) {
    ENGLISH("en"),
    SWAHILI("sw");

    companion object {
        fun fromLanguageTag(value: String?): AppLanguage =
            entries.firstOrNull { it.languageTag == value } ?: ENGLISH
    }
}
