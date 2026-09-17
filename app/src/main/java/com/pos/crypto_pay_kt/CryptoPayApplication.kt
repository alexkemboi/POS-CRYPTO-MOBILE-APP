package com.pos.crypto_pay_kt

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.pos.crypto_pay_kt.core.session.SessionLifecycleObserver
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class CryptoPayApplication : Application() {
    @Inject
    lateinit var sessionLifecycleObserver: SessionLifecycleObserver

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(sessionLifecycleObserver)
    }
}
