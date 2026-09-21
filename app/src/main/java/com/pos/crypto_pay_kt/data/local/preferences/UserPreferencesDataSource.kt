package com.pos.crypto_pay_kt.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.pos.crypto_pay_kt.domain.model.AppLanguage
import com.pos.crypto_pay_kt.domain.model.AppTheme
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

@Singleton
class UserPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val theme: Flow<AppTheme> = dataStore.data
        .catch { error ->
            if (error is IOException) {
                emit(emptyPreferences())
            } else {
                throw error
            }
        }
        .map { preferences ->
            AppTheme.fromStoredValue(preferences[THEME_KEY])
        }

    val language: Flow<AppLanguage> = dataStore.data
        .catchIoErrors()
        .map { preferences ->
            AppLanguage.fromLanguageTag(preferences[LANGUAGE_KEY])
        }

    val isChargeIntroDismissed: Flow<Boolean> = dataStore.data
        .catchIoErrors()
        .map { preferences -> preferences[CHARGE_INTRO_DISMISSED_KEY] ?: false }

    suspend fun setTheme(theme: AppTheme) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.storedValue
        }
    }

    suspend fun setLanguage(language: AppLanguage) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language.languageTag
        }
    }

    suspend fun dismissChargeIntro() {
        dataStore.edit { preferences ->
            preferences[CHARGE_INTRO_DISMISSED_KEY] = true
        }
    }

    private fun Flow<Preferences>.catchIoErrors(): Flow<Preferences> =
        catch { error ->
            if (error is IOException) {
                emit(emptyPreferences())
            } else {
                throw error
            }
        }

    private companion object {
        val THEME_KEY = stringPreferencesKey("theme")
        val LANGUAGE_KEY = stringPreferencesKey("language")
        val CHARGE_INTRO_DISMISSED_KEY = booleanPreferencesKey("charge_intro_dismissed")
    }
}
