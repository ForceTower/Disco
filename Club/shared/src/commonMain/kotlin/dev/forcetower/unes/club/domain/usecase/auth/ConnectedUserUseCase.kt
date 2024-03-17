package dev.forcetower.unes.club.domain.usecase.auth

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import dev.forcetower.unes.club.domain.repository.local.AccessRepository

class ConnectedUserUseCase(
    private val repository: AccessRepository
) {
    @NativeCoroutines
    suspend fun hasAccess() = repository.requireCurrentAccess() != null
}