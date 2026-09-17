package com.pos.crypto_pay_kt.core.session

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.pos.crypto_pay_kt.domain.repository.SessionRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Singleton
class SessionLifecycleObserver @Inject constructor(
    private val sessionRepository: SessionRepository,
) : DefaultLifecycleObserver {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onStart(owner: LifecycleOwner) {
        scope.launch {
            sessionRepository.expireIfInactive(System.currentTimeMillis())
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        scope.launch {
            sessionRepository.recordBackgroundedAt(System.currentTimeMillis())
        }
    }
}
