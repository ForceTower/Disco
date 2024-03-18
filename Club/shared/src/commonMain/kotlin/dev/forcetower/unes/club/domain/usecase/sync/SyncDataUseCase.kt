package dev.forcetower.unes.club.domain.usecase.sync

import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import dev.forcetower.unes.club.domain.repository.local.SyncRepository

class SyncDataUseCase(
    private val repository: SyncRepository
) {
    @NativeCoroutines
    suspend fun execute(loadDetails: Boolean) = repository.sync(loadDetails)
}