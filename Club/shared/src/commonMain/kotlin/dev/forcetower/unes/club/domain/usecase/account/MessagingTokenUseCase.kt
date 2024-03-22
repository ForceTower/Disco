package dev.forcetower.unes.club.domain.usecase.account

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import dev.forcetower.unes.club.domain.repository.remote.edge.AccountRepository

class MessagingTokenUseCase internal constructor(
    private val repository: AccountRepository
) {
    @NativeCoroutines
    suspend fun sendFcmTokenIfNeeded(token: String) {
        repository.registerMessagingTokenIfNeeded(token)
    }
}