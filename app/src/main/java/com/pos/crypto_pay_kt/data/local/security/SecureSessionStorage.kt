package com.pos.crypto_pay_kt.data.local.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import dagger.hilt.android.qualifiers.ApplicationContext
import java.nio.ByteBuffer
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecureSessionStorage @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply { load(null) }

    @Synchronized
    fun write(key: String, value: String) {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
        val encrypted = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
        val packed = ByteBuffer.allocate(Int.SIZE_BYTES + cipher.iv.size + encrypted.size)
            .putInt(cipher.iv.size)
            .put(cipher.iv)
            .put(encrypted)
            .array()
        preferences.edit().putString(key, Base64.encodeToString(packed, Base64.NO_WRAP)).apply()
    }

    @Synchronized
    fun read(key: String): String? {
        val stored = preferences.getString(key, null) ?: return null
        return runCatching {
            val packed = ByteBuffer.wrap(Base64.decode(stored, Base64.NO_WRAP))
            val ivSize = packed.int
            require(ivSize in 12..16)
            val iv = ByteArray(ivSize)
            packed.get(iv)
            val encrypted = ByteArray(packed.remaining())
            packed.get(encrypted)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), GCMParameterSpec(128, iv))
            cipher.doFinal(encrypted).toString(Charsets.UTF_8)
        }.getOrElse {
            preferences.edit().remove(key).apply()
            null
        }
    }

    fun remove(key: String) {
        preferences.edit().remove(key).apply()
    }

    fun clearSession() {
        preferences.edit()
            .remove(ACCESS_TOKEN)
            .remove(AGENT_NAME)
            .remove(ROLE)
            .remove(BACKGROUNDED_AT)
            .apply()
    }

    private fun getOrCreateSecretKey(): SecretKey {
        (keyStore.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE).run {
            init(
                KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(true)
                    .build(),
            )
            generateKey()
        }
    }

    companion object {
        const val ACCESS_TOKEN = "access_token"
        const val AGENT_NAME = "agent_name"
        const val ROLE = "role"
        const val BACKGROUNDED_AT = "session_backgrounded_at"

        private const val PREFERENCES_NAME = "crypto_pay_secure_session"
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "crypto_pay_session_key"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
    }
}
