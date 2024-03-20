package dev.forcetower.unes.club.domain.usecase.auth

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import dev.forcetower.unes.club.domain.model.auth.ServiceAuthResult
import dev.forcetower.unes.club.domain.repository.remote.edge.AuthRepository

class ServiceAuthUseCase internal constructor(
    private val auth: AuthRepository
) {
    @NativeCoroutines
    suspend fun handshake(): ServiceAuthResult {
        return auth.handshake()
    }
}